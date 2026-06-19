package collab.report.controller;

import collab.report.dto.CollaborativePricelistLifecycleReportDto;
import collab.report.dto.CollaborativePricelistReportFilters;
import collab.report.service.CollaborativePricelistLifecycleReportPdfGenerator;
import collab.report.service.CollaborativePricelistLifecycleReportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final CollaborativePricelistLifecycleReportService reportService;
    private final CollaborativePricelistLifecycleReportPdfGenerator pdfGenerator;

    public ReportController(CollaborativePricelistLifecycleReportService reportService,
                            CollaborativePricelistLifecycleReportPdfGenerator pdfGenerator) {
        this.reportService = reportService;
        this.pdfGenerator = pdfGenerator;
    }

    @GetMapping(value = "/collaborative-pricelist-lifecycle/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateCollaborativePricelistLifecycleReport(
            @RequestParam(required = false) String teamId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String pricelistId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        CollaborativePricelistReportFilters filters = filters(teamId, region, status, pricelistId, from, to);
        CollaborativePricelistLifecycleReportDto report = reportService.buildReport(filters);
        byte[] pdf = pdfGenerator.generate(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("collaborative-pricelist-lifecycle-report.pdf")
                .build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    private CollaborativePricelistReportFilters filters(String teamId,
                                                       String region,
                                                       String status,
                                                       String pricelistId,
                                                       String from,
                                                       String to) {
        CollaborativePricelistReportFilters filters = new CollaborativePricelistReportFilters();
        filters.setTeamId(teamId);
        filters.setRegion(region);
        filters.setStatus(status);
        filters.setPricelistId(pricelistId);
        filters.setFrom(from);
        filters.setTo(to);
        return filters;
    }
}
