package rs.ac.uns.acs.nais.AdverseEffectsSearchService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.AnalyticsService;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analytics;

    public AnalyticsController(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @GetMapping("/drug-risk-search")
    public JsonNode searchDrugRisk(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String therapeuticClass,
            @RequestParam(required = false) Double minRiskScore,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) throws IOException {
        return analytics.searchDrugRisk(text, therapeuticClass, minRiskScore, sortDirection);
    }

    @GetMapping("/reports-by-region")
    public JsonNode reportsByRegion(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws IOException {
        return analytics.reportsByRegion(region, severity, from, to);
    }

    @GetMapping("/manufacturer-safety")
    public JsonNode manufacturerSafety(
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String reactionType,
            @RequestParam(required = false) Integer minReports
    ) throws IOException {
        return analytics.manufacturerSafety(manufacturer, reactionType, minReports);
    }
}
