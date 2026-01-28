package com.loopon.auth.application.dto.response;

public record SocialInfoResponse(
        String id,
        String email,
        String nickname,
        String profileImage
) {
}
