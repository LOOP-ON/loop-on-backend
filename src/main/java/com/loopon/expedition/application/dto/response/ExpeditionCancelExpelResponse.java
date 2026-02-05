package com.loopon.expedition.application.dto.response;

import lombok.Builder;

@Builder
public record ExpeditionCancelExpelResponse(
        Long userId
) {}
