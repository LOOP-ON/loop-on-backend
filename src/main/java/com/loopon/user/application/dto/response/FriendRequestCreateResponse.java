package com.loopon.user.application.dto.response;

import com.loopon.user.domain.Friend;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FriendRequestCreateResponse (
    @NotNull Long requesterId,
    @NotNull Long receiverId,
    @NotNull String requesterName,
    @NotNull String receiverName
    ){
    public static FriendRequestCreateResponse from(Friend friend) {
        return FriendRequestCreateResponse.builder()
                .requesterId(friend.getRequester().getId())
                .receiverId(friend.getReceiver().getId())
                .requesterName(friend.getRequester().getName())
                .receiverName(friend.getReceiver().getName())
                .build();
    }
}