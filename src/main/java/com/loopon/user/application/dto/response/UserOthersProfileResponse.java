package com.loopon.user.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loopon.challenge.application.dto.response.ChallengeThumbnailResponse;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.user.domain.UserJpaEntity;

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
            UserJpaEntity userJpaEntity,
            Boolean isFriend,
            PageResponse<ChallengeThumbnailResponse> challenges
    ) {

        return new UserOthersProfileResponse(
                userJpaEntity.getId(),
                userJpaEntity.getNickname(),
                userJpaEntity.getBio(),
                userJpaEntity.getStatusMessage(),
                userJpaEntity.getProfileImageUrl(),
                isFriend,
                challenges
        );
    }
}
