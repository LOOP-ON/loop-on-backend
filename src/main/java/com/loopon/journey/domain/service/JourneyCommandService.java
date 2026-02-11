package com.loopon.journey.domain.service;

import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.response.JourneyResponse;

public interface JourneyCommandService {

    JourneyResponse.PostponeRoutineDto postponeRoutine(JourneyCommand.PostponeRoutineCommand command);

    @Transactional
    JourneyResponse.JourneyRecordDto completeJourney(Long journeyId, Long userId);
}
