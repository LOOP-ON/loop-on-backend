package com.loopon.journey.domain.service;

import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.response.JourneyResponse;
import org.springframework.transaction.annotation.Transactional;

public interface JourneyCommandService {

    JourneyResponse.PostponeRoutineDto postponeRoutine(JourneyCommand.PostponeRoutineCommand command);

    JourneyResponse.JourneyRecordDto completeJourney(Long journeyId, Long userId);

    @Transactional
    void createJourneyFeedback(Long journeyId, Long userId);
}
