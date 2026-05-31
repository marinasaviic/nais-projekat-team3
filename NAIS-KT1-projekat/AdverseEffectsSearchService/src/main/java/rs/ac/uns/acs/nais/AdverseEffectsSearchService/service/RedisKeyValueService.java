package rs.ac.uns.acs.nais.AdverseEffectsSearchService.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisKeyValueService {
    private final StringRedisTemplate redisTemplate;

    public RedisKeyValueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void put(String key, String value, long ttlSeconds) {
        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
