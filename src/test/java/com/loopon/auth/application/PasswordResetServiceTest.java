package com.loopon.auth.application;

import com.loopon.auth.application.dto.request.PasswordEmailRequest;
import com.loopon.auth.application.dto.request.PasswordResetRequest;
import com.loopon.auth.application.dto.request.PasswordVerifyRequest;
import com.loopon.auth.infrastructure.RedisAuthAdapter;
import com.loopon.auth.infrastructure.RefreshTokenRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.mail.MailService;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisAuthAdapter redisAuthAdapter;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private static final String EMAIL = "test@loopon.com";

    @Nested
    @DisplayName("인증 코드 발송")
    class SendAuthCode {

        @Test
        @DisplayName("성공: 가입된 이메일이면 인증 코드를 저장하고 메일을 발송한다")
        void 인증코드_발송_성공() {
            // given
            PasswordEmailRequest request = new PasswordEmailRequest(EMAIL);
            given(userRepository.existsByEmail(EMAIL)).willReturn(true);

            // when
            passwordResetService.sendAuthCode(request);

            // then
            verify(redisAuthAdapter).saveAuthCode(eq(EMAIL), anyString());
            verify(mailService).sendAuthCode(eq(EMAIL), anyString());
        }

        @Test
        @DisplayName("성공(보안): 가입되지 않은 이메일이어도 예외를 던지지 않고 조용히 종료된다 (계정 열거 방지)")
        void 인증코드_발송_미가입_이메일_SilentFail() {
            // given
            String unknownEmail = "unknown@loopon.com";
            PasswordEmailRequest request = new PasswordEmailRequest(unknownEmail);

            given(redisAuthAdapter.isRateLimitExceeded(unknownEmail)).willReturn(false);

            given(userRepository.existsByEmail(unknownEmail)).willReturn(false);

            // when
            passwordResetService.sendAuthCode(request);

            // then
            verify(userRepository).existsByEmail(unknownEmail);

            verify(redisAuthAdapter, never()).saveAuthCode(anyString(), anyString());
            verify(mailService, never()).sendAuthCode(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("인증 코드 검증")
    class VerifyAuthCode {

        @Test
        @DisplayName("성공: 인증 코드가 일치하면 리셋 토큰을 발급하고 인증 코드를 삭제한다")
        void 인증코드_검증_성공() {
            // given
            String code = "1234";
            PasswordVerifyRequest request = new PasswordVerifyRequest(EMAIL, code);

            given(redisAuthAdapter.getAuthCode(EMAIL)).willReturn(code);

            // when
            String resetToken = passwordResetService.verifyAuthCode(request);

            // then
            assertThat(resetToken).isNotNull();
            verify(redisAuthAdapter).saveResetToken(eq(EMAIL), anyString());
            verify(redisAuthAdapter).deleteAuthCode(EMAIL);
        }

        @Test
        @DisplayName("실패: 인증 코드가 만료되었거나(null) 일치하지 않으면 예외가 발생한다")
        void 인증코드_검증_실패_코드_불일치() {
            // given
            String wrongCode = "9999";
            PasswordVerifyRequest request = new PasswordVerifyRequest(EMAIL, wrongCode);

            given(redisAuthAdapter.getAuthCode(EMAIL)).willReturn("1234");

            // when & then
            assertThatThrownBy(() -> passwordResetService.verifyAuthCode(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.AUTH_CODE_INVALID);

            verify(redisAuthAdapter, never()).saveResetToken(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정")
    class ResetPassword {

        @Test
        @DisplayName("성공: 토큰이 유효하고 비밀번호가 일치하면 비밀번호를 변경하고 모든 세션을 만료시킨다")
        void 비밀번호_재설정_성공() {
            // given
            String newPassword = "newPassword123!";
            String token = "valid-token-uuid";

            PasswordResetRequest request = new PasswordResetRequest(EMAIL, token, newPassword, newPassword);
            User user = User.createLocalUser(
                    EMAIL,
                    "loopon",
                    "oldEncodedPassword",
                    null
            );

            given(redisAuthAdapter.getResetToken(EMAIL)).willReturn(token);
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.ofNullable(user));
            given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPassword");

            // when
            passwordResetService.resetPassword(request);

            // then
            assertThat(user.getPassword()).isEqualTo("encodedNewPassword");

            verify(refreshTokenRepository).deleteById(EMAIL);
        }

        @Test
        @DisplayName("실패: 새 비밀번호와 확인 비밀번호가 다르면 예외가 발생한다")
        void 비밀번호_재설정_실패_비밀번호_불일치() {
            // given
            PasswordResetRequest request = new PasswordResetRequest(EMAIL, "token", "pw1", "pw2");

            // when & then
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.PASSWORD_MISMATCH);
        }

        @Test
        @DisplayName("실패: 재설정 토큰이 없거나 일치하지 않으면 예외가 발생한다")
        void 비밀번호_재설정_실패_토큰_유효하지_않음() {
            // given
            String token = "invalid-token";
            PasswordResetRequest request = new PasswordResetRequest(EMAIL, token, "pw123", "pw123");

            given(redisAuthAdapter.getResetToken(EMAIL)).willReturn("other-token");

            // when & then
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.RESET_TOKEN_INVALID);

            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("실패: 토큰 검증은 통과했으나 유저가 존재하지 않는 경우(중간에 삭제됨) 예외가 발생한다")
        void 비밀번호_재설정_실패_유저_없음() {
            // given
            String token = "valid-token";
            PasswordResetRequest request = new PasswordResetRequest(EMAIL, token, "pw123!", "pw123!");

            given(redisAuthAdapter.getResetToken(EMAIL)).willReturn(token);

            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }
}
