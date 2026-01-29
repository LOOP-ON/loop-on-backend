package com.loopon.auth.presentation;

import com.loopon.auth.application.AuthService;
import com.loopon.auth.application.dto.request.KakaoLoginRequest;
import com.loopon.auth.application.dto.request.LoginRequest;
import com.loopon.auth.application.dto.response.AccessTokenResponse;
import com.loopon.auth.application.dto.response.AuthResult;
import com.loopon.auth.application.dto.response.LoginSuccessResponse;
import com.loopon.auth.presentation.docs.AuthApiDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.jwt.TokenCookieFactory;
import com.loopon.user.domain.UserProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController implements AuthApiDocs {
    private final AuthService authService;

    private final TokenCookieFactory tokenCookieFactory;

    @Override
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginSuccessResponse>> login(@RequestBody @Valid LoginRequest request) {
        AuthResult authResult = authService.login(request);

        ResponseCookie cookie = tokenCookieFactory.createRefreshTokenCookie(authResult.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(CommonResponse.onSuccess(LoginSuccessResponse.of(authResult.accessToken())));
    }

    @Override
    @PostMapping("/login/kakao")
    public ResponseEntity<CommonResponse<LoginSuccessResponse>> loginKakao(
            @Valid @RequestBody KakaoLoginRequest request
    ) {
        AuthResult authResult = authService.loginSocial(UserProvider.KAKAO, request.accessToken());

        ResponseCookie cookie = tokenCookieFactory.createRefreshTokenCookie(authResult.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(CommonResponse.onSuccess(LoginSuccessResponse.of(authResult.accessToken())));
    }

    @Override
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<AccessTokenResponse>> reissueTokens(
            @CookieValue(value = "refresh_token", required = true) String refreshToken,
            HttpServletResponse response
    ) {
        AuthResult authResult = authService.reissueTokens(refreshToken);

        ResponseCookie refreshTokenCookie = tokenCookieFactory.createRefreshTokenCookie(authResult.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(CommonResponse.onSuccess(AccessTokenResponse.of(authResult.accessToken())));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie logoutCookie = tokenCookieFactory.createLogoutCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
                .body(CommonResponse.onSuccess());
    }
}
