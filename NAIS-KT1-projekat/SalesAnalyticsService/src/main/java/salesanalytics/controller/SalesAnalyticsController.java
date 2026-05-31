package salesanalytics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;
import salesanalytics.service.SalesAnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/sales-analytics")
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    public SalesAnalyticsController(SalesAnalyticsService salesAnalyticsService) {
        this.salesAnalyticsService = salesAnalyticsService;
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