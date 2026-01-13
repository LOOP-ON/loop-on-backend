package com.loopon.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "rt", timeToLive = 1209600)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Builder
public class RefreshToken {

    @Id
    private String email;

    private String token;

    private String role;

    private Long userId;

    public void rotate(String newToken) {
        this.token = newToken;
    }
}
