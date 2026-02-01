package com.loopon.journey.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JourneyGenerationRequest(
        @NotBlank(message = "목표는 비어있을 수 없습니다.")
        @Size(max = 255, message = "목표는 255자 이하여야 합니다.")
        String goal
) {}