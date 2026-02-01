package com.loopon.auth.application;

import com.loopon.auth.domain.Verification;
import com.loopon.auth.domain.VerificationPurpose;
import com.loopon.auth.domain.VerificationStatus;
import com.loopon.auth.domain.repository.VerificationRepository;
import com.loopon.auth.infrastructure.RedisAuthAdapter;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @InjectMocks
    private VerificationService verificationService;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisAuthAdapter redisAuthAdapter;

    private static final String EMAIL = "test@loopon.com";

    @Nested
    @DisplayName("인증 코드 발송 (SendCode)")
    class SendCode {

        @Test
        @DisplayName("성공: 요청 횟수 제한을 통과하면 DB에 저장하고 이메일을 비동기 발송한다")
        void 인증코드_발송_성공() {
            // given
            given(redisAuthAdapter.isRateLimitExceeded(EMAIL)).willReturn(false);

            // when
            verificationService.sendCode(EMAIL, VerificationPurpose.PASSWORD_RESET);

            // then
            verify(verificationRepository).save(any(Verification.class));
            verify(emailService).sendVerificationEmail(eq(EMAIL), anyString(), eq(VerificationPurpose.PASSWORD_RESET));
        }

        @Test
        @DisplayName("실패: 요청 횟수가 초과되면 예외가 발생하고 메일을 보내지 않는다")
        void 인증코드_발송_실패_RateLimit() {
            // given
            given(redisAuthAdapter.isRateLimitExceeded(EMAIL)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> verificationService.sendCode(EMAIL, VerificationPurpose.PASSWORD_RESET))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOO_MANY_REQUESTS);

            verify(verificationRepository, never()).save(any());
            verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), any());
        }
    }

    @Nested
    @DisplayName("인증 코드 검증 (VerifyCode)")
    class VerifyCode {

        @Test
        @DisplayName("성공: 코드가 일치하고 만료되지 않았으면 검증 처리하고, 리셋 토큰을 반환한다")
        void 인증코드_검증_성공() {
            // given
            String code = "1234";

            Verification mockVerification = Verification.of(EMAIL, code, VerificationPurpose.PASSWORD_RESET);
            ReflectionTestUtils.setField(mockVerification, "id", 1L);
            ReflectionTestUtils.setField(mockVerification, "createdAt", LocalDateTime.now().minusMinutes(1));

            given(verificationRepository.findLatest(eq(EMAIL), eq(VerificationPurpose.PASSWORD_RESET)))
                    .willReturn(Optional.of(mockVerification));

            // when
            String resetToken = verificationService.verifyCode(EMAIL, code, VerificationPurpose.PASSWORD_RESET);

            // then
            assertThat(resetToken).isNotNull();
            verify(redisAuthAdapter).saveResetToken(eq(EMAIL), anyString());
        }

        @Test
        @DisplayName("실패: 인증 내역이 없으면 예외가 발생한다")
        void 인증코드_검증_실패_내역없음() {
            // given
            given(verificationRepository.findLatest(anyString(), any()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> verificationService.verifyCode(EMAIL, "1234", VerificationPurpose.PASSWORD_RESET))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VERIFICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 코드가 불일치하면 엔티티에서 예외를 던진다")
        void 인증코드_검증_실패_코드불일치() {
            // given
            String realCode = "1234";
            String wrongCode = "9999";
            Verification mockVerification = Verification.of(EMAIL, realCode, VerificationPurpose.PASSWORD_RESET);

            given(verificationRepository.findLatest(anyString(), any()))
                    .willReturn(Optional.of(mockVerification));

            // when & then
            assertThatThrownBy(() -> verificationService.verifyCode(EMAIL, wrongCode, VerificationPurpose.PASSWORD_RESET))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        @Test
        @DisplayName("실패: 인증 코드의 유효 시간(5분)이 지났으면 예외가 발생한다")
        void 인증코드_검증_실패_만료됨() {
            // given
            String code = "1234";
            Verification mockVerification = Verification.of(EMAIL, code, VerificationPurpose.PASSWORD_RESET);

            ReflectionTestUtils.setField(mockVerification, "expiresAt", LocalDateTime.now().minusSeconds(1));


            given(verificationRepository.findLatest(eq(EMAIL), eq(VerificationPurpose.PASSWORD_RESET)))
                    .willReturn(Optional.of(mockVerification));

            // when & then
            assertThatThrownBy(() -> verificationService.verifyCode(EMAIL, code, VerificationPurpose.PASSWORD_RESET))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.VERIFICATION_EXPIRED);
        }

        @Test
        @DisplayName("실패: 이미 검증이 완료된(VERIFIED) 코드를 다시 요청하면 예외가 발생한다")
        void 인증코드_검증_실패_이미완료됨() {
            // given
            String code = "1234";
            Verification mockVerification = Verification.of(EMAIL, code, VerificationPurpose.PASSWORD_RESET);

            ReflectionTestUtils.setField(mockVerification, "status", VerificationStatus.VERIFIED);

            given(verificationRepository.findLatest(eq(EMAIL), eq(VerificationPurpose.PASSWORD_RESET)))
                    .willReturn(Optional.of(mockVerification));

            // when & then
            assertThatThrownBy(() -> verificationService.verifyCode(EMAIL, code, VerificationPurpose.PASSWORD_RESET))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VERIFICATION_ALREADY_COMPLETED);
        }
    }
}
