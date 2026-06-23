package timeseries.service;

import org.springframework.stereotype.Service;
import timeseries.dto.ActivationDurationResponse;
import timeseries.dto.AverageActivationTimeResponse;
import timeseries.dto.PricelistLifecycleEventResponse;
import timeseries.dto.PricelistLifecycleSummaryResponse;
import timeseries.repository.PriceListLifecycleRepository;
import timeseries.model.PriceListLifecycleAggregate;
import timeseries.model.PriceListLifecycleEvent;

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
public class PriceListLifecycleService {

    private final PriceListLifecycleRepository priceListLifecycleRepository;

    public PriceListLifecycleService(PriceListLifecycleRepository priceListLifecycleRepository) {
        this.priceListLifecycleRepository = priceListLifecycleRepository;
    }

    public boolean save(PriceListLifecycleEvent event) {
        return priceListLifecycleRepository.save(event);
    }

    public List<PriceListLifecycleEvent> findAll() {
        return priceListLifecycleRepository.findAll();
    }

    public List<PriceListLifecycleEvent> findAllByTeamId(String teamId) {
        return priceListLifecycleRepository.findAllByTeamId(teamId);
    }

    public List<PriceListLifecycleEvent> findAllByUserId(String userId) {
        return priceListLifecycleRepository.findAllByUserId(userId);
    }

    public List<PricelistLifecycleEventResponse> findLifecycleEvents(String pricelistId,
                                                                     String userId,
                                                                     String teamId,
                                                                     String operationType,
                                                                     String statusFrom,
                                                                     String statusTo,
                                                                     String from,
                                                                     String to) {
        return priceListLifecycleRepository
                .findByFilters(pricelistId, userId, teamId, operationType, statusFrom, statusTo, from, to)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PricelistLifecycleEventResponse> findLifecycleEventsByPricelistId(String pricelistId) {
        return priceListLifecycleRepository.findByPricelistId(pricelistId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PricelistLifecycleSummaryResponse lifecycleSummary() {
        List<PriceListLifecycleEvent> events = priceListLifecycleRepository.findByFilters(
                null, null, null, null, null, null, null, null);
        PricelistLifecycleSummaryResponse summary = new PricelistLifecycleSummaryResponse();
        summary.setTotalEvents(events.size());
        summary.setCreatedCount(events.stream().filter(event -> "CREATED".equalsIgnoreCase(event.getOperationType())).count());
        summary.setStatusChangedCount(events.stream().filter(event -> "STATUS_CHANGED".equalsIgnoreCase(event.getOperationType())).count());
        summary.setCompletedCount(events.stream().filter(event -> event.getSuccess() == null || event.getSuccess()).count());
        summary.setFailedCount(events.stream().filter(event -> event.getSuccess() != null && !event.getSuccess()).count());
        summary.setAverageDurationMs(events.stream()
                .map(PriceListLifecycleEvent::getDurationMs)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0));
        return summary;
    }

    public List<AverageActivationTimeResponse> averageActivationTime() {
        List<PriceListLifecycleEvent> events = priceListLifecycleRepository.findActivationEvents();
        Map<String, ActivationStart> startsByPricelist = new HashMap<>();
        Map<String, List<Long>> durationsByGroup = new HashMap<>();

        events.stream()
                .filter(event -> event.getTimestamp() != null)
                .sorted(Comparator.comparing(PriceListLifecycleEvent::getTimestamp))
                .forEach(event -> {
                    String pricelistId = firstNonBlank(event.getPricelistId(), event.getPriceListId());
                    if (pricelistId == null || pricelistId.isBlank()) {
                        return;
                    }
                    String statusTo = firstNonBlank(event.getStatusToTag(), event.getStatusTo());
                    boolean startsActivation = "CREATED".equalsIgnoreCase(event.getOperationType())
                            || "DRAFT".equalsIgnoreCase(statusTo);
                    if (startsActivation) {
                        startsByPricelist.putIfAbsent(pricelistId, new ActivationStart(event.getTimestamp(), event.getTeamId(), event.getRegion()));
                        return;
                    }
                    if (!"ACTIVE".equalsIgnoreCase(statusTo)) {
                        return;
                    }
                    ActivationStart start = startsByPricelist.get(pricelistId);
                    if (start == null || event.getTimestamp().isBefore(start.timestamp())) {
                        return;
                    }
                    String teamId = firstNonBlank(event.getTeamId(), start.teamId(), "none");
                    String region = firstNonBlank(event.getRegion(), start.region(), "unknown");
                    String key = teamId + "|" + region;
                    durationsByGroup.computeIfAbsent(key, ignored -> new ArrayList<>())
                            .add(Duration.between(start.timestamp(), event.getTimestamp()).toMillis());
                });

        return durationsByGroup.entrySet().stream()
                .map(entry -> {
                    String[] keyParts = entry.getKey().split("\\|", 2);
                    double averageMs = entry.getValue().stream()
                            .mapToLong(Long::longValue)
                            .average()
                            .orElse(0.0);
                    return new AverageActivationTimeResponse(keyParts[0], keyParts[1], entry.getValue().size(), averageMs);
                })
                .sorted(Comparator.comparing(AverageActivationTimeResponse::getTeamId)
                        .thenComparing(AverageActivationTimeResponse::getRegion))
                .collect(Collectors.toList());
    }

    public List<ActivationDurationResponse> activationDurations(String teamId, String from, String to) {
        List<PriceListLifecycleEvent> events = priceListLifecycleRepository.findByFilters(
                null, null, teamId, null, null, null, from, to);
        Map<String, ActivationStart> startsByPricelist = new HashMap<>();
        List<ActivationDurationResponse> result = new ArrayList<>();

        events.stream()
                .filter(event -> event.getTimestamp() != null)
                .sorted(Comparator.comparing(PriceListLifecycleEvent::getTimestamp))
                .forEach(event -> {
                    String pricelistId = firstNonBlank(event.getPricelistId(), event.getPriceListId());
                    if (pricelistId == null || pricelistId.isBlank()) {
                        return;
                    }
                    String statusTo = firstNonBlank(event.getStatusToTag(), event.getStatusTo());
                    boolean startsActivation = "CREATED".equalsIgnoreCase(event.getOperationType())
                            || "DRAFT".equalsIgnoreCase(statusTo);
                    if (startsActivation) {
                        startsByPricelist.putIfAbsent(pricelistId, new ActivationStart(event.getTimestamp(), event.getTeamId(), event.getRegion()));
                        return;
                    }
                    if (!"ACTIVE".equalsIgnoreCase(statusTo)) {
                        return;
                    }
                    ActivationStart start = startsByPricelist.get(pricelistId);
                    if (start == null || event.getTimestamp().isBefore(start.timestamp())) {
                        return;
                    }
                    result.add(new ActivationDurationResponse(
                            pricelistId,
                            firstNonBlank(event.getTeamId(), start.teamId(), "none"),
                            firstNonBlank(event.getRegion(), start.region(), "unknown"),
                            Duration.between(start.timestamp(), event.getTimestamp()).toMillis()));
                });
        return result;
    }

    public List<PriceListLifecycleAggregate> averageDraftDurationByTeam() {
        return priceListLifecycleRepository.averageDailyTeamSpeed();
    }

    public List<PriceListLifecycleAggregate> activityByUserBetween(String userId, String start, String stop) {
        return priceListLifecycleRepository.activityByUserBetween(userId, start, stop);
    }

    public List<PriceListLifecycleAggregate> slowestPriceListsAboveAverageReviewTime() {
        return priceListLifecycleRepository.slowestPriceListsAboveAverageReviewTime();
    }

    public boolean deleteRecord(String priceListId) {
        return priceListLifecycleRepository.delete(priceListId);
    }

    public int seed(int count) {
        return priceListLifecycleRepository.seed(count);
    }

    private PricelistLifecycleEventResponse toResponse(PriceListLifecycleEvent event) {
        PricelistLifecycleEventResponse response = new PricelistLifecycleEventResponse();
        response.setSagaId(event.getSagaId());
        response.setPricelistId(firstNonBlank(event.getPricelistId(), event.getPriceListId()));
        response.setUserId(event.getUserId());
        response.setTeamId(event.getTeamId());
        response.setRegion(event.getRegion());
        response.setOperationType(event.getOperationType());
        response.setStatusFrom(firstNonBlank(event.getStatusFromTag(), event.getStatusFrom()));
        response.setStatusTo(firstNonBlank(event.getStatusToTag(), event.getStatusTo()));
        response.setDurationMs(event.getDurationMs());
        response.setSuccess(event.getSuccess());
        response.setErrorMessage(event.getErrorMessage());
        response.setTimestamp(event.getTimestamp());
        return response;
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    private String firstNonBlank(String first, String second, String fallback) {
        String value = firstNonBlank(first, second);
        return value == null || value.isBlank() ? fallback : value;
    }

    private record ActivationStart(Instant timestamp, String teamId, String region) {
    }
}
