package com.loopon.journey.domain.service;

import com.loopon.journey.application.dto.response.JourneyResponse;

public interface JourneyQueryService {
    JourneyResponse.CurrentJourneyDto getCurrentJourney(Long userId);
}
