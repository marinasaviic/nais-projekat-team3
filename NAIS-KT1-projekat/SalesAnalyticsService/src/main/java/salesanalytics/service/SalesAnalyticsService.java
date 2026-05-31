package salesanalytics.service;

import org.springframework.stereotype.Service;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;
import salesanalytics.repository.SalesAnalyticsRepository;
import org.springframework.cache.annotation.Cacheable;

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

    @Cacheable(value = "topNegotiationPipeline")
    public List<SalesAnalyticsAggregate> topSalesRepsByNegotiationPipeline() {
        return salesAnalyticsRepository.topSalesRepsByNegotiationPipeline();
    }

    @Cacheable(value = "stageBottlenecks")
    public List<SalesAnalyticsAggregate> stageBottlenecks() {
        return salesAnalyticsRepository.stageBottlenecks();
    }

    @Cacheable(value = "weeklyPipelineGrowth")
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