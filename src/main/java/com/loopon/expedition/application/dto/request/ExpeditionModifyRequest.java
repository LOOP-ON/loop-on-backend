package com.loopon.expedition.application.dto.request;

import com.loopon.expedition.domain.ExpeditionVisibility;

public record ExpeditionModifyRequest(
        Long expeditionId,
        String title,
        ExpeditionVisibility visibility,
        String password
) {
}
