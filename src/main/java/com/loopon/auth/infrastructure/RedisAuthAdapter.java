package com.loopon.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisAuthAdapter {
    private final StringRedisTemplate redisTemplate;

    private static final String AUTH_CODE_PREFIX = "AUTH_CODE:";
    private static final String RESET_TOKEN_PREFIX = "RESET_TOKEN:";
    private static final long AUTH_CODE_TTL = 3;
    private static final long RESET_TOKEN_TTL = 10;

    public void saveAuthCode(String email, String code) {
        redisTemplate.opsForValue()
                .set(AUTH_CODE_PREFIX + email, code, Duration.ofMinutes(AUTH_CODE_TTL));
    }

    public String getAuthCode(String email) {
        return redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);
    }

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

    public void deleteAuthCode(String email) {
        redisTemplate.delete(AUTH_CODE_PREFIX + email);
    }
}
