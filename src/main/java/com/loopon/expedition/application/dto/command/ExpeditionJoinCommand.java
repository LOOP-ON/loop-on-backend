package com.loopon.expedition.application.dto.command;

import com.loopon.expedition.domain.ExpeditionVisibility;
import lombok.Builder;

@Builder
public record ExpeditionJoinCommand(

        Long expeditionId,

        Long userId,

        ExpeditionVisibility expeditionVisibility,

        String password
) {}
