package collab.report.service;

import collab.report.dto.ActivationPerformanceRow;
import collab.report.dto.CollaborativePricelistLifecycleReportDto;
import collab.report.dto.CollaborativePricelistReportFilters;
import collab.report.dto.CollaborativePricelistSummaryRow;
import collab.report.dto.LifecycleEventRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CollaborativePricelistLifecycleReportService {

    private final CollaborativePricelistReportNeo4jReader reportNeo4jReader;
    private final RestTemplate restTemplate;
    private final String timeseriesServiceUrl;

    public CollaborativePricelistLifecycleReportService(
            CollaborativePricelistReportNeo4jReader reportNeo4jReader,
            @Value("${app.timeseries-service-url:http://timeseries-service:8080}") String timeseriesServiceUrl) {
        this.reportNeo4jReader = reportNeo4jReader;
        this.timeseriesServiceUrl = timeseriesServiceUrl;
        this.restTemplate = new RestTemplate();
    }

    public CollaborativePricelistLifecycleReportDto buildReport(CollaborativePricelistReportFilters filters) {
        CollaborativePricelistLifecycleReportDto report = new CollaborativePricelistLifecycleReportDto();
        report.setGeneratedAt(Instant.now());
        report.setFilters(filters);

        List<CollaborativePricelistSummaryRow> pricelists = loadPricelists(filters);
        report.setPricelists(pricelists);
        if (pricelists.isEmpty()) {
            report.getWarnings().add("No Neo4j pricelists found for selected filters.");
        }

        List<LifecycleEventRow> events = loadLifecycleEvents(filters, report.getWarnings());
        report.setLifecycleEvents(events);
        if (events.isEmpty()) {
            report.getWarnings().add("No lifecycle events found for selected filters.");
        }

        report.setActivationPerformance(calculateActivationPerformance(events, pricelists));
        if (report.getActivationPerformance().isEmpty()) {
            report.getWarnings().add("No activation performance data available for selected filters.");
        }

        return report;
    }

    public List<CollaborativePricelistSummaryRow> loadPricelists(CollaborativePricelistReportFilters filters) {
        return reportNeo4jReader.findPricelistReportRows(filters);
    }

    private List<LifecycleEventRow> loadLifecycleEvents(CollaborativePricelistReportFilters filters, List<String> warnings) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(timeseriesServiceUrl)
                    .path("/timeseries/pricelist-lifecycle/report-data");
            addQueryParam(builder, "pricelistId", filters.getPricelistId());
            addQueryParam(builder, "teamId", filters.getTeamId());
            addQueryParam(builder, "from", filters.getFrom());
            addQueryParam(builder, "to", filters.getTo());
            LifecycleEventRow[] rows = restTemplate.getForObject(builder.toUriString(), LifecycleEventRow[].class);
            if (rows == null) {
                return new ArrayList<>();
            }
            return java.util.Arrays.stream(rows)
                    .sorted(Comparator.comparing(LifecycleEventRow::getTime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            warnings.add("Invalid date or URL format for Timeseries query: " + ex.getMessage());
            return new ArrayList<>();
        } catch (RestClientException ex) {
            warnings.add("TimeseriesDatabaseService unavailable or returned an error: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    private List<ActivationPerformanceRow> calculateActivationPerformance(
            List<LifecycleEventRow> events,
            List<CollaborativePricelistSummaryRow> pricelists) {
        Map<String, CollaborativePricelistSummaryRow> metadataByPricelist = pricelists.stream()
                .filter(row -> row.getPricelistId() != null)
                .collect(Collectors.toMap(CollaborativePricelistSummaryRow::getPricelistId, row -> row, (left, right) -> left));
        Map<String, ActivationStart> startsByPricelist = new HashMap<>();
        Map<String, List<Long>> durationsByGroup = new HashMap<>();

        events.stream()
                .filter(event -> event.getTime() != null && event.getPricelistId() != null)
                .sorted(Comparator.comparing(LifecycleEventRow::getTime))
                .forEach(event -> {
                    String statusTo = event.getStatusTo();
                    boolean startEvent = "CREATED".equalsIgnoreCase(event.getOperationType())
                            || "DRAFT".equalsIgnoreCase(statusTo);
                    if (startEvent) {
                        startsByPricelist.putIfAbsent(event.getPricelistId(), new ActivationStart(event.getTime()));
                        return;
                    }
                    if (!"ACTIVE".equalsIgnoreCase(statusTo)) {
                        return;
                    }
                    ActivationStart start = startsByPricelist.get(event.getPricelistId());
                    if (start == null || event.getTime().isBefore(start.timestamp())) {
                        return;
                    }
                    CollaborativePricelistSummaryRow metadata = metadataByPricelist.get(event.getPricelistId());
                    String teamId = firstNonBlank(event.getTeamId(), metadata == null ? null : metadata.getTeamId(), "none");
                    String region = firstNonBlank(metadata == null ? null : metadata.getRegion(), "unknown");
                    String key = teamId + "|" + region;
                    durationsByGroup.computeIfAbsent(key, ignored -> new ArrayList<>())
                            .add(Duration.between(start.timestamp(), event.getTime()).toMillis());
                });

        return durationsByGroup.entrySet().stream()
                .map(entry -> toActivationPerformanceRow(entry.getKey(), entry.getValue(), pricelists))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ActivationPerformanceRow::getAverageActivationTimeHours))
                .collect(Collectors.toList());
    }

    private ActivationPerformanceRow toActivationPerformanceRow(String key,
                                                                List<Long> durations,
                                                                List<CollaborativePricelistSummaryRow> pricelists) {
        if (durations.isEmpty()) {
            return null;
        }
        String[] parts = key.split("\\|", 2);
        String teamId = parts[0];
        String region = parts.length > 1 ? parts[1] : "unknown";
        double averageMs = durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double fastestHours = durations.stream().mapToLong(Long::longValue).min().orElse(0L) / 3_600_000.0;
        double slowestHours = durations.stream().mapToLong(Long::longValue).max().orElse(0L) / 3_600_000.0;
        String teamName = pricelists.stream()
                .filter(row -> teamId.equals(row.getTeamId()))
                .map(CollaborativePricelistSummaryRow::getTeamName)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElse(teamId);

        ActivationPerformanceRow row = new ActivationPerformanceRow();
        row.setTeamId(teamId);
        row.setTeamName(teamName);
        row.setRegion(region);
        row.setActivatedPricelistCount(durations.size());
        row.setAverageActivationTimeMs(averageMs);
        row.setFastestActivationHours(fastestHours);
        row.setSlowestActivationHours(slowestHours);
        return row;
    }

    private void addQueryParam(UriComponentsBuilder builder, String name, String value) {
        if (value != null && !value.isBlank()) {
            builder.queryParam(name, value);
        }
    }

    private String firstNonBlank(String first, String fallback) {
        return first == null || first.isBlank() ? fallback : first;
    }

    private String firstNonBlank(String first, String second, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return firstNonBlank(second, fallback);
    }

    private record ActivationStart(Instant timestamp) {
    }
}
