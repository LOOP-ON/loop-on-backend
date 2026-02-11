package com.loopon.expedition.application.dto.response;

import com.loopon.expedition.domain.ExpeditionCategory;
import com.loopon.expedition.domain.ExpeditionVisibility;
import lombok.Builder;

import java.util.List;

@Builder
public record ExpeditionGetResponseList(
        List<ExpeditionGetResponse> expeditionGetResponses
) {

    @Builder
    public record ExpeditionGetResponse(
            Long expeditionId,
            String title,
            ExpeditionCategory category,
            String admin,
            Integer currentUsers,
            Integer capacity,
            ExpeditionVisibility visibility,
            Boolean isAdmin
    ) {
    }
}
