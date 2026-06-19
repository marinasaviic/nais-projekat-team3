package timeseries.repository;

import com.influxdb.client.InfluxDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import timeseries.configuration.InfluxDBConnectionClass;
import timeseries.model.PriceListLifecycleAggregate;
import timeseries.model.PriceListLifecycleEvent;

import java.util.List;

@Repository
public class PriceListLifecycleRepositoryImpl implements PriceListLifecycleRepository {

    private final InfluxDBConnectionClass inConn;

    @Autowired
    public PriceListLifecycleRepositoryImpl(InfluxDBConnectionClass influxDBConnectionClass) {
        this.inConn = influxDBConnectionClass;
    }

    @Override
    public Boolean save(PriceListLifecycleEvent event) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.save(client, event);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleEvent> findAll() {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findAll(client);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleEvent> findAllByTeamId(String teamId) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findAllByTeamId(client, teamId);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleEvent> findAllByUserId(String userId) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findAllByUserId(client, userId);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleEvent> findByFilters(String pricelistId,
                                                       String userId,
                                                       String teamId,
                                                       String operationType,
                                                       String statusFrom,
                                                       String statusTo,
                                                       String from,
                                                       String to) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findByFilters(client, pricelistId, userId, teamId, operationType, statusFrom, statusTo, from, to);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleEvent> findByPricelistId(String pricelistId) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findByPricelistId(client, pricelistId);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleEvent> findActivationEvents() {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findActivationEvents(client);
        } finally {
            client.close();
        }
    }

    @Override
    public int seed(int count) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.seed(client, count);
        } finally {
            client.close();
        }
    }

    @Override
    public Boolean delete(String priceListId) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.deleteRecord(client, priceListId);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleAggregate> averageDailyTeamSpeed() {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.averageDraftDurationByTeam(client);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleAggregate> activityByUserBetween(String userId, String start, String stop) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.activityByUserBetween(client, userId, start, stop);
        } finally {
            client.close();
        }
    }

    @Override
    public List<PriceListLifecycleAggregate> slowestPriceListsAboveAverageReviewTime() {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.slowestPriceListsAboveAverageReviewTime(client);
        } finally {
            client.close();
        }
    }
}
