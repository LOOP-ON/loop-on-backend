package com.loopon.journey.application.dto.request;

import com.loopon.journey.domain.JourneyCategory;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public class JourneyRequest {
    public record AddJourneyDto(
            String goal,
            JourneyCategory category
    ) {
    }

    public record PostponeRoutineDto(
            List<Long> progressIds,
            @NotBlank(message = "미루는 사유는 필수입니다.")
            String reason
    ) {
    }
}
