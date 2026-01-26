package com.loopon.expedition.application.dto.command;

import lombok.Builder;

@Builder
public record ExpeditionDeleteCommand(
        Long expeditionId,
        Long userId
) {}
