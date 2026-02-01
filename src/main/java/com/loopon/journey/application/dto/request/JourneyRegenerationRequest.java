package com.loopon.journey.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record JourneyRegenerationRequest(
        @NotBlank(message = "목표는 비어있을 수 없습니다.")
        @Size(max = 255, message = "목표는 255자 이하여야 합니다.")
        String goal,

        @NotEmpty(message = "제외할 여정 목록은 비어있을 수 없습니다.")
        List<String> excludeJourneyTitles
) {}