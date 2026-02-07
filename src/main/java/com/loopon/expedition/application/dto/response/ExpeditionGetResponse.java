package com.loopon.expedition.application.dto.response;

import com.loopon.expedition.domain.ExpeditionVisibility;
import lombok.Builder;

@Builder
public record ExpeditionGetResponse(
        String title,
        Integer currentUsers,
        ExpeditionVisibility visibility,
        String password
) {
}
