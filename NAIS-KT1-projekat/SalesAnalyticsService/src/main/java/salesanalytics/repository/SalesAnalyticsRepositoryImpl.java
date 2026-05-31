package salesanalytics.repository;

import com.influxdb.client.InfluxDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import salesanalytics.configuration.InfluxDBConnectionClass;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;

import java.util.List;

@Repository
public class SalesAnalyticsRepositoryImpl implements SalesAnalyticsRepository {

    private final InfluxDBConnectionClass inConn;

    @Autowired
    public SalesAnalyticsRepositoryImpl(InfluxDBConnectionClass influxDBConnectionClass) {
        this.inConn = influxDBConnectionClass;
    }

    @Override
    public Boolean save(SalesProcessEvent event) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.save(client, event);
        } finally {
            client.close();
        }
    }

    @Override
    public List<SalesProcessEvent> findAll() {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findAll(client);
        } finally {
            client.close();
        }
    }

    @Override
    public List<SalesProcessEvent> findAllBySalesRepId(String salesRepId) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findAllBySalesRepId(client, salesRepId);
        } finally {
            client.close();
        }
    }

    @Override
    public List<SalesProcessEvent> findAllByRegion(String region) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.findAllByRegion(client, region);
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
    public Boolean delete(String opportunityId) {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.deleteRecord(client, opportunityId);
        } finally {
            client.close();
        }
    }

    @Override
    public List<SalesAnalyticsAggregate> topSalesRepsByNegotiationPipeline() {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.topSalesRepsByNegotiationPipeline(client);
        } finally {
            client.close();
        }
    }

    @Override
    public List<SalesAnalyticsAggregate> stageBottlenecks() {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.stageBottlenecks(client);
        } finally {
            client.close();
        }
    }

    @Override
    public List<SalesAnalyticsAggregate> weeklyPipelineGrowthByRegion() {
        InfluxDBClient client = inConn.buildConnection();
        try {
            return inConn.weeklyPipelineGrowthByRegion(client);
        } finally {
            client.close();
        }
    }
}