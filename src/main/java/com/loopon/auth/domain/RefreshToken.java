package com.loopon.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "rt", timeToLive = RefreshToken.REFRESH_TOKEN_TTL_SECONDS)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Builder
public class RefreshToken {
    public static final long REFRESH_TOKEN_TTL_SECONDS = 60L * 60 * 24 * 14;

    @Id
    private String email;

    private String token;

    private String role;

    private Long userId;

    public void rotate(String newToken) {
        this.token = newToken;
    }
}
