package com.loopon.user.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loopon.challenge.application.dto.response.ChallengeThumbnailResponse;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.user.infrastructure.persistence.jpa.UserJpaEntity;

public record UserProfileResponse(
        Long userId,
        String nickname,
        String email,
        String bio,
        String statusMessage,
        String profileImageUrl,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        PageResponse<ChallengeThumbnailResponse> thumbnailResponse
) {
    public static UserProfileResponse of(
            UserJpaEntity userJpaEntity,
            PageResponse<ChallengeThumbnailResponse> challenges
    ) {

        return new UserProfileResponse(
                userJpaEntity.getId(),
                userJpaEntity.getNickname(),
                userJpaEntity.getEmail(),
                userJpaEntity.getBio(),
                userJpaEntity.getStatusMessage(),
                userJpaEntity.getProfileImageUrl(),
                challenges
        );
    }
}
