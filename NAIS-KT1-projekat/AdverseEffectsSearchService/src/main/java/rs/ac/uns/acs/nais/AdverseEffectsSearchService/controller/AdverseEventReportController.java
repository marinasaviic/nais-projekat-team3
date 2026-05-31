package rs.ac.uns.acs.nais.AdverseEffectsSearchService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.AdverseEventReportDocument;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.ElasticsearchDocumentService;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.RedisKeyValueService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/adverse-event-reports")
public class AdverseEventReportController {

    private final ElasticsearchDocumentService elasticsearch;
    private final RedisKeyValueService redis;
    private final ObjectMapper objectMapper;

    public AdverseEventReportController(ElasticsearchDocumentService elasticsearch,
                                        RedisKeyValueService redis,
                                        ObjectMapper objectMapper) {
        this.elasticsearch = elasticsearch;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public JsonNode create(@RequestBody AdverseEventReportDocument report) throws IOException {
        JsonNode result = elasticsearch.create(ElasticsearchDocumentService.REPORTS_INDEX, report.getId(), report);
        invalidateReportCache();
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonNode> get(@PathVariable String id) throws IOException {
        String cacheKey = "report:" + id;
        var cached = redis.get(cacheKey);
        if (cached.isPresent()) {
            redis.recordCacheHit("report");
            return ResponseEntity.ok(objectMapper.readTree(cached.get()));
        }
        redis.recordCacheMiss("report");
        return elasticsearch.get(ElasticsearchDocumentService.REPORTS_INDEX, id)
                .map(node -> {
                    try {
                        redis.put(cacheKey, objectMapper.writeValueAsString(node), 600);
                    } catch (Exception ignored) {}
                    return ResponseEntity.ok(node);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public JsonNode update(@PathVariable String id, @RequestBody AdverseEventReportDocument report) throws IOException {
        report.setId(id);
        JsonNode result = elasticsearch.update(ElasticsearchDocumentService.REPORTS_INDEX, id, report);
        redis.delete("report:" + id);
        invalidateReportCache();
        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws IOException {
        elasticsearch.delete(ElasticsearchDocumentService.REPORTS_INDEX, id);
        redis.delete("report:" + id);
        invalidateReportCache();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public JsonNode list(@RequestParam(defaultValue = "20") int size) throws IOException {
        return elasticsearch.search(ElasticsearchDocumentService.REPORTS_INDEX,
                Map.of("size", size, "query", Map.of("match_all", Map.of())));
    }

    private void invalidateReportCache() {
        Set<String> keys = redis.getKeysByPattern("analytics:reports-region:*");
        keys.forEach(redis::delete);
    }
}
