package com.loopon.expedition.application.dto.request;

import com.loopon.expedition.domain.ExpeditionVisibility;

public record ExpeditionModifyRequest(
        String title,
        ExpeditionVisibility visibility,
        String password,
        Integer userLimit
) {
}
