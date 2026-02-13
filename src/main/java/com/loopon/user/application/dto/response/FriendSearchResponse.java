package com.loopon.user.application.dto.response;

import com.loopon.user.domain.FriendStatus;

public record FriendSearchResponse(
        String nickname,
        String bio,
        FriendStatus friendStatus,
        String profile_image_url,
        Long user_id
) {
}
