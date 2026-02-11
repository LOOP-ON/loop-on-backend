package com.loopon.journey.application.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class JourneyRequest {
    public record GoalRequest(
            @NotBlank(message = "카테고리는 필수입니다.")
            String category,

            @NotBlank(message = "목표는 필수입니다.")
            String goal
    ) {
    }

    public record PostponeRoutineDto(
            List<Long> progressIds,

            @NotBlank(message = "미루는 사유는 필수입니다.")
            String reason
    ) {
    }
}
