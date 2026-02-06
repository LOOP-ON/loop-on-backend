package com.loopon.user.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.term.domain.Term;
import com.loopon.term.domain.repository.TermRepository;
import com.loopon.term.domain.repository.UserTermAgreementRepository;
import com.loopon.user.application.dto.command.UpdateProfileCommand;
import com.loopon.user.application.dto.command.UserSignUpCommand;
import com.loopon.user.application.dto.response.UserProfileResponse;
import com.loopon.user.domain.User;
import com.loopon.user.domain.UserVisibility;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    private TermRepository termRepository;

    @Mock
    private UserTermAgreementRepository userTermAgreementRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        private UserSignUpCommand createCommand(String email, String password, String confirmPassword, String nickname, List<Long> agreedTermIds) {
            return UserSignUpCommand.of(
                    email, password, confirmPassword, "홍길동", nickname, agreedTermIds
            );
        }

        private Term createTerm(Long id, boolean mandatory) {
            Term term = Term.builder()
                    .title("테스트 약관")
                    .content("내용")
                    .mandatory(mandatory)
                    .version("1.0")
                    .build();
            ReflectionTestUtils.setField(term, "id", id);
            return term;
        }

        @Test
        @DisplayName("성공: 필수 약관을 모두 동의하고 검증을 통과하면 회원을 저장한다")
        void 회원가입_성공_모든_검증_통과() {
            // given
            Term mandatoryTerm = createTerm(1L, true);
            Term optionalTerm = createTerm(2L, false);
            given(termRepository.findAllForSignUp()).willReturn(List.of(mandatoryTerm, optionalTerm));

            UserSignUpCommand command = createCommand(
                    "test@loopon.com", "pw123", "pw123", "loopon", List.of(1L, 2L)
            );

            given(userRepository.existsByEmail(command.email())).willReturn(false);
            given(userRepository.existsByNickname(command.nickname())).willReturn(false);
            given(passwordEncoder.encode(command.password())).willReturn("encodedPassword");

            User savedUser = User.createLocalUser(
                    "test@loopon.com",
                    "loopon",
                    "encodedPassword",
                    null
            );
            ReflectionTestUtils.setField(savedUser, "id", 1L);

            // when
            userCommandService.signUp(command);

            // then
            verify(userRepository).save(any(User.class));
            verify(userTermAgreementRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("실패: 필수 약관에 동의하지 않으면 예외가 발생한다")
        void 회원가입_실패_필수_약관_미동의() {
            // given
            Term mandatoryTerm = createTerm(1L, true);
            given(termRepository.findAllForSignUp()).willReturn(List.of(mandatoryTerm));

            UserSignUpCommand command = createCommand(
                    "test@loopon.com", "pw123", "pw123", "loopon", List.of()
            );

            // when & then
            assertThatThrownBy(() -> userCommandService.signUp(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MANDATORY_TERM_NOT_AGREED);

            verify(userRepository, never()).save(any());
            verify(userTermAgreementRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("실패: 비밀번호와 확인 비밀번호가 다르면 예외가 발생한다")
        void 회원가입_실패_비밀번호_비밀번호_확인_불일치() {
            // given
            UserSignUpCommand command = createCommand(
                    "test@loopon.com", "pw123", "pw999", "loopon", List.of(1L)
            );

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
            Term mandatoryTerm = createTerm(1L, true);
            given(termRepository.findAllForSignUp()).willReturn(List.of(mandatoryTerm));

            UserSignUpCommand command = createCommand(
                    "exist@loopon.com", "pw123", "pw123", "loopon", List.of(1L)
            );

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
            Term mandatoryTerm = createTerm(1L, true);
            given(termRepository.findAllForSignUp()).willReturn(List.of(mandatoryTerm));

            UserSignUpCommand command = createCommand(
                    "test@loopon.com", "pw123", "pw123", "existNick", List.of(1L)
            );

            given(userRepository.existsByEmail(command.email())).willReturn(false);
            given(userRepository.existsByNickname(command.nickname())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userCommandService.signUp(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("프로필 수정")
    class UpdateProfile {

        private UpdateProfileCommand createUpdateCommand(String nickname, String bio, String statusMessage, String profileImageUrl, UserVisibility visibility) {
            return new UpdateProfileCommand(
                    nickname, bio, statusMessage, profileImageUrl, visibility
            );
        }

        @Test
        @DisplayName("성공: 존재하는 사용자의 프로필 정보를 수정한다")
        void 프로필_수정_성공() {
            // given
            Long userId = 1L;
            User user = User.createLocalUser(
                    "test@loopon.com", "oldNick", "encodedPw", "oldImg.jpg"
            );
            ReflectionTestUtils.setField(user, "id", userId);

            UpdateProfileCommand command = createUpdateCommand(
                    "newNick", "newBio", "Studying...", "newImg.jpg", UserVisibility.PUBLIC
            );

            given(userRepository.findById(userId)).willReturn(java.util.Optional.of(user));

            // when
            UserProfileResponse response = userCommandService.updateProfile(userId, command);

            // then
            org.assertj.core.api.Assertions.assertThat(response.nickname()).isEqualTo(command.nickname());
            org.assertj.core.api.Assertions.assertThat(response.bio()).isEqualTo(command.bio());
            org.assertj.core.api.Assertions.assertThat(response.statusMessage()).isEqualTo(command.statusMessage());
            org.assertj.core.api.Assertions.assertThat(response.profileImageUrl()).isEqualTo(command.profileImageUrl());

            org.assertj.core.api.Assertions.assertThat(user.getNickname()).isEqualTo("newNick");
            org.assertj.core.api.Assertions.assertThat(user.getBio()).isEqualTo("newBio");
            org.assertj.core.api.Assertions.assertThat(user.getVisibility()).isEqualTo(UserVisibility.PUBLIC);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID로 요청하면 예외가 발생한다")
        void 프로필_수정_실패_사용자_없음() {
            // given
            Long userId = 999L;
            UpdateProfileCommand command = createUpdateCommand(
                    "newNick", "newBio", "msg", "img.jpg", UserVisibility.PRIVATE
            );

            given(userRepository.findById(userId)).willReturn(java.util.Optional.empty());

            // when & then
            assertThatThrownBy(() -> userCommandService.updateProfile(userId, command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }
}
