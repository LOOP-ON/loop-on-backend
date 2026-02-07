package com.loopon.notification.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeLikeAggregationRedisRepository {

    private final StringRedisTemplate redis;

    public long increment(Long challengeId) {
        Long v = redis.opsForValue().increment(key(challengeId));
        return v == null ? 0L : v;
    }

    public Integer get(Long challengeId) {
        String v = redis.opsForValue().get(key(challengeId));
        return v == null ? null : Integer.parseInt(v);
    }

    public void clear(Long challengeId) {
        redis.delete(key(challengeId));
    }

    private String key(Long challengeId) {
        return RedisKeys.CHALLENGE_LIKE + challengeId;
    }
}