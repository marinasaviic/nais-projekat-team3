package collab.report.service;

import collab.report.dto.ActivationPerformanceRow;
import collab.report.dto.CollaborativePricelistLifecycleReportDto;
import collab.report.dto.CollaborativePricelistSummaryRow;
import collab.report.dto.LifecycleEventRow;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CollaborativePricelistLifecycleReportPdfGenerator {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font SECTION_FONT = new Font(Font.HELVETICA, 13, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 9);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 8, Font.BOLD);
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 7);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault());

    public byte[] generate(CollaborativePricelistLifecycleReportDto report) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 28, 28, 28, 28);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addTitleSection(document, report);
            addPricelistSection(document, report.getPricelists());
            addLifecycleSection(document, report.getLifecycleEvents());
            addActivationSection(document, report.getActivationPerformance());
            addChartSection(document, report.getActivationPerformance());

            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate collaborative pricelist lifecycle report PDF", ex);
        }
    }

    private void addTitleSection(Document document, CollaborativePricelistLifecycleReportDto report) throws Exception {
        Paragraph title = new Paragraph("Collaborative Pricelist Lifecycle Report", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Generated at: " + DATE_FORMATTER.format(report.getGeneratedAt()), NORMAL_FONT));
        document.add(new Paragraph("Selected filters: " + filtersText(report), NORMAL_FONT));
        document.add(new Paragraph("This report combines current graph data from Neo4j with lifecycle time-series events from InfluxDB.", NORMAL_FONT));
        if (!report.getWarnings().isEmpty()) {
            document.add(new Paragraph("Warnings: " + String.join(" | ", report.getWarnings()), NORMAL_FONT));
        }
        addSpacing(document);
    }

    private void addPricelistSection(Document document, List<CollaborativePricelistSummaryRow> rows) throws Exception {
        document.add(new Paragraph("Collaborative Pricelists (Neo4j)", SECTION_FONT));
        if (rows.isEmpty()) {
            document.add(new Paragraph("No Neo4j pricelists found for selected filters.", NORMAL_FONT));
            addSpacing(document);
            return;
        }
        PdfPTable table = table(8);
        addHeaders(table, "Pricelist ID", "Name", "Region", "Team ID", "Team Name", "Status", "Creator", "Collaborators");
        for (CollaborativePricelistSummaryRow row : rows) {
            addCells(table,
                    value(row.getPricelistId()),
                    value(row.getName()),
                    value(row.getRegion()),
                    value(row.getTeamId()),
                    value(row.getTeamName()),
                    value(row.getCurrentStatus()),
                    value(row.getCreatorUserId()),
                    value(row.getNumberOfCollaborators()));
        }
        document.add(table);
        addSpacing(document);
    }

    private void addLifecycleSection(Document document, List<LifecycleEventRow> rows) throws Exception {
        document.add(new Paragraph("Lifecycle Events (InfluxDB)", SECTION_FONT));
        if (rows.isEmpty()) {
            document.add(new Paragraph("No lifecycle events found for selected filters.", NORMAL_FONT));
            addSpacing(document);
            return;
        }
        PdfPTable table = table(10);
        addHeaders(table, "Time", "Pricelist ID", "User ID", "Team ID", "Operation", "From", "To", "Duration ms", "Success", "Error");
        rows.stream().limit(40).forEach(row -> addCells(table,
                row.getTime() == null ? "N/A" : DATE_FORMATTER.format(row.getTime()),
                value(row.getPricelistId()),
                value(row.getUserId()),
                value(row.getTeamId()),
                value(row.getOperationType()),
                value(row.getStatusFrom()),
                value(row.getStatusTo()),
                value(row.getDurationMs()),
                value(row.getSuccess()),
                value(row.getErrorMessage())));
        document.add(table);
        if (rows.size() > 40) {
            document.add(new Paragraph("Showing first 40 lifecycle events out of " + rows.size() + ".", SMALL_FONT));
        }
        addSpacing(document);
    }

    private void addActivationSection(Document document, List<ActivationPerformanceRow> rows) throws Exception {
        document.add(new Paragraph("Activation Performance by Team and Region (Neo4j + InfluxDB)", SECTION_FONT));
        if (rows.isEmpty()) {
            document.add(new Paragraph("No activation data available for selected filters.", NORMAL_FONT));
            addSpacing(document);
            return;
        }
        PdfPTable table = table(8);
        addHeaders(table, "Team ID", "Team Name", "Region", "Activated", "Avg ms", "Avg h", "Fastest h", "Slowest h");
        for (ActivationPerformanceRow row : rows) {
            addCells(table,
                    value(row.getTeamId()),
                    value(row.getTeamName()),
                    value(row.getRegion()),
                    value(row.getActivatedPricelistCount()),
                    number(row.getAverageActivationTimeMs()),
                    number(row.getAverageActivationTimeHours()),
                    number(row.getFastestActivationHours()),
                    number(row.getSlowestActivationHours()));
        }
        document.add(table);
        addSpacing(document);
    }

    private void addChartSection(Document document, List<ActivationPerformanceRow> rows) throws Exception {
        document.add(new Paragraph("Average Activation Time by Team", SECTION_FONT));
        if (rows.isEmpty()) {
            document.add(new Paragraph("Chart cannot be generated because activation data is not available.", NORMAL_FONT));
            return;
        }
        Image chart = Image.getInstance(createChart(rows));
        chart.scaleToFit(620, 280);
        chart.setAlignment(Element.ALIGN_CENTER);
        document.add(chart);
    }

    private byte[] createChart(List<ActivationPerformanceRow> rows) throws IOException {
        int width = 900;
        int height = 360;
        int left = 70;
        int bottom = 70;
        int top = 40;
        int right = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawLine(left, height - bottom, width - right, height - bottom);
        graphics.drawLine(left, top, left, height - bottom);

        double max = rows.stream()
                .mapToDouble(ActivationPerformanceRow::getAverageActivationTimeHours)
                .max()
                .orElse(1.0);
        int barAreaWidth = width - left - right;
        int barAreaHeight = height - top - bottom;
        int barWidth = Math.max(28, barAreaWidth / Math.max(rows.size(), 1) - 18);

        for (int index = 0; index < rows.size(); index++) {
            ActivationPerformanceRow row = rows.get(index);
            int x = left + 12 + index * (barAreaWidth / Math.max(rows.size(), 1));
            int barHeight = (int) ((row.getAverageActivationTimeHours() / max) * (barAreaHeight - 20));
            int y = height - bottom - barHeight;
            graphics.setColor(new Color(52, 120, 190));
            graphics.fillRect(x, y, barWidth, barHeight);
            graphics.setColor(Color.DARK_GRAY);
            graphics.drawString(number(row.getAverageActivationTimeHours()) + "h", x, Math.max(top + 12, y - 6));
            graphics.drawString(shortLabel(firstNonBlank(row.getTeamName(), row.getTeamId())), x, height - bottom + 20);
        }

        graphics.setColor(Color.DARK_GRAY);
        graphics.drawString("Average activation time (hours)", left, 22);
        graphics.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    private PdfPTable table(int columns) {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100);
        table.getDefaultCell().setPadding(4);
        return table;
    }

    private void addHeaders(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(new java.awt.Color(230, 235, 242));
            cell.setPadding(4);
            table.addCell(cell);
        }
    }

    private void addCells(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, SMALL_FONT));
            cell.setPadding(4);
            table.addCell(cell);
        }
    }

    private void addSpacing(Document document) throws Exception {
        document.add(new Paragraph(" "));
    }

    private String filtersText(CollaborativePricelistLifecycleReportDto report) {
        if (report.getFilters() == null) {
            return "none";
        }
        return "teamId=" + value(report.getFilters().getTeamId())
                + ", region=" + value(report.getFilters().getRegion())
                + ", status=" + value(report.getFilters().getStatus())
                + ", pricelistId=" + value(report.getFilters().getPricelistId())
                + ", from=" + value(report.getFilters().getFrom())
                + ", to=" + value(report.getFilters().getTo());
    }

    private String value(Object value) {
        return value == null ? "N/A" : String.valueOf(value);
    }

    private String number(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String shortLabel(String value) {
        if (value == null) {
            return "N/A";
        }
        return value.length() <= 14 ? value : value.substring(0, 14);
    }

    private String firstNonBlank(String first, String second) {
        return first == null || first.isBlank() ? second : first;
    }
}
