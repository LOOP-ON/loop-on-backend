package com.loopon.auth.application;

import com.loopon.auth.application.dto.response.AuthResult;
import com.loopon.auth.application.dto.response.SocialInfoResponse;
import com.loopon.auth.application.strategy.SocialLoadStrategy;
import com.loopon.auth.domain.RefreshToken;
import com.loopon.auth.infrastructure.RefreshTokenRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.AuthorizationException;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.security.jwt.JwtTokenProvider;
import com.loopon.global.security.jwt.JwtTokenValidator;
import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import com.loopon.user.domain.UserRole;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SocialLoadStrategy socialLoadStrategy;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                refreshTokenRepository,
                userRepository,
                jwtTokenProvider,
                jwtTokenValidator,
                List.of(socialLoadStrategy)
        );
    }

    @Nested
    @DisplayName("소셜 로그인")
    class LoginSocial {

        @Mock
        private SocialLoadStrategy socialLoadStrategy;

        @BeforeEach
        void setUp() {
            authService = new AuthService(
                    refreshTokenRepository,
                    userRepository,
                    jwtTokenProvider,
                    jwtTokenValidator,
                    List.of(socialLoadStrategy)
            );
        }

        @Test
        @DisplayName("성공: 기존 회원이 로그인하면 토큰을 발급하고 저장한다")
        void 성공_기존_회원_로그인() {
            // given
            UserProvider provider = UserProvider.KAKAO;
            String socialToken = "token";
            String socialId = "12345";
            String email = "old@loopon.com";

            given(socialLoadStrategy.support(provider)).willReturn(true);
            given(socialLoadStrategy.loadSocialInfo(socialToken)).willReturn(
                    new SocialInfoResponse(socialId, "nick", email, "img")
            );

            User existingUser = User.createSocialUser(socialId, provider, email, "nick", "img");
            org.springframework.test.util.ReflectionTestUtils.setField(existingUser, "id", 1L);
            org.springframework.test.util.ReflectionTestUtils.setField(existingUser, "role", UserRole.ROLE_USER);

            given(userRepository.findBySocialIdAndProvider(socialId, provider))
                    .willReturn(Optional.of(existingUser));

            given(jwtTokenProvider.createAccessToken(anyLong(), anyString(), any())).willReturn("access");
            given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("refresh");

            // when
            AuthResult result = authService.loginSocial(provider, socialToken);

            // then
            assertThat(result.accessToken()).isEqualTo("access");
            verify(userRepository, times(0)).save(any(User.class));
        }

        @Test
        @DisplayName("성공: 신규 회원이면 회원가입을 진행 후 토큰을 발급한다")
        void 성공_신규_회원_가입() {
            // given
            UserProvider provider = UserProvider.KAKAO;
            String email = "new@loopon.com";
            SocialInfoResponse socialInfo = new SocialInfoResponse("999", "new", email, "img");

            given(socialLoadStrategy.support(provider)).willReturn(true);
            given(socialLoadStrategy.loadSocialInfo(anyString())).willReturn(socialInfo);

            given(userRepository.findBySocialIdAndProvider(anyString(), any())).willReturn(Optional.empty());
            given(userRepository.existsByNickname(anyString())).willReturn(false);

            given(userRepository.save(any(User.class))).willAnswer(invocation -> {
                User savingUser = invocation.getArgument(0);

                org.springframework.test.util.ReflectionTestUtils.setField(savingUser, "id", 2L);
                org.springframework.test.util.ReflectionTestUtils.setField(savingUser, "role", UserRole.ROLE_USER);

                return 2L;
            });

            given(jwtTokenProvider.createAccessToken(anyLong(), anyString(), any())).willReturn("access");
            given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("refresh");

            // when
            AuthResult result = authService.loginSocial(provider, "token");

            // then
            assertThat(result.accessToken()).isEqualTo("access");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("성공: 닉네임 중복 시 난수 접미사를 재생성하여 가입한다")
        void 성공_닉네임_중복_재시도() {
            // given
            given(socialLoadStrategy.support(any())).willReturn(true);
            given(socialLoadStrategy.loadSocialInfo(anyString())).willReturn(
                    new SocialInfoResponse("888", "dup", "dup@loopon.com", "img")
            );
            given(userRepository.findBySocialIdAndProvider(anyString(), any())).willReturn(Optional.empty());

            given(userRepository.existsByNickname(anyString()))
                    .willReturn(true)
                    .willReturn(false);

            given(userRepository.save(any(User.class))).willAnswer(invocation -> {
                User savingUser = invocation.getArgument(0);
                org.springframework.test.util.ReflectionTestUtils.setField(savingUser, "id", 3L);
                org.springframework.test.util.ReflectionTestUtils.setField(savingUser, "role", UserRole.ROLE_USER);
                return 3L; // Long 반환
            });

            given(jwtTokenProvider.createAccessToken(anyLong(), anyString(), any())).willReturn("access");
            given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("refresh");

            // when
            authService.loginSocial(UserProvider.KAKAO, "token");

            // then
            verify(userRepository, times(2)).existsByNickname(anyString());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 지원하지 않는 Provider 요청 시 예외가 발생한다")
        void 실패_지원하지_않는_Provider() {
            // given
            UserProvider unsupportedProvider = UserProvider.APPLE;
            given(socialLoadStrategy.support(unsupportedProvider)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.loginSocial(unsupportedProvider, "token"))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PROVIDER);
        }
    }

    @Nested
    @DisplayName("토큰 재발급")
    class ReissueTokens {

        private RefreshToken createRefreshTokenEntity(String email, String token) {
            return RefreshToken.builder()
                    .email(email)
                    .token(token)
                    .role("ROLE_USER")
                    .userId(1L)
                    .build();
        }
        
        @Test
        @DisplayName("성공: 유효한 리프레시 토큰이면 새로운 토큰 쌍을 발급하고 저장한다")
        void 토큰_재발급_성공() {
            // given
            String oldRefreshToken = "old_refresh_token";
            String email = "test@loopon.com";
            String newAccess = "new_access_token";
            String newRefresh = "new_refresh_token";

            RefreshToken savedTokenEntity = createRefreshTokenEntity(email, oldRefreshToken);

            given(jwtTokenValidator.getEmailFromRefreshToken(oldRefreshToken))
                    .willReturn(Optional.of(email));
            given(refreshTokenRepository.findById(email))
                    .willReturn(Optional.of(savedTokenEntity));
            given(jwtTokenProvider.createAccessToken(anyLong(), anyString(), any()))
                    .willReturn(newAccess);
            given(jwtTokenProvider.createRefreshToken(anyString()))
                    .willReturn(newRefresh);

            // when
            AuthResult response = authService.reissueTokens(oldRefreshToken);

            // then
            assertThat(response.accessToken()).isEqualTo(newAccess);
            assertThat(response.refreshToken()).isEqualTo(newRefresh);

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());

            RefreshToken savedEntity = captor.getValue();
            assertThat(savedEntity.getEmail()).isEqualTo(email);
            assertThat(savedEntity.getToken()).isEqualTo(newRefresh);
        }

        @Test
        @DisplayName("실패: 토큰에서 이메일을 추출할 수 없으면 예외가 발생한다")
        void 실패_이메일_추출_불가() {
            // given
            String invalidToken = "invalid_token";
            given(jwtTokenValidator.getEmailFromRefreshToken(invalidToken))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.reissueTokens(invalidToken))
                    .isInstanceOf(AuthorizationException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("실패: 저장소에 해당 이메일의 토큰이 없으면(만료됨) 예외가 발생한다")
        void 실패_저장된_토큰_없음() {
            // given
            String refreshToken = "valid_format_token";
            String email = "test@loopon.com";

            given(jwtTokenValidator.getEmailFromRefreshToken(refreshToken))
                    .willReturn(Optional.of(email));

            given(refreshTokenRepository.findById(email))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.reissueTokens(refreshToken))
                    .isInstanceOf(AuthorizationException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 요청 토큰과 저장된 토큰이 다르면(탈취 시도) 토큰을 삭제하고 예외를 던진다")
        void 실패_토큰_불일치_RTR_발동() {
            // given
            String requestToken = "stolen_token";
            String storedToken = "latest_token";
            String email = "test@loopon.com";

            RefreshToken savedTokenEntity = createRefreshTokenEntity(email, storedToken);

            given(jwtTokenValidator.getEmailFromRefreshToken(requestToken))
                    .willReturn(Optional.of(email));

            given(refreshTokenRepository.findById(email))
                    .willReturn(Optional.of(savedTokenEntity));

            // when & then
            assertThatThrownBy(() -> authService.reissueTokens(requestToken))
                    .isInstanceOf(AuthorizationException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);

            verify(refreshTokenRepository).delete(savedTokenEntity);
        }
    }
}
