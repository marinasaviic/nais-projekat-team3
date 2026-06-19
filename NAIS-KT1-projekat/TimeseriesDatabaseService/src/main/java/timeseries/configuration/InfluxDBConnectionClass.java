package timeseries.configuration;

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
import timeseries.model.PriceListLifecycleAggregate;
import timeseries.model.PriceListLifecycleEvent;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class InfluxDBConnectionClass {

    private static final String MEASUREMENT = "price_list_lifecycle";

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

    public boolean save(InfluxDBClient influxDBClient, PriceListLifecycleEvent event) {
        try {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
            if (event.getTimestamp() == null) {
                event.setTimestamp(Instant.now());
            }
            String pricelistId = tagValue(firstNonBlank(event.getPricelistId(), event.getPriceListId()), "unknown");
            String userId = tagValue(event.getUserId(), "unknown");
            String teamId = tagValue(event.getTeamId(), "none");
            String region = tagValue(event.getRegion(), "unknown");
            String operationType = tagValue(event.getOperationType(), "UNKNOWN");
            String statusFrom = tagValue(firstNonBlank(event.getStatusFromTag(), event.getStatusFrom()), "NONE");
            String statusTo = tagValue(firstNonBlank(event.getStatusToTag(), event.getStatusTo()), "UNKNOWN");
            Point point = Point.measurement(MEASUREMENT)
                    .addTag("saga_id", tagValue(event.getSagaId(), "unknown"))
                    .addTag("pricelistId", pricelistId)
                    .addTag("userId", userId)
                    .addTag("teamId", teamId)
                    .addTag("region", region)
                    .addTag("operationType", operationType)
                    .addTag("statusFrom", statusFrom)
                    .addTag("statusTo", statusTo)
                    .addTag("price_list_id", pricelistId)
                    .addTag("price_list_name", tagValue(resolvePriceListName(pricelistId, event.getPriceListName()), "unknown"))
                    .addTag("team_id", teamId)
                    .addTag("team_name", tagValue(resolveTeamName(teamId, event.getTeamName()), "none"))
                    .addTag("user_id", userId)
                    .addTag("user_name", tagValue(resolveUserName(userId, event.getUserName()), "unknown"))
                    .addTag("transition_label", tagValue(resolveTransitionLabel(statusFrom, statusTo, event.getTransitionLabel()), "UNKNOWN"))
                    .addField("status_from", statusFrom)
                    .addField("status_to", statusTo)
                    .addField("duration_ms", event.getDurationMs() == null ? 0.0 : event.getDurationMs())
                    .addField("success", event.getSuccess() == null || event.getSuccess())
                    .addField("error_message", event.getErrorMessage() == null ? "" : event.getErrorMessage())
                    .time(event.getTimestamp(), WritePrecision.MS);
            writeApi.writePoint(point);
            return true;
        } catch (InfluxException ex) {
            return false;
        }
    }

    public List<PriceListLifecycleEvent> findAll(InfluxDBClient influxDBClient) {
        String flux = String.format(
            "from(bucket:\"%s\") |> range(start: -365d) |> filter(fn: (r) => r[\"_measurement\"] == \"%s\") |> pivot(rowKey:[\"_time\"], columnKey:[\"_field\"], valueColumn:\"_value\") |> sort(columns:[\"_time\"]) |> yield(name:\"all\")",
            bucket,
            MEASUREMENT);
        return mapEvents(influxDBClient.getQueryApi(), flux);
    }

    public List<PriceListLifecycleEvent> findAllByTeamId(InfluxDBClient influxDBClient, String teamId) {
        String flux = String.format(
            "from(bucket:\"%s\") |> range(start: -365d) |> filter(fn: (r) => r[\"_measurement\"] == \"%s\") |> pivot(rowKey:[\"_time\"], columnKey:[\"_field\"], valueColumn:\"_value\") |> filter(fn: (r) => r[\"team_id\"] == \"%s\") |> sort(columns:[\"_time\"]) |> yield(name:\"team\")",
            bucket,
            MEASUREMENT,
            teamId);
        return mapEvents(influxDBClient.getQueryApi(), flux);
    }

    public List<PriceListLifecycleEvent> findAllByUserId(InfluxDBClient influxDBClient, String userId) {
        String flux = String.format(
            "from(bucket:\"%s\") |> range(start: -365d) |> filter(fn: (r) => r[\"_measurement\"] == \"%s\") |> pivot(rowKey:[\"_time\"], columnKey:[\"_field\"], valueColumn:\"_value\") |> filter(fn: (r) => r[\"user_id\"] == \"%s\") |> sort(columns:[\"_time\"]) |> yield(name:\"user\")",
            bucket,
            MEASUREMENT,
            userId);
        return mapEvents(influxDBClient.getQueryApi(), flux);
    }

    public List<PriceListLifecycleEvent> findByFilters(InfluxDBClient influxDBClient,
                                                       String pricelistId,
                                                       String userId,
                                                       String teamId,
                                                       String operationType,
                                                       String statusFrom,
                                                       String statusTo,
                                                       String from,
                                                       String to) {
        StringBuilder flux = new StringBuilder(String.format(
                "from(bucket:\"%s\") |> range(start: %s%s) |> filter(fn: (r) => r[\"_measurement\"] == \"%s\") |> pivot(rowKey:[\"_time\"], columnKey:[\"_field\"], valueColumn:\"_value\")",
                bucket,
                fluxTime(from, "-365d"),
                to == null || to.isBlank() ? "" : ", stop: " + fluxTime(to, null),
                MEASUREMENT));
        appendStringFilter(flux, "pricelistId", pricelistId);
        appendStringFilter(flux, "userId", userId);
        appendStringFilter(flux, "teamId", teamId);
        appendStringFilter(flux, "operationType", operationType);
        appendStringFilter(flux, "statusFrom", statusFrom);
        appendStringFilter(flux, "statusTo", statusTo);
        flux.append(" |> sort(columns:[\"_time\"]) |> yield(name:\"filtered\")");
        return mapEvents(influxDBClient.getQueryApi(), flux.toString());
    }

    public List<PriceListLifecycleEvent> findByPricelistId(InfluxDBClient influxDBClient, String pricelistId) {
        return findByFilters(influxDBClient, pricelistId, null, null, null, null, null, null, null);
    }

    public List<PriceListLifecycleEvent> findActivationEvents(InfluxDBClient influxDBClient) {
        String flux = String.format(
                "from(bucket:\"%s\") |> range(start: -365d) |> filter(fn: (r) => r[\"_measurement\"] == \"%s\") |> pivot(rowKey:[\"_time\"], columnKey:[\"_field\"], valueColumn:\"_value\") |> filter(fn: (r) => (exists r[\"operationType\"] and r[\"operationType\"] == \"CREATED\") or (exists r[\"statusTo\"] and (r[\"statusTo\"] == \"DRAFT\" or r[\"statusTo\"] == \"ACTIVE\")) or (exists r[\"status_to\"] and (r[\"status_to\"] == \"DRAFT\" or r[\"status_to\"] == \"ACTIVE\"))) |> sort(columns:[\"_time\"]) |> yield(name:\"activation-events\")",
                bucket,
                MEASUREMENT);
        return mapEvents(influxDBClient.getQueryApi(), flux);
    }

    public List<PriceListLifecycleAggregate> averageDraftDurationByTeam(InfluxDBClient influxDBClient) {
        String flux = String.format(
            "from(bucket:\"%s\") |> range(start: -30d) |> filter(fn: (r) => r[\"_measurement\"] == \"%s\") |> pivot(rowKey:[\"_time\"], columnKey:[\"_field\"], valueColumn:\"_value\") |> filter(fn: (r) => r[\"status_to\"] == \"Active\") |> aggregateWindow(every: 1d, fn: mean, column:\"duration_ms\") |> group(columns:[\"team_id\"]) |> sort(columns:[\"_value\"], desc:true) |> yield(name:\"mean\")",
            bucket,
            MEASUREMENT);
        return mapAggregates(influxDBClient.getQueryApi(), flux, "team_id", "mean_duration_ms");
    }

    public List<PriceListLifecycleAggregate> activityByUserBetween(InfluxDBClient influxDBClient, String userId, String start, String stop) {
        String flux = String.format(
            "from(bucket:\"%s\") |> range(start: %s, stop: %s) |> filter(fn: (r) => r[\"_measurement\"] == \"%s\" and r[\"_field\"] == \"status_to\") |> filter(fn: (r) => r[\"user_id\"] == \"%s\") |> aggregateWindow(every: 1w, fn: count, column:\"_value\") |> group(columns:[\"user_id\"]) |> sort(columns:[\"_time\"]) |> yield(name:\"count\")",
            bucket,
            start,
            stop,
            MEASUREMENT,
            userId);
        return mapAggregates(influxDBClient.getQueryApi(), flux, "user_id", "changes_count");
    }

    public List<PriceListLifecycleAggregate> slowestPriceListsAboveAverageReviewTime(InfluxDBClient influxDBClient) {
        List<PriceListLifecycleEvent> events = findAll(influxDBClient);
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
        Map<String, Double> maxDurationsByPriceList = new HashMap<>();

        for (PriceListLifecycleEvent event : events) {
            if (event.getTimestamp() == null || event.getTimestamp().isBefore(cutoff)) {
                continue;
            }
            if (!"Draft".equals(event.getStatusFrom()) || !"In Review".equals(event.getStatusTo())) {
                continue;
            }
            if (event.getPriceListId() == null || event.getDurationMs() == null) {
                continue;
            }
            maxDurationsByPriceList.merge(event.getPriceListId(), event.getDurationMs(), Math::max);
        }

        if (maxDurationsByPriceList.isEmpty()) {
            return new ArrayList<>();
        }

        double average = maxDurationsByPriceList.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);

        List<PriceListLifecycleAggregate> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : maxDurationsByPriceList.entrySet()) {
            if (entry.getValue() > average) {
                result.add(new PriceListLifecycleAggregate(entry.getKey(), "in_review_max_duration_ms", entry.getValue()));
            }
        }
        result.sort((left, right) -> Double.compare(right.getValue(), left.getValue()));
        return result;
    }

    public boolean deleteRecord(InfluxDBClient influxDBClient, String priceListId) {
        try {
            DeleteApi deleteApi = influxDBClient.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.now().minus(365, ChronoUnit.DAYS);
            OffsetDateTime stop = OffsetDateTime.now();
            String predicate = String.format("_measurement=\"%s\" AND price_list_id = \"%s\"", MEASUREMENT, priceListId);
            deleteApi.delete(start, stop, predicate, bucket, org);
            return true;
        } catch (InfluxException ex) {
            return false;
        }
    }

    public int seed(InfluxDBClient influxDBClient, int count) {
        Random random = new Random(42L);
        String[] teams = {"team-alpha", "team-beta", "team-gamma", "team-delta"};
        String[] teamNames = {"Alpha Team", "Beta Team", "Gamma Team", "Delta Team"};
        String[] users = {"user-01", "user-02", "user-03", "user-04", "user-05", "user-06"};
        String[] userNames = {"Ana Markovic", "Marko Ilic", "Jelena Petrovic", "Nikola Jovic", "Mina Pavlovic", "Stefan Nikolic"};
        String[][] transitions = {
                {"Draft", "In Review"},
                {"In Review", "Draft"},
                {"In Review", "Active"},
                {"Active", "Archived"}
        };

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        for (int index = 0; index < count; index++) {
            String[] transition = transitions[random.nextInt(transitions.length)];
            PriceListLifecycleEvent event = new PriceListLifecycleEvent();
            event.setPriceListId("PL-" + (1000 + (index % 250)));
            event.setPriceListName("Price List " + (1000 + (index % 250)));
            event.setTeamId(teams[index % teams.length]);
            event.setTeamName(teamNames[index % teamNames.length]);
            int userIndex = random.nextInt(users.length);
            event.setUserId(users[userIndex]);
            event.setUserName(userNames[userIndex]);
            event.setStatusFrom(transition[0]);
            event.setStatusTo(transition[1]);
            event.setTransitionLabel(resolveTransitionLabel(event.getStatusFrom(), event.getStatusTo(), null));

            // Realistic duration distributions (milliseconds)
            long durationMs;
            if ("Draft".equals(transition[0]) && "In Review".equals(transition[1])) {
                // Draft -> In Review: 1h - 72h
                durationMs = (1 + random.nextInt(72)) * 60L * 60L * 1000L;
            } else if ("In Review".equals(transition[0]) && "Active".equals(transition[1])) {
                // In Review -> Active: 1h - 48h
                durationMs = (1 + random.nextInt(48)) * 60L * 60L * 1000L;
            } else if ("Active".equals(transition[0]) && "Archived".equals(transition[1])) {
                // Active -> Archived: 7 - 30 days
                durationMs = (7 + random.nextInt(24)) * 24L * 60L * 60L * 1000L;
            } else if ("In Review".equals(transition[0]) && "Draft".equals(transition[1])) {
                // In Review -> Draft (rework): 1h - 24h
                durationMs = (1 + random.nextInt(24)) * 60L * 60L * 1000L;
            } else {
                // fallback: 5m - 12h
                durationMs = (5 + random.nextInt(12 * 60)) * 60L * 1000L;
            }
            event.setDurationMs((double) durationMs);

                int maxDaysBack = ("Draft".equals(transition[0]) && "In Review".equals(transition[1])) ? 30 : 365;
                event.setTimestamp(Instant.now()
                    .minus(random.nextInt(maxDaysBack), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES));

            Point point = Point.measurement(MEASUREMENT)
                    .addTag("price_list_id", event.getPriceListId())
                    .addTag("team_id", event.getTeamId())
                    .addTag("user_id", event.getUserId())
                    .addField("status_from", event.getStatusFrom())
                    .addField("status_to", event.getStatusTo())
                    .addField("duration_ms", event.getDurationMs())
                    .time(event.getTimestamp(), WritePrecision.MS);
            writeApi.writePoint(point);
        }
        return count;
    }

    private List<PriceListLifecycleEvent> mapEvents(QueryApi queryApi, String flux) {
        List<PriceListLifecycleEvent> events = new ArrayList<>();
        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                        PriceListLifecycleEvent event = new PriceListLifecycleEvent();
                        event.setSagaId(record.getValueByKey("saga_id") == null ? null : record.getValueByKey("saga_id").toString());
                        event.setPricelistId(record.getValueByKey("pricelistId") == null ? null : record.getValueByKey("pricelistId").toString());
                        event.setPriceListId(record.getValueByKey("price_list_id") == null ? null : record.getValueByKey("price_list_id").toString());
                        if (event.getPricelistId() == null) {
                            event.setPricelistId(event.getPriceListId());
                        }
                        event.setPriceListName(resolvePriceListName(event.getPriceListId(), record.getValueByKey("price_list_name") == null ? null : record.getValueByKey("price_list_name").toString()));
                        event.setTeamId(record.getValueByKey("team_id") == null ? null : record.getValueByKey("team_id").toString());
                        if (event.getTeamId() == null && record.getValueByKey("teamId") != null) {
                            event.setTeamId(record.getValueByKey("teamId").toString());
                        }
                        event.setTeamName(resolveTeamName(event.getTeamId(), record.getValueByKey("team_name") == null ? null : record.getValueByKey("team_name").toString()));
                        event.setUserId(record.getValueByKey("user_id") == null ? null : record.getValueByKey("user_id").toString());
                        if (event.getUserId() == null && record.getValueByKey("userId") != null) {
                            event.setUserId(record.getValueByKey("userId").toString());
                        }
                        event.setUserName(resolveUserName(event.getUserId(), record.getValueByKey("user_name") == null ? null : record.getValueByKey("user_name").toString()));
                        event.setRegion(record.getValueByKey("region") == null ? null : record.getValueByKey("region").toString());
                        event.setOperationType(record.getValueByKey("operationType") == null ? null : record.getValueByKey("operationType").toString());
                        event.setStatusFromTag(record.getValueByKey("statusFrom") == null ? null : record.getValueByKey("statusFrom").toString());
                        event.setStatusToTag(record.getValueByKey("statusTo") == null ? null : record.getValueByKey("statusTo").toString());
                        Object sf = record.getValueByKey("status_from");
                        if (sf != null) event.setStatusFrom(sf.toString());
                        if (event.getStatusFrom() == null) {
                            event.setStatusFrom(event.getStatusFromTag());
                        }
                        Object st = record.getValueByKey("status_to");
                        if (st != null) event.setStatusTo(st.toString());
                        if (event.getStatusTo() == null) {
                            event.setStatusTo(event.getStatusToTag());
                        }
                        event.setTransitionLabel(resolveTransitionLabel(event.getStatusFrom(), event.getStatusTo(), record.getValueByKey("transition_label") == null ? null : record.getValueByKey("transition_label").toString()));
                        Object duration = record.getValueByKey("duration_ms");
                        if (duration instanceof Number number) {
                            event.setDurationMs(number.doubleValue());
                        } else if (duration != null) {
                            try {
                                event.setDurationMs(Double.parseDouble(duration.toString()));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        Object success = record.getValueByKey("success");
                        if (success instanceof Boolean booleanValue) {
                            event.setSuccess(booleanValue);
                        } else if (success != null) {
                            event.setSuccess(Boolean.parseBoolean(success.toString()));
                        }
                        Object errorMessage = record.getValueByKey("error_message");
                        if (errorMessage != null) {
                            event.setErrorMessage(errorMessage.toString());
                        }
                        event.setTimestamp((Instant) record.getValueByKey("_time"));
                events.add(event);
            }
        }
        return events;
    }

    private List<PriceListLifecycleAggregate> mapAggregates(QueryApi queryApi, String flux, String groupKeyField, String metric) {
        List<PriceListLifecycleAggregate> aggregates = new ArrayList<>();
        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Object value = record.getValueByKey("_value");
                Double numericValue = value instanceof Number number ? number.doubleValue() : null;
                Object keyValue = record.getValueByKey(groupKeyField);
                String groupKey = keyValue == null ? null : keyValue.toString();
                PriceListLifecycleAggregate agg = new PriceListLifecycleAggregate(groupKey, metric, numericValue);
                Object time = record.getValueByKey("_time");
                if (time instanceof Instant instant) {
                    agg.setTimestamp(instant);
                } else if (time != null) {
                    try {
                        agg.setTimestamp(Instant.parse(time.toString()));
                    } catch (Exception ignored) {
                    }
                }
                aggregates.add(agg);
            }
        }
        return aggregates;
    }

    private String resolvePriceListName(String priceListId, String priceListName) {
        if (priceListName != null && !priceListName.isBlank()) {
            return priceListName;
        }
        return priceListId == null ? null : "Price List " + priceListId.replace("PL-", "");
    }

    private String resolveTeamName(String teamId, String teamName) {
        if (teamName != null && !teamName.isBlank()) {
            return teamName;
        }
        if (teamId == null) {
            return null;
        }
        return switch (teamId) {
            case "team-alpha" -> "Alpha Team";
            case "team-beta" -> "Beta Team";
            case "team-gamma" -> "Gamma Team";
            case "team-delta" -> "Delta Team";
            default -> teamId;
        };
    }

    private String resolveUserName(String userId, String userName) {
        if (userName != null && !userName.isBlank()) {
            return userName;
        }
        if (userId == null) {
            return null;
        }
        return switch (userId) {
            case "user-01" -> "Ana Markovic";
            case "user-02" -> "Marko Ilic";
            case "user-03" -> "Jelena Petrovic";
            case "user-04" -> "Nikola Jovic";
            case "user-05" -> "Mina Pavlovic";
            case "user-06" -> "Stefan Nikolic";
            default -> userId;
        };
    }

    private String resolveTransitionLabel(String statusFrom, String statusTo, String transitionLabel) {
        if (transitionLabel != null && !transitionLabel.isBlank()) {
            return transitionLabel;
        }
        if (statusFrom == null || statusTo == null) {
            return null;
        }
        return statusFrom + " -> " + statusTo;
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    private String tagValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void appendStringFilter(StringBuilder flux, String column, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        flux.append(String.format(" |> filter(fn: (r) => exists r[\"%s\"] and r[\"%s\"] == \"%s\")", column, column, escapeFluxString(value)));
    }

    private String fluxTime(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        if (value.startsWith("-") || value.matches("\\d+[smhdw]")) {
            return value;
        }
        return "time(v: \"" + escapeFluxString(value) + "\")";
    }

    private String escapeFluxString(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
