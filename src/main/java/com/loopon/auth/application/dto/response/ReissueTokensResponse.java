package com.loopon.auth.application.dto.response;

public record ReissueTokensResponse(
        String accessToken,
        String refreshToken
) {

    public static ReissueTokensResponse of(String accessToken, String refreshToken) {
        return new ReissueTokensResponse(accessToken, refreshToken);
    }
}
