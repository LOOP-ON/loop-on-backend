package com.loopon.user.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loopon.challenge.application.dto.response.ChallengeThumbnailResponse;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.user.domain.User;

public record UserOthersProfileResponse(
        Long userId,
        String nickname,
        String bio,
        String statusMessage,
        String profileImageUrl,
        Boolean isFriend,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        PageResponse<ChallengeThumbnailResponse> thumbnailResponse
) {
    public static UserOthersProfileResponse of(
            User user,
            Boolean isFriend,
            PageResponse<ChallengeThumbnailResponse> challenges
    ) {

        return new UserOthersProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getBio(),
                user.getStatusMessage(),
                user.getProfileImageUrl(),
                isFriend,
                challenges
        );
    }
}
