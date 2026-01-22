package com.loopon.user.application.dto.request;

import com.loopon.user.domain.FriendStatus;
import jakarta.validation.constraints.NotNull;

public record FriendRequestRespondRequest (
        @NotNull Long requesterId,
        @NotNull FriendStatus friendStatus
){}
