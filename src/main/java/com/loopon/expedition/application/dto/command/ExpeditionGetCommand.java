package com.loopon.expedition.application.dto.command;

import lombok.Builder;

@Builder
public record ExpeditionGetCommand(
        Long expeditionId,
        Long userId
) {
}
