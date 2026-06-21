package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.dto.SalesPerformanceReportDto;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.SalesReportService;

@RestController
public class SalesReportController {

    private final SalesReportService salesReportService;

    public SalesReportController(SalesReportService salesReportService) {
        this.salesReportService = salesReportService;
    }

    @GetMapping("/sales-report")
    public SalesPerformanceReportDto generateSalesReport(
            @RequestParam(defaultValue = "Serbia") String region
    ) {
        return salesReportService.generateSalesPerformanceReport(region);
    }
}