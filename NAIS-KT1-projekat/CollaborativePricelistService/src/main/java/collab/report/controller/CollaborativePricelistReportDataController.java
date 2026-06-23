package collab.report.controller;

import collab.report.dto.CollaborativePricelistReportFilters;
import collab.report.dto.CollaborativePricelistSummaryRow;
import collab.report.service.CollaborativePricelistLifecycleReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/collaborative-pricelists")
public class CollaborativePricelistReportDataController {

    private final CollaborativePricelistLifecycleReportService reportService;

    public CollaborativePricelistReportDataController(CollaborativePricelistLifecycleReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report-data")
    public ResponseEntity<List<CollaborativePricelistSummaryRow>> reportData(
            @RequestParam(required = false) String teamId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String pricelistId) {
        CollaborativePricelistReportFilters filters = new CollaborativePricelistReportFilters();
        filters.setTeamId(teamId);
        filters.setRegion(region);
        filters.setStatus(status);
        filters.setPricelistId(pricelistId);
        return ResponseEntity.ok(reportService.loadPricelists(filters));
    }
}
