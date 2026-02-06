package com.loopon.expedition.application.dto.command;

import lombok.Builder;

@Builder
public record ExpeditionExpelCommand(
        Long expeditionId,
        Long userId,
        Long myUserId
) {
}
