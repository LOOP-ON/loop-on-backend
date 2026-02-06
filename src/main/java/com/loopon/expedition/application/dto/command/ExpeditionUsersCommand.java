package com.loopon.expedition.application.dto.command;

import lombok.Builder;

@Builder
public record ExpeditionUsersCommand(
        Long expeditionId,
        Long userId
) {
}
