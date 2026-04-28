package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.SideEffectGraphQueryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/graph-queries")
public class SideEffectGraphQueryController {

    private final SideEffectGraphQueryService queryService;

    public SideEffectGraphQueryController(SideEffectGraphQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/medication-side-effects-frequency")
    public List<Map<String, Object>> medicationSideEffectsFrequency() {
        return queryService.medicationSideEffectsFrequency();
    }

    @GetMapping("/medications-with-most-reports")
    public List<Map<String, Object>> medicationsWithMostReports() {
        return queryService.medicationsWithMostReports();
    }

    @GetMapping("/reports-by-side-effect")
    public List<Map<String, Object>> reportsBySideEffect() {
        return queryService.reportsBySideEffect();
    }

    @GetMapping("/similar-reports")
    public List<Map<String, Object>> similarReports() {
        return queryService.similarReports();
    }

    @GetMapping("/analyst-workload")
    public List<Map<String, Object>> analystWorkload() {
        return queryService.analystWorkload();
    }
}