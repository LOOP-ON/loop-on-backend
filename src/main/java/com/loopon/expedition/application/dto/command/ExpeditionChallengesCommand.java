package com.loopon.expedition.application.dto.command;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public record ExpeditionChallengesCommand(
        Long expeditionId,
        Long userId,
        Pageable pageable
) {
}
