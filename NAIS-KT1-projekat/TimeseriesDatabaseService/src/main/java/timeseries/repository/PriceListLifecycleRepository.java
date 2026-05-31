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

    int seed(int count);

    Boolean delete(String priceListId);

    List<PriceListLifecycleAggregate> averageDailyTeamSpeed();

    List<PriceListLifecycleAggregate> activityByUserBetween(String userId, String start, String stop);

    List<PriceListLifecycleAggregate> slowestPriceListsAboveAverageReviewTime();
}
