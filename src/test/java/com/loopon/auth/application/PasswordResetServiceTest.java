package com.loopon.auth.application;

import com.loopon.auth.application.dto.request.PasswordResetRequest;
import com.loopon.auth.domain.Verification;
import com.loopon.auth.domain.VerificationPurpose;
import com.loopon.auth.domain.repository.VerificationRepository;
import com.loopon.auth.infrastructure.RedisAuthAdapter;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private RedisAuthAdapter redisAuthAdapter;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String EMAIL = "test@loopon.com";

    @Nested
    @DisplayName("비밀번호 재설정")
    class ResetPassword {

        @Test
        @DisplayName("성공: 리셋 토큰이 유효하고 인증 내역이 존재하면 비밀번호를 변경한다")
        void 비밀번호_재설정_성공() {
            // given
            String token = "valid-token-uuid";
            String newPassword = "newPassword123!";
            PasswordResetRequest request = new PasswordResetRequest(EMAIL, token, newPassword);

            User user = User.createLocalUser(EMAIL, "loopon", "oldEncoded", null);
            Verification mockVerification = Verification.of(EMAIL, "1234", VerificationPurpose.PASSWORD_RESET, null);

            ReflectionTestUtils.setField(mockVerification, "status", com.loopon.auth.domain.VerificationStatus.VERIFIED);

            given(redisAuthAdapter.getResetToken(EMAIL)).willReturn(token);
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));
            given(passwordEncoder.encode(newPassword)).willReturn("newEncoded");

            given(verificationRepository.findLatest(eq(EMAIL), eq(VerificationPurpose.PASSWORD_RESET)))
                    .willReturn(Optional.of(mockVerification));

            // when
            passwordResetService.resetPassword(request);

            // then
            assertThat(user.getPassword()).isEqualTo("newEncoded");
            assertThat(mockVerification.getStatus()).isEqualTo(com.loopon.auth.domain.VerificationStatus.USED);
        }

        @Test
        @DisplayName("실패: 리셋 토큰이 없거나 일치하지 않으면 예외가 발생한다")
        void 비밀번호_재설정_실패_토큰불일치() {
            // given
            String token = "invalid-token";
            PasswordResetRequest request = new PasswordResetRequest(EMAIL, token, "pw123");

            given(redisAuthAdapter.getResetToken(EMAIL)).willReturn("other-token");

            // when & then
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_RESET_TOKEN);
        }

        @Test
        @DisplayName("실패: 유저가 존재하지 않으면 예외가 발생한다")
        void 비밀번호_재설정_실패_유저없음() {
            // given
            String token = "valid-token";
            PasswordResetRequest request = new PasswordResetRequest(EMAIL, token, "pw123");

            given(redisAuthAdapter.getResetToken(EMAIL)).willReturn(token);
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 인증 내역(Verification)을 찾을 수 없으면 예외가 발생한다 (엄격한 일관성)")
        void 비밀번호_재설정_실패_인증내역_없음() {
            // given
            String token = "valid-token";
            PasswordResetRequest request = new PasswordResetRequest(EMAIL, token, "newPw");
            User user = User.createLocalUser(EMAIL, "loopon", "oldEncoded", null);

            given(redisAuthAdapter.getResetToken(EMAIL)).willReturn(token);
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));
            given(passwordEncoder.encode("newPw")).willReturn("newEncoded");

            given(verificationRepository.findLatest(eq(EMAIL), eq(VerificationPurpose.PASSWORD_RESET)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.VERIFICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 인증 상태가 VERIFIED가 아니면 markAsUsed 호출 시 예외가 발생한다")
        void 비밀번호_재설정_실패_상태이상() {
            // given
            String token = "valid-token";
            PasswordResetRequest request = new PasswordResetRequest(EMAIL, token, "newPw");
            User user = User.createLocalUser(EMAIL, "loopon", "oldEncoded", null);

            Verification notVerified = Verification.of(EMAIL, "1234", VerificationPurpose.PASSWORD_RESET, null);
            ReflectionTestUtils.setField(notVerified, "status", com.loopon.auth.domain.VerificationStatus.PENDING);

            given(redisAuthAdapter.getResetToken(EMAIL)).willReturn(token);
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));
            given(passwordEncoder.encode("newPw")).willReturn("newEncoded");
            given(verificationRepository.findLatest(eq(EMAIL), eq(VerificationPurpose.PASSWORD_RESET)))
                    .willReturn(Optional.of(notVerified));

            // when & then
            assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.VERIFICATION_NOT_VERIFIED);
        }
    }
}
