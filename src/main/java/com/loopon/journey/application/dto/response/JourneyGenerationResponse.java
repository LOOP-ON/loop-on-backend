package com.loopon.journey.application.dto.response;

import java.util.List;

public record JourneyGenerationResponse(
        List<String> journeys
) {}