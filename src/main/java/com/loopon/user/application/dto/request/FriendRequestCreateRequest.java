package com.loopon.user.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record FriendRequestCreateRequest(
        @NotNull Long receiverId
        ){}
