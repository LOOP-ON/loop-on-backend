package com.loopon.user.application.dto.response;

import com.loopon.user.domain.Friend;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FriendRequestCreateResponse(
        @NotNull Long requesterId,
        @NotNull Long receiverId,
        @NotNull String requesterNickname,
        @NotNull String receiverNickname
) {
    public static FriendRequestCreateResponse from(Friend friend) {
        return FriendRequestCreateResponse.builder()
                .requesterId(friend.getRequester().getId())
                .receiverId(friend.getReceiver().getId())
                .requesterNickname(friend.getRequester().getNickname())
                .receiverNickname(friend.getReceiver().getNickname())
                .build();
    }
}