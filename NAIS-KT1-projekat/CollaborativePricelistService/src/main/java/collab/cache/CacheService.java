package collab.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class CacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public <T> Optional<T> get(String key, Class<T> valueType) {
        String v = redisTemplate.opsForValue().get(key);
        if (v == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(v, valueType));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public void set(String key, Object value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (JsonProcessingException e) {
            // ignore serialization errors for now
        }
    }

    public void del(String key) {
        redisTemplate.delete(key);
    }
}
