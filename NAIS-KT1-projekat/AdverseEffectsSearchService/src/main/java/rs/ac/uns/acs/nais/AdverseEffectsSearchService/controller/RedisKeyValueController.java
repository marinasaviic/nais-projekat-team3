package rs.ac.uns.acs.nais.AdverseEffectsSearchService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.KeyValueEntry;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.RedisKeyValueService;

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

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        redis.delete(key);
        return ResponseEntity.noContent().build();
    }
}
