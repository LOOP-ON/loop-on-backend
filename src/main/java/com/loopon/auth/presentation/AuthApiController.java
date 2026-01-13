package com.loopon.auth.presentation;

import com.loopon.auth.application.AuthService;
import com.loopon.auth.application.dto.request.LoginRequest;
import com.loopon.auth.application.dto.response.LoginSuccessResponse;
import com.loopon.auth.application.dto.response.ReissueTokensResponse;
import com.loopon.global.domain.dto.CommonResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(LoginRequest request) {
        throw new UnsupportedOperationException("이 메서드는 Spring Security의 JsonLoginProcessingFilter에서 처리됩니다.");
    }

    @PostMapping("/reissue")
    public CommonResponse<String> reissueTokens(
            @CookieValue(value = "refresh_token", required = true) String refreshToken,
            HttpServletResponse response
    ) {
        ReissueTokensResponse reissueTokensResponse = authService.reissueTokens(refreshToken);

        Cookie refreshCookie = createRefreshTokenCookie(reissueTokensResponse.refreshToken());
        response.addCookie(refreshCookie);

        return CommonResponse.onSuccess(reissueTokensResponse.accessToken());
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);
        return cookie;
    }
}
