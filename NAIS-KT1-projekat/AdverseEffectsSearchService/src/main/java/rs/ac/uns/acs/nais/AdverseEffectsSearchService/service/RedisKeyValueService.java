package rs.ac.uns.acs.nais.AdverseEffectsSearchService.service;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisKeyValueService {
    private final StringRedisTemplate redisTemplate;

    public RedisKeyValueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void put(String key, String value, long ttlSeconds) {
        validateKey(key);
        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    public Optional<String> get(String key) {
        validateKey(key);
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void delete(String key) {
        validateKey(key);
        redisTemplate.delete(key);
    }

    public void putHashValue(String key, String field, String value) {
        validateKey(key);
        validateField(field);
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Optional<String> getHashValue(String key, String field) {
        validateKey(key);
        validateField(field);
        Object value = redisTemplate.opsForHash().get(key, field);
        return Optional.ofNullable(value).map(Object::toString);
    }

    public Map<String, String> getHash(String key) {
        validateKey(key);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Map<String, String> result = new LinkedHashMap<>();
        entries.forEach((field, value) -> result.put(field.toString(), value.toString()));
        return result;
    }

    public void deleteHashField(String key, String field) {
        validateKey(key);
        validateField(field);
        redisTemplate.opsForHash().delete(key, field);
    }

    public long incrementHashValue(String key, String field, long delta) {
        validateKey(key);
        validateField(field);
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    public void recordCacheHit(String cacheName) {
        incrementHashValue("cache:statistics", cacheName + ":hit", 1);
    }

    public void recordCacheMiss(String cacheName) {
        incrementHashValue("cache:statistics", cacheName + ":miss", 1);
    }

    public long getTtlSeconds(String key) {
        validateKey(key);
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : -2L;
    }

    public boolean exists(String key) {
        validateKey(key);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Set<String> getKeysByPattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            throw new IllegalArgumentException("Redis key pattern must not be blank");
        }

        Set<String> matchingKeys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(matchingKeys::add);
        }

        return matchingKeys;
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Redis key must not be blank");
        }
    }

    private void validateField(String field) {
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException("Redis hash field must not be blank");
        }
    }
}
