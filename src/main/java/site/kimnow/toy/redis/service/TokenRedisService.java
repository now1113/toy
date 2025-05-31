package site.kimnow.toy.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import site.kimnow.toy.common.constant.Constants;

import java.time.Duration;
import java.util.Optional;

import static site.kimnow.toy.common.constant.Constants.REDIS_REFRESH_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRedisService {
    private final RedissonClient redissonClient;

    public void save(String userId, String refreshToken, Duration ttl) {
        String key = REDIS_REFRESH_PREFIX + refreshToken;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(userId, ttl);
    }

    public Optional<String> get(String refreshToken) {
        String key = REDIS_REFRESH_PREFIX + refreshToken;
        RBucket<String> bucket = redissonClient.getBucket(key);
        return Optional.ofNullable(bucket.get());
    }

    public void delete(String userId) {
        String key = REDIS_REFRESH_PREFIX + userId;
        redissonClient.getBucket(key).delete();
    }
}
