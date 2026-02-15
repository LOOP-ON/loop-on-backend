package com.loopon.journey.domain.service;

import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.response.JourneyResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface JourneyCommandService {

    JourneyResponse.PostponeRoutineDto postponeRoutine(JourneyCommand.PostponeRoutineCommand command);


    @Transactional
    void UpdateJourneyFeedback(Long journeyId, Long userId, LocalDate targetDate);
}
