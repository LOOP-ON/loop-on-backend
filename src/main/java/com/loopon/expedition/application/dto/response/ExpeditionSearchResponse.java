package com.loopon.expedition.application.dto.response;

import com.loopon.expedition.domain.ExpeditionCategory;
import com.loopon.expedition.domain.ExpeditionVisibility;
import lombok.Builder;

@Builder
public record ExpeditionSearchResponse(
        Long expeditionId,
        String title,
        ExpeditionCategory category,
        String admin,
        Integer currentUsers,
        Integer capacity,
        ExpeditionVisibility visibility,
        Boolean isJoined,
        Boolean isAdmin
) {
}
