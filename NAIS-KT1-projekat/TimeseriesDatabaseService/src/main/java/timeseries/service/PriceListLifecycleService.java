package timeseries.service;

import org.springframework.stereotype.Service;
import timeseries.repository.PriceListLifecycleRepository;
import timeseries.model.PriceListLifecycleAggregate;
import timeseries.model.PriceListLifecycleEvent;

import java.util.List;

@Service
public class PriceListLifecycleService {

    private final PriceListLifecycleRepository priceListLifecycleRepository;

    public PriceListLifecycleService(PriceListLifecycleRepository priceListLifecycleRepository) {
        this.priceListLifecycleRepository = priceListLifecycleRepository;
    }

    public boolean save(PriceListLifecycleEvent event) {
        return priceListLifecycleRepository.save(event);
    }

    public List<PriceListLifecycleEvent> findAll() {
        return priceListLifecycleRepository.findAll();
    }

    public List<PriceListLifecycleEvent> findAllByTeamId(String teamId) {
        return priceListLifecycleRepository.findAllByTeamId(teamId);
    }

    public List<PriceListLifecycleEvent> findAllByUserId(String userId) {
        return priceListLifecycleRepository.findAllByUserId(userId);
    }

    public List<PriceListLifecycleAggregate> averageDraftDurationByTeam() {
        return priceListLifecycleRepository.averageDailyTeamSpeed();
    }

    public List<PriceListLifecycleAggregate> activityByUserBetween(String userId, String start, String stop) {
        return priceListLifecycleRepository.activityByUserBetween(userId, start, stop);
    }

    public List<PriceListLifecycleAggregate> slowestPriceListsAboveAverageReviewTime() {
        return priceListLifecycleRepository.slowestPriceListsAboveAverageReviewTime();
    }

    public boolean deleteRecord(String priceListId) {
        return priceListLifecycleRepository.delete(priceListId);
    }

    public int seed(int count) {
        return priceListLifecycleRepository.seed(count);
    }
}