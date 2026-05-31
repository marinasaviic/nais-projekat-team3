package salesanalytics.service;

import org.springframework.stereotype.Service;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;
import salesanalytics.repository.SalesAnalyticsRepository;

import java.util.List;

@Service
public class SalesAnalyticsService {

    private final SalesAnalyticsRepository salesAnalyticsRepository;

    public SalesAnalyticsService(SalesAnalyticsRepository salesAnalyticsRepository) {
        this.salesAnalyticsRepository = salesAnalyticsRepository;
    }

    public boolean save(SalesProcessEvent event) {
        return salesAnalyticsRepository.save(event);
    }

    public List<SalesProcessEvent> findAll() {
        return salesAnalyticsRepository.findAll();
    }

    public List<SalesProcessEvent> findAllBySalesRepId(String salesRepId) {
        return salesAnalyticsRepository.findAllBySalesRepId(salesRepId);
    }

    public List<SalesProcessEvent> findAllByRegion(String region) {
        return salesAnalyticsRepository.findAllByRegion(region);
    }

    public List<SalesAnalyticsAggregate> topSalesRepsByNegotiationPipeline() {
        return salesAnalyticsRepository.topSalesRepsByNegotiationPipeline();
    }

    public List<SalesAnalyticsAggregate> stageBottlenecks() {
        return salesAnalyticsRepository.stageBottlenecks();
    }

    public List<SalesAnalyticsAggregate> weeklyPipelineGrowthByRegion() {
        return salesAnalyticsRepository.weeklyPipelineGrowthByRegion();
    }

    public boolean deleteRecord(String opportunityId) {
        return salesAnalyticsRepository.delete(opportunityId);
    }

    public int seed(int count) {
        return salesAnalyticsRepository.seed(count);
    }
}