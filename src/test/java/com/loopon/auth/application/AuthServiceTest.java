package com.loopon.auth.application;

import com.loopon.auth.application.dto.response.ReissueTokensResponse;
import com.loopon.auth.domain.RefreshToken;
import com.loopon.auth.infrastructure.RefreshTokenRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.AuthorizationException;
import com.loopon.global.security.jwt.JwtTokenProvider;
import com.loopon.global.security.jwt.JwtTokenValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
            ReissueTokensResponse response = authService.reissueTokens(oldRefreshToken);

            // then
            assertThat(response.accessToken()).isEqualTo(newAccess);
            assertThat(response.refreshToken()).isEqualTo(newRefresh);

            assertThat(savedTokenEntity.getToken()).isEqualTo(newRefresh);
            verify(refreshTokenRepository).save(savedTokenEntity);
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
