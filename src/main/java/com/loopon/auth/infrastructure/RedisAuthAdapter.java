package com.loopon.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisAuthAdapter {
    private final StringRedisTemplate redisTemplate;

    private static final String RESET_TOKEN_PREFIX = "RESET_TOKEN:";
    private static final String RATE_LIMIT_PREFIX = "RATE_LIMIT:";

    private static final long RESET_TOKEN_TTL = 10;
    private static final long RATE_LIMIT_TTL = 5;
    private static final int MAX_ATTEMPTS = 5;

    public void saveResetToken(String email, String token) {
        redisTemplate.opsForValue()
                .set(RESET_TOKEN_PREFIX + email, token, Duration.ofMinutes(RESET_TOKEN_TTL));
    }

    public String getResetToken(String email) {
        String token = redisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + email);
        if (token != null) {
            redisTemplate.delete(RESET_TOKEN_PREFIX + email);
        }
        return token;
    }

    public boolean isRateLimitExceeded(String email) {
        String key = RATE_LIMIT_PREFIX + email;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(RATE_LIMIT_TTL));
        }

        return count != null && count > MAX_ATTEMPTS;
    }
}
