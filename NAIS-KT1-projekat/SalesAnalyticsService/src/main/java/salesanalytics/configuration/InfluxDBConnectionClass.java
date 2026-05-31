package salesanalytics.configuration;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import salesanalytics.model.SalesAnalyticsAggregate;
import salesanalytics.model.SalesProcessEvent;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class InfluxDBConnectionClass {

    private static final String MEASUREMENT = "sales_process_event";

    @Value("${spring.influx.token}")
    private String token;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    @Value("${spring.influx.url}")
    private String url;

    public InfluxDBClient buildConnection() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }

    public boolean save(InfluxDBClient influxDBClient, SalesProcessEvent event) {
        try {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
            if (event.getTimestamp() == null) {
                event.setTimestamp(Instant.now());
            }

            Point point = Point.measurement(MEASUREMENT)
                    .addTag("opportunity_id", event.getOpportunityId())
                    .addTag("customer_id", event.getCustomerId())
                    .addTag("customer_segment", event.getCustomerSegment())
                    .addTag("sales_rep_id", event.getSalesRepId())
                    .addTag("sales_rep_name", event.getSalesRepName())
                    .addTag("region", event.getRegion())
                    .addTag("product_category", event.getProductCategory())
                    .addTag("stage_from", event.getStageFrom())
                    .addTag("stage_to", event.getStageTo())
                    .addTag("activity_type", event.getActivityType())
                    .addTag("outcome", event.getOutcome())
                    .addField("deal_value", event.getDealValue())
                    .addField("probability", event.getProbability())
                    .addField("stage_duration_hours", event.getStageDurationHours())
                    .addField("activity_duration_minutes", event.getActivityDurationMinutes())
                    .time(event.getTimestamp(), WritePrecision.MS);

            writeApi.writePoint(point);
            return true;
        } catch (InfluxException ex) {
            return false;
        }
    }

    public List<SalesProcessEvent> findAll(InfluxDBClient influxDBClient) {
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: -365d)
              |> filter(fn: (r) => r["_measurement"] == "%s")
              |> pivot(rowKey:["_time"], columnKey:["_field"], valueColumn:"_value")
              |> sort(columns:["_time"], desc: true)
            """, bucket, MEASUREMENT);

        return mapEvents(influxDBClient.getQueryApi(), flux);
    }

    public List<SalesProcessEvent> findAllBySalesRepId(InfluxDBClient influxDBClient, String salesRepId) {
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: -365d)
              |> filter(fn: (r) => r["_measurement"] == "%s")
              |> pivot(rowKey:["_time"], columnKey:["_field"], valueColumn:"_value")
              |> filter(fn: (r) => r["sales_rep_id"] == "%s")
              |> sort(columns:["_time"], desc: true)
            """, bucket, MEASUREMENT, salesRepId);

        return mapEvents(influxDBClient.getQueryApi(), flux);
    }

    public List<SalesProcessEvent> findAllByRegion(InfluxDBClient influxDBClient, String region) {
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: -365d)
              |> filter(fn: (r) => r["_measurement"] == "%s")
              |> pivot(rowKey:["_time"], columnKey:["_field"], valueColumn:"_value")
              |> filter(fn: (r) => r["region"] == "%s")
              |> sort(columns:["_time"], desc: true)
            """, bucket, MEASUREMENT, region);

        return mapEvents(influxDBClient.getQueryApi(), flux);
    }

    public List<SalesAnalyticsAggregate> topSalesRepsByNegotiationPipeline(InfluxDBClient influxDBClient) {
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: -90d)
              |> filter(fn: (r) => r["_measurement"] == "%s")
              |> filter(fn: (r) => r["_field"] == "deal_value" or r["_field"] == "probability" or r["_field"] == "stage_duration_hours")
              |> filter(fn: (r) => r["stage_from"] == "Qualification" and r["stage_to"] == "Negotiation")
              |> pivot(rowKey:["_time", "opportunity_id"], columnKey:["_field"], valueColumn:"_value")
              |> filter(fn: (r) => r["deal_value"] > 5000.0 and r["probability"] >= 0.35)
              |> group(columns:["region", "sales_rep_id", "sales_rep_name"])
              |> sum(column:"deal_value")
              |> sort(columns:["deal_value"], desc:true)
              |> limit(n:10)
              |> keep(columns:["region", "sales_rep_id", "sales_rep_name", "deal_value"])
            """, bucket, MEASUREMENT);

        return mapAggregates(influxDBClient.getQueryApi(), flux, "sales_rep_id", "negotiation_pipeline_value", "deal_value");
    }

    public List<SalesAnalyticsAggregate> stageBottlenecks(InfluxDBClient influxDBClient) {
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: -180d)
              |> filter(fn: (r) => r["_measurement"] == "%s")
              |> filter(fn: (r) => r["_field"] == "stage_duration_hours")
              |> filter(fn: (r) => r["stage_to"] != "Closed Won" and r["stage_to"] != "Closed Lost")
              |> group(columns:["stage_to", "region"])
              |> mean(column:"_value")
              |> sort(columns:["_value"], desc:true)
              |> limit(n:15)
              |> keep(columns:["stage_to", "region", "_value"])
            """, bucket, MEASUREMENT);

        return mapAggregates(influxDBClient.getQueryApi(), flux, "stage_to", "average_stage_duration_hours", "_value");
    }

    public List<SalesAnalyticsAggregate> weeklyPipelineGrowthByRegion(InfluxDBClient influxDBClient) {
        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: -120d)
              |> filter(fn: (r) => r["_measurement"] == "%s")
              |> filter(fn: (r) => r["_field"] == "deal_value")
              |> filter(fn: (r) => r["stage_to"] == "Proposal" or r["stage_to"] == "Negotiation" or r["stage_to"] == "Closed Won")
              |> group(columns:["region"])
              |> aggregateWindow(every: 7d, fn: sum, createEmpty:false)
              |> derivative(unit: 7d, nonNegative:false)
              |> sort(columns:["_value"], desc:true)
              |> keep(columns:["_time", "region", "_value"])
            """, bucket, MEASUREMENT);

        return mapAggregates(influxDBClient.getQueryApi(), flux, "region", "weekly_pipeline_growth", "_value");
    }

    public boolean deleteRecord(InfluxDBClient influxDBClient, String opportunityId) {
        try {
            DeleteApi deleteApi = influxDBClient.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.now().minus(365, ChronoUnit.DAYS);
            OffsetDateTime stop = OffsetDateTime.now();
            String predicate = String.format("_measurement=\"%s\" AND opportunity_id=\"%s\"", MEASUREMENT, opportunityId);
            deleteApi.delete(start, stop, predicate, bucket, org);
            return true;
        } catch (InfluxException ex) {
            return false;
        }
    }

    public int seed(InfluxDBClient influxDBClient, int count) {
        Random random = new Random(72L);

        String[] regions = {"Serbia", "Montenegro", "Bosnia", "Croatia"};
        String[] categories = {"Antibiotics", "Analgesics", "Cardiology", "Dermatology", "Supplements"};
        String[] segments = {"Large Pharmacy Chain", "Hospital", "Private Clinic", "Regional Wholesaler"};
        String[] reps = {"rep-01", "rep-02", "rep-03", "rep-04", "rep-05", "rep-06", "rep-07", "rep-08"};
        String[] repNames = {"Ana Markovic", "Marko Ilic", "Jelena Petrovic", "Nikola Jovic", "Mina Pavlovic", "Stefan Nikolic", "Sara Jovanovic", "Luka Petrovic"};
        String[][] transitions = {
                {"Lead", "Qualification"},
                {"Qualification", "Proposal"},
                {"Proposal", "Negotiation"},
                {"Qualification", "Negotiation"},
                {"Negotiation", "Closed Won"},
                {"Negotiation", "Closed Lost"}
        };
        String[] activities = {"Call", "Email", "Meeting", "Product Presentation", "Follow-up"};
        String[] outcomes = {"Positive", "Neutral", "Negative", "Waiting Customer", "Contract Sent"};

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        for (int index = 0; index < count; index++) {
            String[] transition = transitions[random.nextInt(transitions.length)];
            int repIndex = random.nextInt(reps.length);

            SalesProcessEvent event = new SalesProcessEvent();
            event.setOpportunityId("OPP-" + (1000 + (index % 500)));
            event.setCustomerId("CUST-" + (100 + (index % 120)));
            event.setCustomerSegment(segments[random.nextInt(segments.length)]);
            event.setSalesRepId(reps[repIndex]);
            event.setSalesRepName(repNames[repIndex]);
            event.setRegion(regions[random.nextInt(regions.length)]);
            event.setProductCategory(categories[random.nextInt(categories.length)]);
            event.setStageFrom(transition[0]);
            event.setStageTo(transition[1]);
            event.setActivityType(activities[random.nextInt(activities.length)]);
            event.setOutcome(outcomes[random.nextInt(outcomes.length)]);

            double baseValue = 3000 + random.nextInt(95000);
            if ("Hospital".equals(event.getCustomerSegment())) {
                baseValue *= 1.6;
            }
            if ("Closed Won".equals(event.getStageTo())) {
                baseValue *= 1.25;
            }
            event.setDealValue(baseValue);

            double probability = switch (event.getStageTo()) {
                case "Qualification" -> 0.20 + random.nextDouble() * 0.20;
                case "Proposal" -> 0.35 + random.nextDouble() * 0.20;
                case "Negotiation" -> 0.55 + random.nextDouble() * 0.25;
                case "Closed Won" -> 1.0;
                case "Closed Lost" -> 0.0;
                default -> 0.1;
            };
            event.setProbability(probability);

            double stageHours = switch (event.getStageTo()) {
                case "Qualification" -> 8 + random.nextInt(72);
                case "Proposal" -> 24 + random.nextInt(168);
                case "Negotiation" -> 48 + random.nextInt(240);
                case "Closed Won", "Closed Lost" -> 12 + random.nextInt(96);
                default -> 6 + random.nextInt(48);
            };
            event.setStageDurationHours(stageHours);
            event.setActivityDurationMinutes((double) (10 + random.nextInt(110)));

            event.setTimestamp(Instant.now()
                    .minus(random.nextInt(180), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES));

            Point point = Point.measurement(MEASUREMENT)
                    .addTag("opportunity_id", event.getOpportunityId())
                    .addTag("customer_id", event.getCustomerId())
                    .addTag("customer_segment", event.getCustomerSegment())
                    .addTag("sales_rep_id", event.getSalesRepId())
                    .addTag("sales_rep_name", event.getSalesRepName())
                    .addTag("region", event.getRegion())
                    .addTag("product_category", event.getProductCategory())
                    .addTag("stage_from", event.getStageFrom())
                    .addTag("stage_to", event.getStageTo())
                    .addTag("activity_type", event.getActivityType())
                    .addTag("outcome", event.getOutcome())
                    .addField("deal_value", event.getDealValue())
                    .addField("probability", event.getProbability())
                    .addField("stage_duration_hours", event.getStageDurationHours())
                    .addField("activity_duration_minutes", event.getActivityDurationMinutes())
                    .time(event.getTimestamp(), WritePrecision.MS);

            writeApi.writePoint(point);
        }

        return count;
    }

    private List<SalesProcessEvent> mapEvents(QueryApi queryApi, String flux) {
        List<SalesProcessEvent> events = new ArrayList<>();
        List<FluxTable> tables = queryApi.query(flux);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                SalesProcessEvent event = new SalesProcessEvent();

                event.setOpportunityId(asString(record.getValueByKey("opportunity_id")));
                event.setCustomerId(asString(record.getValueByKey("customer_id")));
                event.setCustomerSegment(asString(record.getValueByKey("customer_segment")));
                event.setSalesRepId(asString(record.getValueByKey("sales_rep_id")));
                event.setSalesRepName(asString(record.getValueByKey("sales_rep_name")));
                event.setRegion(asString(record.getValueByKey("region")));
                event.setProductCategory(asString(record.getValueByKey("product_category")));
                event.setStageFrom(asString(record.getValueByKey("stage_from")));
                event.setStageTo(asString(record.getValueByKey("stage_to")));
                event.setActivityType(asString(record.getValueByKey("activity_type")));
                event.setOutcome(asString(record.getValueByKey("outcome")));
                event.setDealValue(asDouble(record.getValueByKey("deal_value")));
                event.setProbability(asDouble(record.getValueByKey("probability")));
                event.setStageDurationHours(asDouble(record.getValueByKey("stage_duration_hours")));
                event.setActivityDurationMinutes(asDouble(record.getValueByKey("activity_duration_minutes")));

                Object time = record.getValueByKey("_time");
                if (time instanceof Instant instant) {
                    event.setTimestamp(instant);
                }

                events.add(event);
            }
        }

        return events;
    }

    private List<SalesAnalyticsAggregate> mapAggregates(QueryApi queryApi, String flux, String groupKeyField, String metric, String valueField) {
        List<SalesAnalyticsAggregate> aggregates = new ArrayList<>();
        List<FluxTable> tables = queryApi.query(flux);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Object keyValue = record.getValueByKey(groupKeyField);
                Object value = record.getValueByKey(valueField);

                SalesAnalyticsAggregate aggregate = new SalesAnalyticsAggregate(
                        keyValue == null ? null : keyValue.toString(),
                        metric,
                        asDouble(value)
                );

                Object time = record.getValueByKey("_time");
                if (time instanceof Instant instant) {
                    aggregate.setTimestamp(instant);
                }

                aggregates.add(aggregate);
            }
        }

        return aggregates;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Double asDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}