package com.loopon.user.application.dto.response;

import com.loopon.user.domain.Friend;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

//내가 받은 친구 요청 목록 조회용
public record FriendRequestReceivedResponse(
        @NotNull Long requester_id,
        @NotNull String friend_image_url,
        @NotNull String friend_nickname,
        @NotNull LocalDateTime created_at,
        @NotNull LocalDateTime updated_at

) {
    public static FriendRequestReceivedResponse from(Friend friend) {
        return new FriendRequestReceivedResponse(
                friend.getRequester().getId(),
                friend.getRequester().getProfileImageUrl(),   // 친구 프로필 이미지
                friend.getRequester().getNickname(),           // 친구 닉네임
                friend.getCreatedAt(),                         // 요청 생성 시각
                friend.getUpdatedAt()                          // 최근 수정 시각
        );
    }
}
