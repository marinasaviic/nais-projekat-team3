package rs.ac.uns.acs.nais.AdverseEffectsSearchService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.DrugDocument;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.ElasticsearchDocumentService;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.RedisKeyValueService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {

    private final ElasticsearchDocumentService elasticsearch;
    private final RedisKeyValueService redis;
    private final ObjectMapper objectMapper;

    public DrugController(ElasticsearchDocumentService elasticsearch,
                          RedisKeyValueService redis,
                          ObjectMapper objectMapper) {
        this.elasticsearch = elasticsearch;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public JsonNode create(@RequestBody DrugDocument drug) throws IOException {
        JsonNode result = elasticsearch.create(ElasticsearchDocumentService.DRUGS_INDEX, drug.getId(), drug);
        invalidateDrugCache();
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonNode> get(@PathVariable String id) throws IOException {
        String cacheKey = "drug:" + id;
        var cached = redis.get(cacheKey);
        if (cached.isPresent()) {
            redis.recordCacheHit("drug");
            return ResponseEntity.ok(objectMapper.readTree(cached.get()));
        }
        redis.recordCacheMiss("drug");
        return elasticsearch.get(ElasticsearchDocumentService.DRUGS_INDEX, id)
                .map(node -> {
                    try {
                        redis.put(cacheKey, objectMapper.writeValueAsString(node), 600);
                    } catch (Exception ignored) {}
                    return ResponseEntity.ok(node);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public JsonNode update(@PathVariable String id, @RequestBody DrugDocument drug) throws IOException {
        drug.setId(id);
        JsonNode result = elasticsearch.update(ElasticsearchDocumentService.DRUGS_INDEX, id, drug);
        redis.delete("drug:" + id);
        invalidateDrugCache();
        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws IOException {
        elasticsearch.delete(ElasticsearchDocumentService.DRUGS_INDEX, id);
        redis.delete("drug:" + id);
        invalidateDrugCache();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public JsonNode list(@RequestParam(defaultValue = "20") int size) throws IOException {
        return elasticsearch.search(ElasticsearchDocumentService.DRUGS_INDEX,
                Map.of("size", size, "query", Map.of("match_all", Map.of())));
    }

    private void invalidateDrugCache() {
        Set<String> keys = redis.getKeysByPattern("analytics:drug-risk:*");
        keys.forEach(redis::delete);
        Set<String> safetyKeys = redis.getKeysByPattern("analytics:manufacturer-safety:*");
        safetyKeys.forEach(redis::delete);
    }
}
