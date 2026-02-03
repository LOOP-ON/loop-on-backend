package com.loopon.journey.application.dto.response;

import lombok.Builder;

public class JourneyResponse {

    @Builder
    public record PostJourneyGoalDto(
            Long journeyId
    ){}
}
