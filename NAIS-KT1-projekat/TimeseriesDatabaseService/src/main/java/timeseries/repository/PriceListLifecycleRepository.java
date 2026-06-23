package timeseries.repository;

import org.springframework.stereotype.Repository;
import timeseries.model.PriceListLifecycleAggregate;
import timeseries.model.PriceListLifecycleEvent;

import java.util.List;

@Repository
public interface PriceListLifecycleRepository {
    Boolean save(PriceListLifecycleEvent event);

    List<PriceListLifecycleEvent> findAll();

    List<PriceListLifecycleEvent> findAllByTeamId(String teamId);

    List<PriceListLifecycleEvent> findAllByUserId(String userId);

    List<PriceListLifecycleEvent> findByFilters(
            String pricelistId,
            String userId,
            String teamId,
            String operationType,
            String statusFrom,
            String statusTo,
            String from,
            String to);

    List<PriceListLifecycleEvent> findByPricelistId(String pricelistId);

    List<PriceListLifecycleEvent> findActivationEvents();

    int seed(int count);

    Boolean delete(String priceListId);

    List<PriceListLifecycleAggregate> averageDailyTeamSpeed();

    List<PriceListLifecycleAggregate> activityByUserBetween(String userId, String start, String stop);

    List<PriceListLifecycleAggregate> slowestPriceListsAboveAverageReviewTime();
}
