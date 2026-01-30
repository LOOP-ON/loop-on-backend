package com.loopon.auth.application.dto.response;

public record AuthResult(
        String accessToken,
        String refreshToken
) {

    public static AuthResult of(String accessToken, String refreshToken) {
        return new AuthResult(accessToken, refreshToken);
    }
}
