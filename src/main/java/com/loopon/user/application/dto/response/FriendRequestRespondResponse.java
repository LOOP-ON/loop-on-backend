package com.loopon.user.application.dto.response;

import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import jakarta.validation.constraints.NotNull;

public record FriendRequestRespondResponse(
    @NotNull Long requesterId,
    @NotNull Long receiverId,
    @NotNull String requesterName,
    @NotNull String receiverName,
    @NotNull FriendStatus friendStatus
){
    public static FriendRequestRespondResponse from(Friend friend) {

            return new FriendRequestRespondResponse(
                    friend.getRequester().getId(),
                    friend.getReceiver().getId(),
                    friend.getRequester().getName(),
                    friend.getReceiver().getName(),
                    friend.getStatus()
            );
    }
}
