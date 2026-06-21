package salesanalytics.service;

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
import salesanalytics.dto.RedisSalesEventView;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class SalesAnalyticsReportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneOffset.UTC);

    private final SalesAnalyticsService salesAnalyticsService;

    public SalesAnalyticsReportService(SalesAnalyticsService salesAnalyticsService) {
        this.salesAnalyticsService = salesAnalyticsService;
    }

    public byte[] generatePipelineReport(String region, int limit) {
        List<SalesProcessEvent> influxEvents = region == null || region.isBlank()
                ? salesAnalyticsService.findAll()
                : salesAnalyticsService.findAllByRegion(region);
        List<RedisSalesEventView> redisEvents = salesAnalyticsService.latestRedisEvents(limit);
        List<SalesAnalyticsAggregate> bottlenecks = salesAnalyticsService.stageBottlenecks();
        Map<String, Long> redisRegionCounts = salesAnalyticsService.redisRegionCounts();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addTitle(document, region);
            addSimpleInfluxSection(document, influxEvents, limit);
            addSimpleRedisSection(document, redisEvents);
            addComplexSection(document, bottlenecks, redisRegionCounts);
            addRegionChart(document, redisRegionCounts);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("PDF report generation failed.", ex);
        }
    }

    private void addTitle(Document document, String region) throws DocumentException {
        Paragraph title = new Paragraph("Sales Pipeline Operational Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
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

    private void addSimpleInfluxSection(Document document, List<SalesProcessEvent> events, int limit) throws DocumentException {
        addSectionHeading(document, "Simple section 1: InfluxDB sales events");
        PdfPTable table = new PdfPTable(new float[]{2.1f, 2.0f, 1.8f, 2.0f, 1.6f, 1.7f});
        table.setWidthPercentage(100);
        addHeader(table, "Opportunity", "Sales rep", "Region", "Stage", "Value", "Time UTC");

        events.stream()
                .limit(limit)
                .forEach(event -> addRow(table,
                        value(event.getOpportunityId()),
                        value(event.getSalesRepName()),
                        value(event.getRegion()),
                        value(event.getStageTo()),
                        number(event.getDealValue()),
                        date(event.getTimestamp())));

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addSimpleRedisSection(Document document, List<RedisSalesEventView> events) throws DocumentException {
        addSectionHeading(document, "Simple section 2: Redis transactional projections");
        PdfPTable table = new PdfPTable(new float[]{2.1f, 2.0f, 1.8f, 1.8f, 1.6f, 1.7f});
        table.setWidthPercentage(100);
        addHeader(table, "Opportunity", "Sales rep", "Region", "Stage", "Value", "Indexed UTC");

        events.forEach(event -> addRow(table,
                value(event.getOpportunityId()),
                value(event.getSalesRepName()),
                value(event.getRegion()),
                value(event.getStageTo()),
                number(event.getDealValue()),
                date(event.getIndexedAt())));

        if (events.isEmpty()) {
            addEmptyRow(table, 6, "No Redis projections yet. Create one with /sales-analytics/transactions/events.");
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addComplexSection(Document document, List<SalesAnalyticsAggregate> bottlenecks,
                                   Map<String, Long> redisRegionCounts) throws DocumentException {
        addSectionHeading(document, "Complex section: InfluxDB stage bottlenecks enriched with Redis counters");
        PdfPTable table = new PdfPTable(new float[]{2.0f, 2.2f, 2.0f});
        table.setWidthPercentage(100);
        addHeader(table, "Stage", "Avg duration hours", "Redis events in same region");

        bottlenecks.stream()
                .sorted(Comparator.comparing(SalesAnalyticsAggregate::getValue, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .forEach(aggregate -> addRow(table,
                        value(aggregate.getGroupKey()),
                        number(aggregate.getValue()),
                        redisRegionCounts.isEmpty() ? "0" : redisRegionCounts.toString()));

        if (bottlenecks.isEmpty()) {
            addEmptyRow(table, 3, "No InfluxDB analytics yet. Seed data with /sales-analytics/seed.");
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addRegionChart(Document document, Map<String, Long> regionCounts) throws DocumentException {
        addSectionHeading(document, "Chart: Redis transactional events by region");
        PdfPTable chart = new PdfPTable(new float[]{1.5f, 5.0f, 1.0f});
        chart.setWidthPercentage(100);
        addHeader(chart, "Region", "Bar", "Count");

        long max = regionCounts.values().stream().mapToLong(Long::longValue).max().orElse(1L);
        if (regionCounts.isEmpty()) {
            addEmptyRow(chart, 3, "No Redis counters yet.");
        } else {
            regionCounts.forEach((region, count) -> {
                chart.addCell(cell(region, false));
                chart.addCell(barCell(count, max));
                chart.addCell(cell(String.valueOf(count), false));
            });
        }

        document.add(chart);
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
        cell.setBackgroundColor(new Color(222, 239, 255));
        cell.setBorder(Rectangle.BOX);
        return cell;
    }

    private String value(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String number(Double value) {
        return value == null ? "-" : String.format("%.2f", value);
    }

    private String date(Instant instant) {
        return instant == null ? "-" : DATE_FORMAT.format(instant);
    }
}
