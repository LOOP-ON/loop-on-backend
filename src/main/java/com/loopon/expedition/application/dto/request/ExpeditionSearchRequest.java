package com.loopon.expedition.application.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ExpeditionSearchRequest(
        @NotNull
        String keyword,

        @Size(min = 3, max = 3)
        List<@NotNull Boolean> categories
) {
}
