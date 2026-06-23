package rs.ac.uns.acs.nais.AdverseEffectsSearchService.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.dto.AdverseReportProjectionDto;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.dto.AdverseReportSagaResponse;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.AdverseEventReportDocument;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.AdverseEffectsFinalReportService;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.AdverseReportSagaService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/final-defense")
public class FinalDefenseController {
    private final AdverseReportSagaService sagaService;
    private final AdverseEffectsFinalReportService reportService;

    public FinalDefenseController(AdverseReportSagaService sagaService,
                                  AdverseEffectsFinalReportService reportService) {
        this.sagaService = sagaService;
        this.reportService = reportService;
    }

    @PostMapping("/adverse-reports/transactional")
    public ResponseEntity<AdverseReportSagaResponse> createTransactionalReport(
            @RequestBody AdverseEventReportDocument report,
            @RequestParam(defaultValue = "false") boolean simulateRedisFailure) throws IOException {
        return ResponseEntity.status(201).body(sagaService.createTransactional(report, simulateRedisFailure));
    }

    @GetMapping("/adverse-reports/redis-projections")
    public List<AdverseReportProjectionDto> latestRedisProjections(@RequestParam(defaultValue = "20") int limit) {
        return sagaService.latestRedisProjections(limit);
    }

    @GetMapping("/adverse-reports/redis-severity-counts")
    public Map<String, Long> redisSeverityCounts() {
        return sagaService.severityCounts();
    }

    @GetMapping("/adverse-reports/saga-states")
    public List<Map<String, String>> latestSagaStates(@RequestParam(defaultValue = "20") int limit) {
        return sagaService.latestSagaStates(limit);
    }

    @GetMapping("/adverse-effects-report.pdf")
    public ResponseEntity<byte[]> adverseEffectsReport(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "20") int limit) throws IOException {
        byte[] report = reportService.generateReport(region, limit);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=adverse-effects-final-report.pdf")
                .body(report);
    }
}
