package com.loopon.expedition.application.dto.response;

import lombok.Builder;

@Builder
public record ExpeditionWithdrawResponse(
        Long expeditionId
) {}
