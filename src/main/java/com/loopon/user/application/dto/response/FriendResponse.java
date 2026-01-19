package com.loopon.user.application.dto.response;

import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.User;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FriendResponse (
    @NotNull Long friend_id,
    @NotNull FriendStatus friendStatus,
    @NotNull String friend_image_url,//추가 건의
    @NotNull LocalDateTime created_at,
    @NotNull LocalDateTime updated_at
){
    public static FriendResponse from(Friend friend, Long me) {
        User opponent;

        if (friend.getRequester().getId().equals(me)) {
            opponent = friend.getReceiver();
        } else if (friend.getReceiver().getId().equals(me)) {
            opponent = friend.getRequester();
        } else {
            throw new IllegalStateException("Friend 관계에 me가 포함되어 있지 않습니다. me=" + me);
        }

        return new FriendResponse(
                friend.getId(),                 // friend_id = 관계 PK
                friend.getStatus(),             // friendStatus
                opponent.getProfileImageUrl(),  // friend_image_url = 상대방 이미지 (판단 필요)
                friend.getCreatedAt(),          // created_at
                friend.getUpdatedAt()           // updated_at
        );
    }
}
