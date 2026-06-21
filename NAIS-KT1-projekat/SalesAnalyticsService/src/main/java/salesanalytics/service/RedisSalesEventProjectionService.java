package salesanalytics.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import salesanalytics.dto.RedisSalesEventView;
import salesanalytics.model.SalesProcessEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisSalesEventProjectionService {

    private static final String EVENT_KEY_PREFIX = "sales:event:";
    private static final String SAGA_KEY_PREFIX = "sales:saga:";
    private static final String REGION_COUNT_KEY = "sales:report:region-counts";
    private static final String LATEST_EVENTS_KEY = "sales:report:latest-events";

    private final StringRedisTemplate redisTemplate;

    public RedisSalesEventProjectionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveProjection(String sagaId, SalesProcessEvent event) {
        Instant indexedAt = Instant.now();
        String key = EVENT_KEY_PREFIX + event.getOpportunityId();

        Map<String, String> values = new LinkedHashMap<>();
        values.put("sagaId", sagaId);
        values.put("opportunityId", nullToEmpty(event.getOpportunityId()));
        values.put("customerId", nullToEmpty(event.getCustomerId()));
        values.put("salesRepId", nullToEmpty(event.getSalesRepId()));
        values.put("salesRepName", nullToEmpty(event.getSalesRepName()));
        values.put("region", nullToEmpty(event.getRegion()));
        values.put("stageTo", nullToEmpty(event.getStageTo()));
        values.put("productCategory", nullToEmpty(event.getProductCategory()));
        values.put("dealValue", event.getDealValue() == null ? "0" : event.getDealValue().toString());
        values.put("eventTimestamp", event.getTimestamp() == null ? "" : event.getTimestamp().toString());
        values.put("indexedAt", indexedAt.toString());

        redisTemplate.opsForHash().putAll(key, values);
        redisTemplate.opsForZSet().add(LATEST_EVENTS_KEY, event.getOpportunityId(), indexedAt.toEpochMilli());

        if (event.getRegion() != null && !event.getRegion().isBlank()) {
            redisTemplate.opsForHash().increment(REGION_COUNT_KEY, event.getRegion(), 1);
        }
    }

    public void recordSagaState(String sagaId, String status, String message) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("sagaId", sagaId);
        values.put("status", status);
        values.put("message", message);
        values.put("updatedAt", Instant.now().toString());
        redisTemplate.opsForHash().putAll(SAGA_KEY_PREFIX + sagaId, values);
    }

    public List<RedisSalesEventView> latestEvents(int limit) {
        Set<String> ids = redisTemplate.opsForZSet().reverseRange(LATEST_EVENTS_KEY, 0, Math.max(0, limit - 1));
        List<RedisSalesEventView> events = new ArrayList<>();

        if (ids == null) {
            return events;
        }

        for (String id : ids) {
            Map<Object, Object> raw = redisTemplate.opsForHash().entries(EVENT_KEY_PREFIX + id);
            if (!raw.isEmpty()) {
                events.add(map(raw));
            }
        }

        return events;
    }

    public Map<String, Long> regionCounts() {
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(REGION_COUNT_KEY);
        Map<String, Long> counts = new LinkedHashMap<>();

        raw.entrySet().stream()
                .sorted(Map.Entry.<Object, Object>comparingByValue(Comparator.comparingLong(value -> parseLong(value.toString()))).reversed())
                .forEach(entry -> counts.put(entry.getKey().toString(), parseLong(entry.getValue().toString())));

        return counts;
    }

    private RedisSalesEventView map(Map<Object, Object> raw) {
        RedisSalesEventView view = new RedisSalesEventView();
        view.setOpportunityId(value(raw, "opportunityId"));
        view.setCustomerId(value(raw, "customerId"));
        view.setSalesRepId(value(raw, "salesRepId"));
        view.setSalesRepName(value(raw, "salesRepName"));
        view.setRegion(value(raw, "region"));
        view.setStageTo(value(raw, "stageTo"));
        view.setProductCategory(value(raw, "productCategory"));
        view.setDealValue(parseDouble(value(raw, "dealValue")));
        view.setEventTimestamp(parseInstant(value(raw, "eventTimestamp")));
        view.setIndexedAt(parseInstant(value(raw, "indexedAt")));
        return view;
    }

    private String value(Map<Object, Object> raw, String key) {
        Object value = raw.get(key);
        return value == null ? null : value.toString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Double parseDouble(String value) {
        try {
            return value == null || value.isBlank() ? null : Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long parseLong(String value) {
        try {
            return value == null || value.isBlank() ? 0L : Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private Instant parseInstant(String value) {
        try {
            return value == null || value.isBlank() ? null : Instant.parse(value);
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
