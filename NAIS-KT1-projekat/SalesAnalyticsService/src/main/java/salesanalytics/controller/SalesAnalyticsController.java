package salesanalytics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salesanalytics.dto.RedisSalesEventView;
import salesanalytics.dto.TransactionalSalesEventResponse;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;
import salesanalytics.service.SalesAnalyticsService;
import salesanalytics.service.SalesAnalyticsReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sales-analytics")
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;
    private final SalesAnalyticsReportService reportService;

    public SalesAnalyticsController(SalesAnalyticsService salesAnalyticsService,
                                    SalesAnalyticsReportService reportService) {
        this.salesAnalyticsService = salesAnalyticsService;
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<List<SalesProcessEvent>> findAll() {
        return new ResponseEntity<>(salesAnalyticsService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/sales-rep")
    public ResponseEntity<List<SalesProcessEvent>> findAllBySalesRepId(@RequestParam String salesRepId) {
        return new ResponseEntity<>(salesAnalyticsService.findAllBySalesRepId(salesRepId), HttpStatus.OK);
    }

    @GetMapping("/region")
    public ResponseEntity<List<SalesProcessEvent>> findAllByRegion(@RequestParam String region) {
        return new ResponseEntity<>(salesAnalyticsService.findAllByRegion(region), HttpStatus.OK);
    }

    @GetMapping("/analytics/top-negotiation-pipeline")
    public ResponseEntity<List<SalesAnalyticsAggregate>> topSalesRepsByNegotiationPipeline() {
        return new ResponseEntity<>(salesAnalyticsService.topSalesRepsByNegotiationPipeline(), HttpStatus.OK);
    }

    @GetMapping("/analytics/stage-bottlenecks")
    public ResponseEntity<List<SalesAnalyticsAggregate>> stageBottlenecks() {
        return new ResponseEntity<>(salesAnalyticsService.stageBottlenecks(), HttpStatus.OK);
    }

    @GetMapping("/analytics/weekly-pipeline-growth")
    public ResponseEntity<List<SalesAnalyticsAggregate>> weeklyPipelineGrowthByRegion() {
        return new ResponseEntity<>(salesAnalyticsService.weeklyPipelineGrowthByRegion(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Boolean> save(@RequestBody SalesProcessEvent event) {
        return salesAnalyticsService.save(event)
                ? new ResponseEntity<>(true, HttpStatus.CREATED)
                : new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/transactions/events")
    public ResponseEntity<TransactionalSalesEventResponse> createTransactional(
            @RequestBody SalesProcessEvent event,
            @RequestParam(defaultValue = "false") boolean simulateRedisFailure) {
        return new ResponseEntity<>(
                salesAnalyticsService.createTransactional(event, simulateRedisFailure),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/transactions/redis-events")
    public ResponseEntity<List<RedisSalesEventView>> latestRedisEvents(@RequestParam(defaultValue = "20") int limit) {
        return new ResponseEntity<>(salesAnalyticsService.latestRedisEvents(limit), HttpStatus.OK);
    }

    @GetMapping("/transactions/redis-region-counts")
    public ResponseEntity<Map<String, Long>> redisRegionCounts() {
        return new ResponseEntity<>(salesAnalyticsService.redisRegionCounts(), HttpStatus.OK);
    }

    @GetMapping("/reports/pipeline.pdf")
    public ResponseEntity<byte[]> pipelineReport(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "20") int limit) {
        byte[] report = reportService.generatePipelineReport(region, limit);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales-pipeline-report.pdf")
                .body(report);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestParam String opportunityId) {
        return salesAnalyticsService.deleteRecord(opportunityId)
                ? new ResponseEntity<>(true, HttpStatus.OK)
                : new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/seed")
    public ResponseEntity<Integer> seed(@RequestParam(defaultValue = "2500") int count) {
        return new ResponseEntity<>(salesAnalyticsService.seed(count), HttpStatus.CREATED);
    }
}
