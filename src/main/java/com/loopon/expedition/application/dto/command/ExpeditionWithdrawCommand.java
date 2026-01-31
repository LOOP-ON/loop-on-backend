package com.loopon.expedition.application.dto.command;

import lombok.Builder;

@Builder
public record ExpeditionWithdrawCommand(
        Long expeditionId,
        Long userId
) {}
