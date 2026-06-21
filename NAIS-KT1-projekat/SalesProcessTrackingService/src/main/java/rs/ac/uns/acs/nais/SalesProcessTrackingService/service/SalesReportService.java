package rs.ac.uns.acs.nais.SalesProcessTrackingService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.dto.SalesPerformanceReportDto;

import java.util.List;
import java.util.Map;

@Service
public class SalesReportService {

    private final GraphSalesService graphSalesService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String salesAnalyticsUrl;

    public SalesReportService(
            GraphSalesService graphSalesService,
            @Value("${sales-analytics.url:http://sales-analytics-service:8080/sales-analytics}") String salesAnalyticsUrl
    ) {
        this.graphSalesService = graphSalesService;
        this.salesAnalyticsUrl = salesAnalyticsUrl;
    }

    public SalesPerformanceReportDto generateSalesPerformanceReport(String region) {
        List<String> activeProcesses = graphSalesService.findActiveProcessesWithCustomerRepresentativeAndStage();

        List<Map<String, Object>> regionEvents = getListFromAnalytics(
                salesAnalyticsUrl + "/region?region=" + region
        );

        List<Map<String, Object>> stageBottlenecks = getListFromAnalytics(
                salesAnalyticsUrl + "/analytics/stage-bottlenecks"
        );

        List<Map<String, Object>> weeklyPipelineGrowth = getListFromAnalytics(
                salesAnalyticsUrl + "/analytics/weekly-pipeline-growth"
        );

        return new SalesPerformanceReportDto(
                activeProcesses,
                regionEvents,
                stageBottlenecks,
                weeklyPipelineGrowth
        );
    }

    private List<Map<String, Object>> getListFromAnalytics(String url) {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        ).getBody();
    }
}