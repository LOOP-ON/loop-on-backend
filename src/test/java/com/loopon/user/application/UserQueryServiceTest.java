package com.loopon.user.application;

import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @Nested
    @DisplayName("이메일 중복 확인")
    class 이메일_중복_확인 {

        @Test
        @DisplayName("이메일 중복 확인 - 사용 가능한 이메일")
        void 이메일_중복_확인_사용_가능한_이메일() {
            // given
            String email = "test@gmail.com";
            given(userRepository.existsByEmail(email)).willReturn(false);

            // when
            UserDuplicateCheckResponse response = userQueryService.isEmailAvailable(email);

            // then
            assertTrue(response.isAvailable());
            verify(userRepository).existsByEmail(email);
        }

        @Test
        @DisplayName("이메일 중복 확인 - 이미 사용 중인 이메일")
        void 이메일_중복_확인_이미_사용_중인_이메일() {
            // given
            String email = "test@gmail.com";
            given(userRepository.existsByEmail(email)).willReturn(true);

            // when
            UserDuplicateCheckResponse response = userQueryService.isEmailAvailable(email);

            // then
            assertFalse(response.isAvailable());
            verify(userRepository).existsByEmail(email);
        }
    }

    @Nested
    @DisplayName("닉네임 중복 확인")
    class 닉네임_중복_확인 {

        @Test
        @DisplayName("닉네임 중복 확인 - 사용 가능한 닉네임")
        void 닉네임_중복_확인_사용_가능한_닉네임() {
            // given
            String nickname = "loopon";
            given(userRepository.existsByNickname(nickname)).willReturn(false);

            // when
            UserDuplicateCheckResponse response = userQueryService.isNicknameAvailable(nickname);

            // then
            assertTrue(response.isAvailable());
            verify(userRepository).existsByNickname(nickname);
        }

        @Test
        @DisplayName("닉네임 중복 확인 - 이미 사용 중인 닉네임")
        void 닉네임_중복_확인_이미_사용_중인_닉네임() {
            // given
            String nickname = "loopon";
            given(userRepository.existsByNickname(nickname)).willReturn(true);

            // when
            UserDuplicateCheckResponse response = userQueryService.isNicknameAvailable(nickname);

            // then
            assertFalse(response.isAvailable());
            verify(userRepository).existsByNickname(nickname);
        }
    }
}
