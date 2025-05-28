package site.kimnow.toy.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

import static site.kimnow.toy.common.constant.Constants.REDIS_REFRESH_PREFIX;
import static site.kimnow.toy.common.constant.Constants.REDIS_USER_ROLE_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleRedisService {
    private final RedissonClient redissonClient;

    public void save(String userId, String role, Duration ttl) {
        String key = REDIS_USER_ROLE_PREFIX + userId;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(role, ttl);
    }

    public Optional<String> get(String userId) {
        String key = REDIS_USER_ROLE_PREFIX + userId;
        RBucket<String> bucket = redissonClient.getBucket(key);
        return Optional.ofNullable(bucket.get());
    }

    public void delete(String userId) {
        String key = REDIS_USER_ROLE_PREFIX + userId;
        redissonClient.getBucket(key).delete();
    }
}
