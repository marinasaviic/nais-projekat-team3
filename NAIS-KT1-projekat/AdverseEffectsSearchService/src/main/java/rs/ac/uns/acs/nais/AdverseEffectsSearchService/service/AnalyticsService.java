package rs.ac.uns.acs.nais.AdverseEffectsSearchService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    private final ElasticsearchDocumentService elasticsearch;
    private final RedisKeyValueService redis;
    private final ObjectMapper objectMapper;

    public AnalyticsService(ElasticsearchDocumentService elasticsearch, RedisKeyValueService redis, ObjectMapper objectMapper) {
        this.elasticsearch = elasticsearch;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public JsonNode searchDrugRisk(String text, String therapeuticClass, Double minRiskScore, String sortDirection) throws IOException {
        String cacheKey = "analytics:drug-risk:" + text + ":" + therapeuticClass + ":" + minRiskScore + ":" + sortDirection;
        var cached = redis.get(cacheKey);
        if (cached.isPresent()) {
            return objectMapper.readTree(cached.get());
        }

        List<Object> filters = new ArrayList<>();
        if (therapeuticClass != null && !therapeuticClass.isBlank()) {
            filters.add(Map.of("term", Map.of("therapeuticClass", therapeuticClass)));
        }
        if (minRiskScore != null) {
            filters.add(Map.of("range", Map.of("riskScore", Map.of("gte", minRiskScore))));
        }

        Map<String, Object> bool = new LinkedHashMap<>();
        bool.put("filter", filters);
        if (text != null && !text.isBlank()) {
            bool.put("must", List.of(Map.of("multi_match", Map.of(
                    "query", text,
                    "fields", List.of("name^3", "description^2", "commonSideEffects")
            ))));
        } else {
            bool.put("must", List.of(Map.of("match_all", Map.of())));
        }

        Map<String, Object> body = Map.of(
                "size", 20,
                "query", Map.of("bool", bool),
                "sort", List.of(Map.of("riskScore", Map.of("order", normalizeSort(sortDirection)))),
                "aggs", Map.of(
                        "by_prescription_type", Map.of("terms", Map.of("field", "prescriptionType")),
                        "avg_risk_score", Map.of("avg", Map.of("field", "riskScore")),
                        "reported_cases_stats", Map.of("stats", Map.of("field", "reportedCases"))
                )
        );
        JsonNode response = elasticsearch.search(ElasticsearchDocumentService.DRUGS_INDEX, body);
        redis.put(cacheKey, objectMapper.writeValueAsString(response), 300);
        return response;
    }

    public JsonNode reportsByRegion(String region, String severity, LocalDate from, LocalDate to) throws IOException {
        String cacheKey = "analytics:reports-region:" + region + ":" + severity + ":" + from + ":" + to;
        var cached = redis.get(cacheKey);
        if (cached.isPresent()) {
            return objectMapper.readTree(cached.get());
        }

        List<Object> filters = new ArrayList<>();
        if (region != null && !region.isBlank()) {
            filters.add(Map.of("term", Map.of("region", region)));
        }
        if (severity != null && !severity.isBlank()) {
            filters.add(Map.of("term", Map.of("severity", severity)));
        }
        Map<String, Object> range = new LinkedHashMap<>();
        if (from != null) {
            range.put("gte", from.toString());
        }
        if (to != null) {
            range.put("lte", to.toString());
        }
        if (!range.isEmpty()) {
            filters.add(Map.of("range", Map.of("eventDate", range)));
        }

        Map<String, Object> body = Map.of(
                "size", 20,
                "query", Map.of("bool", Map.of("filter", filters)),
                "sort", List.of(Map.of("eventDate", Map.of("order", "desc"))),
                "aggs", Map.of(
                        "by_severity", Map.of("terms", Map.of("field", "severity")),
                        "avg_patient_age", Map.of("avg", Map.of("field", "patientAge")),
                        "hospitalization_rate", Map.of("terms", Map.of("field", "hospitalizationRequired"))
                )
        );
        JsonNode response = elasticsearch.search(ElasticsearchDocumentService.REPORTS_INDEX, body);
        redis.put(cacheKey, objectMapper.writeValueAsString(response), 300);
        return response;
    }

    public JsonNode manufacturerSafety(String manufacturer, String reactionType, Integer minReports) throws IOException {
        String cacheKey = "analytics:manufacturer-safety:" + manufacturer + ":" + reactionType + ":" + minReports;
        var cached = redis.get(cacheKey);
        if (cached.isPresent()) {
            return objectMapper.readTree(cached.get());
        }

        List<Object> filters = new ArrayList<>();
        if (manufacturer != null && !manufacturer.isBlank()) {
            filters.add(Map.of("term", Map.of("manufacturer", manufacturer)));
        }
        if (minReports != null) {
            filters.add(Map.of("range", Map.of("reportedCases", Map.of("gte", minReports))));
        }
        if (reactionType != null && !reactionType.isBlank()) {
            filters.add(Map.of("term", Map.of("commonSideEffects", reactionType)));
        }

        Map<String, Object> body = Map.of(
                "size", 20,
                "query", Map.of("bool", Map.of("filter", filters)),
                "sort", List.of(Map.of("reportedCases", Map.of("order", "desc"))),
                "aggs", Map.of(
                        "by_therapeutic_class", Map.of("terms", Map.of("field", "therapeuticClass")),
                        "avg_risk_score", Map.of("avg", Map.of("field", "riskScore")),
                        "top_reported_drugs", Map.of("terms", Map.of("field", "name.keyword", "size", 10))
                )
        );
        JsonNode response = elasticsearch.search(ElasticsearchDocumentService.DRUGS_INDEX, body);
        redis.put(cacheKey, objectMapper.writeValueAsString(response), 300);
        return response;
    }

    private String normalizeSort(String sortDirection) {
        return "asc".equalsIgnoreCase(sortDirection) ? "asc" : "desc";
    }
}
