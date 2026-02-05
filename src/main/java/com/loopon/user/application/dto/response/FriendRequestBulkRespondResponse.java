package com.loopon.user.application.dto.response;

import jakarta.validation.constraints.NotNull;

public record FriendRequestBulkRespondResponse(
        @NotNull Long processCount
) {
}
