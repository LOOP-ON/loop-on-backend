package com.loopon.expedition.application.dto.request;

import com.loopon.expedition.domain.ExpeditionCategory;
import com.loopon.expedition.domain.ExpeditionVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExpeditionPostRequest(

        @NotBlank
        String title,

        @NotNull
        @Size(min = 1, max = 50)
        Integer capacity,

        @NotBlank
        ExpeditionVisibility visibility,

        @NotBlank
        ExpeditionCategory category,

        String password
) { }
