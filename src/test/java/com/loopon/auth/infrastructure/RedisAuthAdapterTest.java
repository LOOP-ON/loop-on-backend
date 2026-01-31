package com.loopon.auth.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @Nested
    @DisplayName("비밀번호 재설정 토큰 (Reset Token)")
    class ResetTokenTest {

        @Test
        @DisplayName("저장: Key 접두사(RESET_TOKEN:)와 TTL(10분)이 정확하게 설정되어야 한다")
        void saveResetToken_검증() {
            // given
            String email = "test@loopon.com";
            String token = "uuid-token";

            // when
            redisAuthAdapter.saveResetToken(email, token);

            // then
            verify(valueOperations).set(
                    eq("RESET_TOKEN:" + email),
                    eq(token),
                    eq(Duration.ofMinutes(10))
            );
        }

        @Test
        @DisplayName("조회(Read-Once): 토큰이 존재하면 조회 후 즉시 삭제되어야 한다 (보안 강화)")
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
        @DisplayName("조회: 토큰이 없으면 null을 반환하고 삭제를 시도하지 않아야 한다")
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

    @Nested
    @DisplayName("요청 횟수 제한 (Rate Limit)")
    class RateLimitTest {

        @Test
        @DisplayName("첫 요청: 카운트가 1이면 만료 시간(TTL)을 설정하고 false(제한 미달)를 반환한다")
        void isRateLimitExceeded_첫요청() {
            // given
            String email = "test@loopon.com";
            String key = "RATE_LIMIT:" + email;

            given(valueOperations.increment(key)).willReturn(1L);

            // when
            boolean isExceeded = redisAuthAdapter.isRateLimitExceeded(email);

            // then
            assertThat(isExceeded).isFalse();

            verify(redisTemplate).expire(eq(key), any(Duration.class));
        }

        @Test
        @DisplayName("반복 요청: 카운트가 최대 횟수(5회) 이하라면 false를 반환하고, TTL은 재설정하지 않는다")
        void isRateLimitExceeded_반복요청_통과() {
            // given
            String email = "test@loopon.com";
            String key = "RATE_LIMIT:" + email;

            given(valueOperations.increment(key)).willReturn(5L);

            // when
            boolean isExceeded = redisAuthAdapter.isRateLimitExceeded(email);

            // then
            assertThat(isExceeded).isFalse();

            verify(redisTemplate, never()).expire(any(), any());
        }

        @Test
        @DisplayName("제한 초과: 카운트가 최대 횟수(5회)를 넘으면 true(제한 초과)를 반환한다")
        void isRateLimitExceeded_초과() {
            // given
            String email = "test@loopon.com";
            String key = "RATE_LIMIT:" + email;

            given(valueOperations.increment(key)).willReturn(6L);

            // when
            boolean isExceeded = redisAuthAdapter.isRateLimitExceeded(email);

            // then
            assertThat(isExceeded).isTrue();
        }
    }
}
