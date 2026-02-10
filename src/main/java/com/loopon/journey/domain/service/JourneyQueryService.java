package com.loopon.journey.domain.service;

import com.loopon.journey.application.dto.response.JourneyResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface JourneyQueryService {
    JourneyResponse.CurrentJourneyDto getCurrentJourney(Long userId);

    Slice<JourneyResponse.JourneyPreviewDto> getJourneyList(Long userId, Pageable pageable);

    Slice<JourneyResponse.JourneyPreviewDto> searchJourney(Long userId, String keyword, List<Boolean> categories, Pageable pageable);
}
