package com.loopon.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash(value = "rt", timeToLive = RefreshToken.REFRESH_TOKEN_TTL_SECONDS)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Builder
public class RefreshToken implements Serializable {
    public static final long REFRESH_TOKEN_TTL_SECONDS = 60L * 60 * 24 * 14;

    @Id
    private String email;

    @Indexed
    private String token;

    private String role;

    private Long userId;

    public RefreshToken rotate(String newToken) {
        return RefreshToken.builder()
                .email(this.email)
                .userId(this.userId)
                .role(this.role)
                .token(newToken)
                .build();
    }
}
