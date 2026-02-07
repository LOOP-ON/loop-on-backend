package com.loopon.journey.application.dto.request;

import com.loopon.journey.domain.JourneyCategory;
import jakarta.validation.constraints.NotBlank;

public class JourneyRequest {
    public record AddJourneyDto(
            String goal,
            JourneyCategory category
    ) {
    }

    public record PostponeRoutineDto(
            @NotBlank(message = "미루는 사유는 필수입니다.")
            String reason
    ) {
    }
}
