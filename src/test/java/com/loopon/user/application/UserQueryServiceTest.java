package com.loopon.user.application;

import com.loopon.challenge.domain.ChallengeImage;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import com.loopon.user.application.dto.response.UserProfileResponse;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @InjectMocks
    private UserQueryService userQueryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Nested
    @DisplayName("이메일 중복 확인")
    class 이메일_중복_확인 {

        @Test
        @DisplayName("성공: 이메일 중복 확인 - 사용 가능한 이메일")
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
        @DisplayName("실패: 이메일 중복 확인 - 이미 사용 중인 이메일")
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
        @DisplayName("성공: 닉네임 중복 확인 - 사용 가능한 닉네임")
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
        @DisplayName("실패: 닉네임 중복 확인 - 이미 사용 중인 닉네임")
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

    @Nested
    @DisplayName("내 프로필 조회")
    class 내_프로필_조회 {

        @Test
        @DisplayName("성공: 유저 정보와 챌린지 썸네일 페이징 정보를 반환한다")
        void 내_프로필_조회_성공() {
            // given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);

            User user = User.createLocalUser(
                    "test@loopon.com", "loopon", "encodedPw", "profile.jpg"
            );
            ReflectionTestUtils.setField(user, "id", userId);

            Page<ChallengeImage> emptyImagePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(challengeRepository.findThumbnailsByUserId(userId, pageable)).willReturn(emptyImagePage);

            // when
            UserProfileResponse response = userQueryService.getUserProfile(userId, pageable);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.nickname()).isEqualTo("loopon");
            assertThat(response.email()).isEqualTo("test@loopon.com");
            assertThat(response.thumbnailResponse()).isNotNull();
            assertThat(response.thumbnailResponse().content()).isEmpty();

            verify(userRepository).findById(userId);
            verify(challengeRepository).findThumbnailsByUserId(userId, pageable);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 유저 조회 시 예외 발생")
        void 내_프로필_조회_실패_유저_없음() {
            // given
            Long userId = 999L;
            Pageable pageable = PageRequest.of(0, 10);

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userQueryService.getUserProfile(userId, pageable))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }
}
