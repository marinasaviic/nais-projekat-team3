package timeseries.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import timeseries.dto.AverageActivationTimeResponse;
import timeseries.dto.PricelistLifecycleEventResponse;
import timeseries.dto.PricelistLifecycleSummaryResponse;
import timeseries.service.PriceListLifecycleService;

import java.util.List;

@RestController
@RequestMapping("/timeseries/pricelist-lifecycle")
public class PricelistLifecycleQueryController {

    private final PriceListLifecycleService priceListLifecycleService;

    public PricelistLifecycleQueryController(PriceListLifecycleService priceListLifecycleService) {
        this.priceListLifecycleService = priceListLifecycleService;
    }

    @GetMapping
    public ResponseEntity<List<PricelistLifecycleEventResponse>> findLifecycleEvents(
            @RequestParam(required = false) String pricelistId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String teamId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String statusFrom,
            @RequestParam(required = false) String statusTo,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(priceListLifecycleService.findLifecycleEvents(
                pricelistId,
                userId,
                teamId,
                operationType,
                statusFrom,
                statusTo,
                from,
                to));
    }

    @GetMapping("/summary")
    public ResponseEntity<PricelistLifecycleSummaryResponse> summary() {
        return ResponseEntity.ok(priceListLifecycleService.lifecycleSummary());
    }

    @GetMapping("/average-activation-time")
    public ResponseEntity<List<AverageActivationTimeResponse>> averageActivationTime() {
        return ResponseEntity.ok(priceListLifecycleService.averageActivationTime());
    }

    @GetMapping("/{pricelistId}")
    public ResponseEntity<List<PricelistLifecycleEventResponse>> findByPricelistId(@PathVariable String pricelistId) {
        return ResponseEntity.ok(priceListLifecycleService.findLifecycleEventsByPricelistId(pricelistId));
    }
}
