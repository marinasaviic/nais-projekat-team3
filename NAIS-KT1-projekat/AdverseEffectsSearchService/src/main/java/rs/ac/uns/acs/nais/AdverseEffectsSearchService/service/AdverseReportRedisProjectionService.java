package rs.ac.uns.acs.nais.AdverseEffectsSearchService.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.dto.AdverseReportProjectionDto;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.AdverseEventReportDocument;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdverseReportRedisProjectionService {
    private static final String PROJECTION_PREFIX = "adverse:report-projection:";
    private static final String LATEST_KEY = "adverse:report-projections:latest";
    private static final String SEVERITY_COUNTS_KEY = "adverse:report-projections:severity-counts";
    private static final String SAGA_PREFIX = "adverse:report-saga:";

    private final RedisKeyValueService redis;

    public AdverseReportRedisProjectionService(RedisKeyValueService redis) {
        this.redis = redis;
    }

    public void saveProjection(String sagaId, AdverseEventReportDocument report) {
        String key = PROJECTION_PREFIX + report.getId();
        Instant indexedAt = Instant.now();

        redis.putHashValue(key, "sagaId", sagaId);
        redis.putHashValue(key, "reportId", safe(report.getId()));
        redis.putHashValue(key, "drugName", safe(report.getDrugName()));
        redis.putHashValue(key, "reactionType", safe(report.getReactionType()));
        redis.putHashValue(key, "severity", safe(report.getSeverity()));
        redis.putHashValue(key, "region", safe(report.getRegion()));
        redis.putHashValue(key, "reporterType", safe(report.getReporterType()));
        redis.putHashValue(key, "hospitalizationRequired", String.valueOf(report.isHospitalizationRequired()));
        redis.putHashValue(key, "indexedAt", indexedAt.toString());

        redis.putHashValue(LATEST_KEY, report.getId(), indexedAt.toString());
        if (report.getSeverity() != null && !report.getSeverity().isBlank()) {
            redis.incrementHashValue(SEVERITY_COUNTS_KEY, report.getSeverity(), 1);
        }
    }

    public void recordSagaState(String sagaId, String status, String message) {
        String key = SAGA_PREFIX + sagaId;
        redis.putHashValue(key, "sagaId", sagaId);
        redis.putHashValue(key, "status", status);
        redis.putHashValue(key, "message", message);
        redis.putHashValue(key, "updatedAt", Instant.now().toString());
    }

    public List<AdverseReportProjectionDto> latestProjections(int limit) {
        Map<String, String> latest = redis.getHash(LATEST_KEY);
        return latest.entrySet().stream()
                .sorted(Map.Entry.<String, String>comparingByValue().reversed())
                .limit(Math.max(0, limit))
                .map(entry -> redis.getHash(PROJECTION_PREFIX + entry.getKey()))
                .filter(values -> !values.isEmpty())
                .map(this::mapProjection)
                .toList();
    }

    public Map<String, Long> severityCounts() {
        Map<String, String> raw = redis.getHash(SEVERITY_COUNTS_KEY);
        Map<String, Long> counts = new LinkedHashMap<>();
        raw.entrySet().stream()
                .sorted(Map.Entry.<String, String>comparingByValue(Comparator.comparingLong(this::parseLong)).reversed())
                .forEach(entry -> counts.put(entry.getKey(), parseLong(entry.getValue())));
        return counts;
    }

    public List<Map<String, String>> sagaStates(int limit) {
        Set<String> keys = redis.getKeysByPattern(SAGA_PREFIX + "*");
        List<Map<String, String>> states = new ArrayList<>();
        for (String key : keys) {
            states.add(redis.getHash(key));
        }
        return states.stream()
                .sorted(Comparator.comparing(state -> state.getOrDefault("updatedAt", ""), Comparator.reverseOrder()))
                .limit(Math.max(0, limit))
                .toList();
    }

    private AdverseReportProjectionDto mapProjection(Map<String, String> values) {
        AdverseReportProjectionDto dto = new AdverseReportProjectionDto();
        dto.setReportId(values.get("reportId"));
        dto.setDrugName(values.get("drugName"));
        dto.setReactionType(values.get("reactionType"));
        dto.setSeverity(values.get("severity"));
        dto.setRegion(values.get("region"));
        dto.setReporterType(values.get("reporterType"));
        dto.setHospitalizationRequired(values.get("hospitalizationRequired"));
        dto.setIndexedAt(parseInstant(values.get("indexedAt")));
        return dto;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private long parseLong(String value) {
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
