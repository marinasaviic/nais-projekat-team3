package rs.ac.uns.acs.nais.AdverseEffectsSearchService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.dto.AdverseReportProjectionDto;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdverseEffectsFinalReportService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneOffset.UTC);

    private final ElasticsearchDocumentService elasticsearch;
    private final AdverseReportSagaService sagaService;

    public AdverseEffectsFinalReportService(ElasticsearchDocumentService elasticsearch,
                                            AdverseReportSagaService sagaService) {
        this.elasticsearch = elasticsearch;
        this.sagaService = sagaService;
    }

    public byte[] generateReport(String region, int limit) throws IOException {
        JsonNode elasticReports = elasticsearch.search(
                ElasticsearchDocumentService.REPORTS_INDEX,
                reportSearchBody(region, limit)
        );
        JsonNode complexAnalytics = elasticsearch.search(
                ElasticsearchDocumentService.REPORTS_INDEX,
                complexAnalyticsBody(region)
        );
        List<AdverseReportProjectionDto> redisProjections = sagaService.latestRedisProjections(limit);
        Map<String, Long> redisSeverityCounts = sagaService.severityCounts();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addTitle(document, region);
            addElasticsearchSimpleSection(document, elasticReports);
            addRedisSimpleSection(document, redisProjections);
            addComplexSection(document, complexAnalytics, redisSeverityCounts);
            addSeverityChart(document, redisSeverityCounts);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("PDF report generation failed.", ex);
        }
    }

    private Map<String, Object> reportSearchBody(String region, int limit) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("size", limit);
        body.put("sort", List.of(Map.of("eventDate", Map.of("order", "desc"))));
        if (region == null || region.isBlank()) {
            body.put("query", Map.of("match_all", Map.of()));
        } else {
            body.put("query", Map.of("term", Map.of("region", region)));
        }
        return body;
    }

    private Map<String, Object> complexAnalyticsBody(String region) {
        Map<String, Object> filters = new LinkedHashMap<>();
        if (region != null && !region.isBlank()) {
            filters.put("filter", List.of(Map.of("term", Map.of("region", region))));
        }

        Map<String, Object> query = filters.isEmpty()
                ? Map.of("match_all", Map.of())
                : Map.of("bool", filters);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("size", 0);
        body.put("query", query);
        body.put("aggs", Map.of(
                "severity_by_reaction", Map.of(
                        "terms", Map.of("field", "reactionType", "size", 8),
                        "aggs", Map.of(
                                "by_severity", Map.of("terms", Map.of("field", "severity")),
                                "hospitalizations", Map.of("terms", Map.of("field", "hospitalizationRequired")),
                                "avg_outcome_score", Map.of("avg", Map.of("field", "outcomeScore"))
                        )
                ),
                "by_region", Map.of("terms", Map.of("field", "region", "size", 8))
        ));
        return body;
    }

    private void addTitle(Document document, String region) throws DocumentException {
        Paragraph title = new Paragraph("Adverse Effects Safety Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        String subtitle = "Generated at " + DATE_FORMAT.format(Instant.now()) + " UTC";
        if (region != null && !region.isBlank()) {
            subtitle += " | Region filter: " + region;
        }
        Paragraph subtitleParagraph = new Paragraph(subtitle, FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY));
        subtitleParagraph.setAlignment(Element.ALIGN_CENTER);
        subtitleParagraph.setSpacingAfter(18);
        document.add(subtitleParagraph);
    }

    private void addElasticsearchSimpleSection(Document document, JsonNode response) throws DocumentException {
        addSectionHeading(document, "Simple section 1: Elasticsearch adverse event reports");
        PdfPTable table = new PdfPTable(new float[]{1.5f, 2.1f, 1.6f, 1.3f, 1.4f, 1.4f});
        table.setWidthPercentage(100);
        addHeader(table, "Report", "Drug", "Reaction", "Severity", "Region", "Event date");

        JsonNode hits = response.path("hits").path("hits");
        if (!hits.isArray() || hits.isEmpty()) {
            addEmptyRow(table, 6, "No Elasticsearch reports found.");
        } else {
            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                addRow(table,
                        text(source, "id"),
                        text(source, "drugName"),
                        text(source, "reactionType"),
                        text(source, "severity"),
                        text(source, "region"),
                        text(source, "eventDate"));
            }
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addRedisSimpleSection(Document document, List<AdverseReportProjectionDto> projections) throws DocumentException {
        addSectionHeading(document, "Simple section 2: Redis transactional report projections");
        PdfPTable table = new PdfPTable(new float[]{1.5f, 2.1f, 1.6f, 1.3f, 1.4f, 1.5f});
        table.setWidthPercentage(100);
        addHeader(table, "Report", "Drug", "Reaction", "Severity", "Region", "Indexed UTC");

        if (projections.isEmpty()) {
            addEmptyRow(table, 6, "No Redis projections yet. Create one with /api/final-defense/adverse-reports/transactional.");
        } else {
            for (AdverseReportProjectionDto projection : projections) {
                addRow(table,
                        value(projection.getReportId()),
                        value(projection.getDrugName()),
                        value(projection.getReactionType()),
                        value(projection.getSeverity()),
                        value(projection.getRegion()),
                        projection.getIndexedAt() == null ? "-" : DATE_FORMAT.format(projection.getIndexedAt()));
            }
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addComplexSection(Document document, JsonNode analytics, Map<String, Long> redisCounts) throws DocumentException {
        addSectionHeading(document, "Complex section: reaction severity analytics with Redis transaction counters");
        PdfPTable table = new PdfPTable(new float[]{2.0f, 1.2f, 2.5f, 1.6f});
        table.setWidthPercentage(100);
        addHeader(table, "Reaction", "Reports", "Severity buckets", "Avg outcome");

        JsonNode buckets = analytics.path("aggregations").path("severity_by_reaction").path("buckets");
        if (!buckets.isArray() || buckets.isEmpty()) {
            addEmptyRow(table, 4, "No complex Elasticsearch analytics found.");
        } else {
            for (JsonNode bucket : buckets) {
                addRow(table,
                        bucket.path("key").asText("-"),
                        String.valueOf(bucket.path("doc_count").asLong()),
                        severitySummary(bucket.path("by_severity").path("buckets"), redisCounts),
                        String.format("%.2f", bucket.path("avg_outcome_score").path("value").asDouble(0.0)));
            }
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addSeverityChart(Document document, Map<String, Long> counts) throws DocumentException {
        addSectionHeading(document, "Chart: Redis transactional reports by severity");
        PdfPTable chart = new PdfPTable(new float[]{1.4f, 5.0f, 1.0f});
        chart.setWidthPercentage(100);
        addHeader(chart, "Severity", "Bar", "Count");

        long max = counts.values().stream().mapToLong(Long::longValue).max().orElse(1L);
        if (counts.isEmpty()) {
            addEmptyRow(chart, 3, "No Redis severity counters yet.");
        } else {
            counts.forEach((severity, count) -> {
                chart.addCell(cell(severity, false));
                chart.addCell(barCell(count, max));
                chart.addCell(cell(String.valueOf(count), false));
            });
        }
        document.add(chart);
    }

    private String severitySummary(JsonNode buckets, Map<String, Long> redisCounts) {
        StringBuilder summary = new StringBuilder();
        if (buckets.isArray()) {
            for (JsonNode bucket : buckets) {
                if (summary.length() > 0) {
                    summary.append("; ");
                }
                String severity = bucket.path("key").asText("-");
                summary.append(severity)
                        .append(": ES ")
                        .append(bucket.path("doc_count").asLong())
                        .append(" / Redis ")
                        .append(redisCounts.getOrDefault(severity, 0L));
            }
        }
        return summary.length() == 0 ? "-" : summary.toString();
    }

    private void addSectionHeading(Document document, String text) throws DocumentException {
        Paragraph heading = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13));
        heading.setSpacingBefore(8);
        heading.setSpacingAfter(6);
        document.add(heading);
    }

    private void addHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            table.addCell(cell(header, true));
        }
    }

    private void addRow(PdfPTable table, String... values) {
        for (String value : values) {
            table.addCell(cell(value, false));
        }
    }

    private void addEmptyRow(PdfPTable table, int colspan, String message) {
        PdfPCell cell = cell(message, false);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private PdfPCell cell(String text, boolean header) {
        Font font = FontFactory.getFont(header ? FontFactory.HELVETICA_BOLD : FontFactory.HELVETICA, header ? 9 : 8);
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setPadding(5);
        cell.setBorderColor(new Color(210, 214, 220));
        if (header) {
            cell.setBackgroundColor(new Color(235, 239, 245));
        }
        return cell;
    }

    private PdfPCell barCell(long value, long max) {
        int width = (int) Math.max(1, Math.round((value * 30.0) / max));
        PdfPCell cell = cell("#".repeat(width), false);
        cell.setBackgroundColor(new Color(232, 242, 255));
        cell.setBorder(Rectangle.BOX);
        return cell;
    }

    private String text(JsonNode node, String field) {
        return node.path(field).isMissingNode() || node.path(field).isNull() ? "-" : node.path(field).asText();
    }

    private String value(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
