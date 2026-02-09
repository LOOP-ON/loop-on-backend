package com.loopon.expedition.application.dto.command;

import com.loopon.expedition.domain.ExpeditionVisibility;
import lombok.Builder;

@Builder
public record ExpeditionModifyCommand(
        Long expeditionId,
        String title,
        ExpeditionVisibility visibility,
        String password,
        Integer userLimit,
        Long userId
) {
}
