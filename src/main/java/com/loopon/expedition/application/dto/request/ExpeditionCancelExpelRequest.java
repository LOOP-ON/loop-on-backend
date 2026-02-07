package com.loopon.expedition.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ExpeditionCancelExpelRequest(
        @NotNull Long userId
) {
}
