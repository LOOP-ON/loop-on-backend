package com.loopon.user.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.application.dto.command.UserSignUpCommand;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @InjectMocks
    private UserCommandService userCommandService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        private UserSignUpCommand createCommand(String email, String password, String confirmPassword, String nickname) {
            return new UserSignUpCommand(
                    email, password, confirmPassword, "홍길동", nickname, LocalDate.of(2000, 1, 1)
            );
        }

        @Test
        @DisplayName("성공: 모든 검증을 통과하면 회원을 저장하고 ID를 반환한다")
        void 회원가입_성공_모든_검증_통과() {
            // given
            UserSignUpCommand command = createCommand("test@loopon.com", "pw123", "pw123", "loopon");

            given(userRepository.existsByEmail(command.email())).willReturn(false);
            given(userRepository.existsByNickname(command.nickname())).willReturn(false);
            given(passwordEncoder.encode(command.password())).willReturn("encodedPassword");

            given(userRepository.save(any(User.class))).willReturn(1L);

            // when
            Long signedUpId = userCommandService.signUp(command);

            // then
            assertThat(signedUpId).isEqualTo(1L);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 비밀번호와 확인 비밀번호가 다르면 예외가 발생한다")
        void 회원가입_실패_비밀번호_비밀번호_확인_불일치() {
            // given
            UserSignUpCommand command = createCommand("test@loopon.com", "pw123", "pw999", "loopon");

            // when & then
            assertThatThrownBy(() -> userCommandService.signUp(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_MISMATCH);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 이미 존재하는 이메일이면 예외가 발생한다")
        void 회원가입_실패_이미_사용_중인_이메일() {
            // given
            UserSignUpCommand command = createCommand("exist@loopon.com", "pw123", "pw123", "loopon");
            given(userRepository.existsByEmail(command.email())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userCommandService.signUp(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("실패: 이미 존재하는 닉네임이면 예외가 발생한다")
        void 회원가입_실패_이미_사용_중인_닉네임() {
            // given
            UserSignUpCommand command = createCommand("test@loopon.com", "pw123", "pw123", "existNick");
            given(userRepository.existsByEmail(command.email())).willReturn(false);
            given(userRepository.existsByNickname(command.nickname())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userCommandService.signUp(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }
}
