package salesanalytics.service;

import org.springframework.stereotype.Service;
import salesanalytics.dto.RedisSalesEventView;
import salesanalytics.dto.TransactionalSalesEventResponse;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;
import salesanalytics.repository.SalesAnalyticsRepository;
import org.springframework.cache.annotation.Cacheable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SalesAnalyticsService {

    private final SalesAnalyticsRepository salesAnalyticsRepository;
    private final RedisSalesEventProjectionService redisProjectionService;

    public SalesAnalyticsService(SalesAnalyticsRepository salesAnalyticsRepository,
                                 RedisSalesEventProjectionService redisProjectionService) {
        this.salesAnalyticsRepository = salesAnalyticsRepository;
        this.redisProjectionService = redisProjectionService;
    }

    public boolean save(SalesProcessEvent event) {
        return salesAnalyticsRepository.save(event);
    }

    public TransactionalSalesEventResponse createTransactional(SalesProcessEvent event, boolean simulateRedisFailure) {
        validateEvent(event);

        String sagaId = UUID.randomUUID().toString();
        List<String> completedSteps = new ArrayList<>();
        redisProjectionService.recordSagaState(sagaId, "STARTED", "Saga started for opportunity " + event.getOpportunityId());

        boolean influxSaved = salesAnalyticsRepository.save(event);
        if (!influxSaved) {
            redisProjectionService.recordSagaState(sagaId, "FAILED", "InfluxDB write failed");
            throw new IllegalStateException("InfluxDB write failed. Transaction was not started.");
        }
        completedSteps.add("INFLUXDB_WRITE");

        try {
            if (simulateRedisFailure) {
                throw new IllegalStateException("Simulated Redis failure");
            }

            redisProjectionService.saveProjection(sagaId, event);
            completedSteps.add("REDIS_PROJECTION_WRITE");
            redisProjectionService.recordSagaState(sagaId, "COMPLETED", "Event stored in InfluxDB and indexed in Redis");

            return new TransactionalSalesEventResponse(
                    sagaId,
                    "COMPLETED",
                    "Event stored in InfluxDB and Redis.",
                    event,
                    completedSteps,
                    Instant.now()
            );
        } catch (RuntimeException ex) {
            boolean compensated = salesAnalyticsRepository.delete(event.getOpportunityId());
            String compensationStatus = compensated ? "COMPENSATED" : "COMPENSATION_FAILED";
            redisProjectionService.recordSagaState(
                    sagaId,
                    compensationStatus,
                    "Redis write failed after InfluxDB write. InfluxDB compensation executed: " + compensated
            );
            throw new IllegalStateException("Redis write failed. InfluxDB compensation executed: " + compensated, ex);
        }
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

    public List<RedisSalesEventView> latestRedisEvents(int limit) {
        return redisProjectionService.latestEvents(limit);
    }

    public Map<String, Long> redisRegionCounts() {
        return redisProjectionService.regionCounts();
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

    private void validateEvent(SalesProcessEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event body is required.");
        }
        if (event.getOpportunityId() == null || event.getOpportunityId().isBlank()) {
            throw new IllegalArgumentException("opportunityId is required.");
        }
        if (event.getCustomerId() == null || event.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("customerId is required.");
        }
        if (event.getSalesRepId() == null || event.getSalesRepId().isBlank()) {
            throw new IllegalArgumentException("salesRepId is required.");
        }
        if (event.getRegion() == null || event.getRegion().isBlank()) {
            throw new IllegalArgumentException("region is required.");
        }
        if (event.getStageTo() == null || event.getStageTo().isBlank()) {
            throw new IllegalArgumentException("stageTo is required.");
        }
        if (event.getDealValue() == null || event.getDealValue() < 0) {
            throw new IllegalArgumentException("dealValue must be a non-negative number.");
        }
    }
}
