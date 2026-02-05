package com.loopon.journey.application.dto.response;

import lombok.Builder;

import java.time.LocalDate;

public class JourneyResponse {

    @Builder
    public record PostJourneyGoalDto(
            Long journeyId
    ){}

    public record PostponeRoutineDto(
            Long routineId,
            String reason
    ) {}
}
