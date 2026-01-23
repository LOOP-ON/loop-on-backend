package com.loopon.auth.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedisAuthAdapterTest {

    @InjectMocks
    private RedisAuthAdapter redisAuthAdapter;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    @DisplayName("인증 코드 저장: Key 접두사(AUTH_CODE:)와 TTL(3분)이 정확하게 설정되어야 한다")
    void saveAuthCode_검증() {
        // given
        String email = "test@loopon.com";
        String code = "1234";

        // when
        redisAuthAdapter.saveAuthCode(email, code);

        // then
        verify(valueOperations).set(
                eq("AUTH_CODE:" + email),
                eq(code),
                eq(Duration.ofMinutes(3))
        );
    }

    @Test
    @DisplayName("인증 코드 조회: Key 접두사를 포함하여 조회해야 한다")
    void getAuthCode_검증() {
        // given
        String email = "test@loopon.com";
        String expectedCode = "1234";
        given(valueOperations.get("AUTH_CODE:" + email)).willReturn(expectedCode);

        // when
        String actualCode = redisAuthAdapter.getAuthCode(email);

        // then
        assertThat(actualCode).isEqualTo(expectedCode);
        verify(valueOperations).get("AUTH_CODE:" + email);
    }

    @Test
    @DisplayName("재설정 토큰 조회(Read-Once): 토큰이 존재하면 조회 후 즉시 삭제되어야 한다")
    void getResetToken_조회_후_삭제_검증() {
        // given
        String email = "test@loopon.com";
        String token = "uuid-token";
        String key = "RESET_TOKEN:" + email;

        given(valueOperations.get(key)).willReturn(token);

        // when
        String actualToken = redisAuthAdapter.getResetToken(email);

        // then
        assertThat(actualToken).isEqualTo(token);

        verify(redisTemplate).delete(key);
    }

    @Test
    @DisplayName("재설정 토큰 조회: 토큰이 없으면 null을 반환하고 삭제를 시도하지 않아야 한다")
    void getResetToken_없으면_삭제안함_검증() {
        // given
        String email = "test@loopon.com";
        String key = "RESET_TOKEN:" + email;

        given(valueOperations.get(key)).willReturn(null);

        // when
        String actualToken = redisAuthAdapter.getResetToken(email);

        // then
        assertThat(actualToken).isNull();

         verify(redisTemplate, never()).delete(key);
    }
}
