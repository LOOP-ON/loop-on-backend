package com.loopon.global.security.handler;

import com.loopon.auth.application.dto.response.LoginSuccessResponse;
import com.loopon.global.security.jwt.JwtTokenProvider;
import com.loopon.global.security.principal.PrincipalDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        log.info("로그인 성공: email = {}", principal.getUsername());

        String accessToken = jwtProvider.createAccessToken(principal.getUserId(), principal.getUsername(), principal.getAuthorities());
        String refreshToken = jwtProvider.createRefreshToken(principal.getUsername());

        Cookie refreshCookie = createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshCookie);

        sendAccessTokenResponse(response, accessToken);
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);
        return cookie;
    }

    private void sendAccessTokenResponse(HttpServletResponse response, String accessToken) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        LoginSuccessResponse responseDto = LoginSuccessResponse.of(accessToken);
        objectMapper.writeValue(response.getWriter(), responseDto);
    }
}
