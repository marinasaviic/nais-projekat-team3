package rs.ac.uns.acs.nais.AdverseEffectsSearchService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.KeyValueEntry;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.RedisKeyValueService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/key-value")
public class RedisKeyValueController {
    private final RedisKeyValueService redis;

    public RedisKeyValueController(RedisKeyValueService redis) {
        this.redis = redis;
    }

    @PostMapping
    public ResponseEntity<Void> put(@RequestBody KeyValueEntry entry) {
        redis.put(entry.getKey(), entry.getValue(), entry.getTtlSeconds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {
        return redis.get(key)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{key}/ttl")
    public ResponseEntity<Long> ttl(@PathVariable String key) {
        return ResponseEntity.ok(redis.getTtlSeconds(key));
    }

    @GetMapping("/{key}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable String key) {
        return ResponseEntity.ok(redis.exists(key));
    }

    @GetMapping("/keys")
    public ResponseEntity<Set<String>> keys(@RequestParam(defaultValue = "*") String pattern) {
        return ResponseEntity.ok(redis.getKeysByPattern(pattern));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        redis.delete(key);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/hash/{key}")
    public ResponseEntity<Void> putHash(@PathVariable String key, @RequestBody Map<String, String> fields) {
        fields.forEach((field, value) -> redis.putHashValue(key, field, value));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hash/{key}")
    public ResponseEntity<Map<String, String>> getHash(@PathVariable String key) {
        Map<String, String> hash = redis.getHash(key);
        return hash.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(hash);
    }

    @GetMapping("/hash/{key}/{field}")
    public ResponseEntity<String> getHashField(@PathVariable String key, @PathVariable String field) {
        return redis.getHashValue(key, field)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/hash/{key}/{field}")
    public ResponseEntity<Void> deleteHashField(@PathVariable String key, @PathVariable String field) {
        redis.deleteHashField(key, field);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cache-statistics")
    public ResponseEntity<Map<String, String>> cacheStatistics() {
        return ResponseEntity.ok(redis.getHash("cache:statistics"));
    }
}
