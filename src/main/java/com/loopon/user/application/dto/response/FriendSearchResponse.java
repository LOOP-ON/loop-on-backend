package com.loopon.user.application.dto.response;

import com.loopon.user.domain.User;

public record FriendSearchResponse(
        String nickname,
        String bio,
        String profile_image_url,
        Long user_id
) {
    public static FriendSearchResponse from(User user) {
        return new FriendSearchResponse(
                user.getNickname(),
                user.getBio(),
                user.getProfileImageUrl(),
                user.getId()
        );
    }
}
