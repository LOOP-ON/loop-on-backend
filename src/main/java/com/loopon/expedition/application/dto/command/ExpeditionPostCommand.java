package com.loopon.expedition.application.dto.command;

import com.loopon.expedition.domain.ExpeditionCategory;
import com.loopon.expedition.domain.ExpeditionVisibility;
import lombok.Builder;

@Builder
public record ExpeditionPostCommand(
        String title,

        Integer capacity,

        ExpeditionVisibility visibility,

        ExpeditionCategory category,

        String password,

        Long userId
) {
}
