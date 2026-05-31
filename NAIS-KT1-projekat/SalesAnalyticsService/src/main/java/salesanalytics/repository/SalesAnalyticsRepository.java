package salesanalytics.repository;

import org.springframework.stereotype.Repository;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;

import java.util.List;

@Repository
public interface SalesAnalyticsRepository {
    Boolean save(SalesProcessEvent event);

    List<SalesProcessEvent> findAll();

    List<SalesProcessEvent> findAllBySalesRepId(String salesRepId);

    List<SalesProcessEvent> findAllByRegion(String region);

    int seed(int count);

    Boolean delete(String opportunityId);

    List<SalesAnalyticsAggregate> topSalesRepsByNegotiationPipeline();

    List<SalesAnalyticsAggregate> stageBottlenecks();

    List<SalesAnalyticsAggregate> weeklyPipelineGrowthByRegion();
}