package com.loopon.expedition.application.dto.request;

import com.loopon.expedition.domain.ExpeditionVisibility;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record ExpeditionJoinRequest(

        @NotNull
        Long expeditionId,

        ExpeditionVisibility expeditionVisibility,

        String password
) {
    @AssertTrue(message = "비공개 방에 참여하려면 비밀번호를 입력해야 합니다.")
    public boolean passwordValid() {
        if (expeditionVisibility == ExpeditionVisibility.PRIVATE) {
            return password != null && !password.isBlank();
        }

        return true;
    }
}
