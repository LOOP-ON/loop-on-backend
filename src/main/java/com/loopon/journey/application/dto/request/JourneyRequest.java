package com.loopon.journey.application.dto.request;

import com.loopon.journey.domain.JourneyCategory;

public class JourneyRequest {
    public record AddJourneyDto(
            String goal,
            JourneyCategory category
    ){}
}
