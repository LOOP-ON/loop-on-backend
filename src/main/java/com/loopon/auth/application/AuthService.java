package com.loopon.auth.application;

import com.loopon.auth.application.dto.response.ReissueTokensResponse;
import com.loopon.auth.domain.RefreshToken;
import com.loopon.auth.infrastructure.RefreshTokenRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.AuthorizationException;
import com.loopon.global.security.jwt.JwtTokenProvider;
import com.loopon.global.security.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;

    public ReissueTokensResponse reissueTokens(String refreshToken) {
        jwtTokenValidator.validateToken(refreshToken);

        String email = jwtTokenValidator.getEmailFromRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthorizationException(ErrorCode.INVALID_REFRESH_TOKEN));

        RefreshToken savedRefreshToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AuthorizationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!savedRefreshToken.getToken().equals(refreshToken)) {
            log.warn("AuthService.reissueTokens - 리프레시 토큰 불일치(email: {})", email);

            refreshTokenRepository.delete(savedRefreshToken);
            throw new AuthorizationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = savedRefreshToken.getUserId();
        String role = savedRefreshToken.getRole();

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, email, Collections.singletonList(new SimpleGrantedAuthority(role)));
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        RefreshToken newRefreshTokenEntity = savedRefreshToken.rotate(newRefreshToken);
        refreshTokenRepository.save(newRefreshTokenEntity);

        return ReissueTokensResponse.of(newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken) {
        try {
            jwtTokenValidator.getEmailFromRefreshToken(refreshToken)
                    .ifPresent(email -> {
                        refreshTokenRepository.deleteById(email);
                        log.info("로그아웃 성공 - Refresh Token 삭제 완료 (email: {})", email);
                    });
        } catch (Exception e) {
            log.warn("로그아웃 처리 중 토큰 파싱 실패 (이미 만료됨): {}", e.getMessage());
        }
    }
}
