package com.loopon.journey.domain.service;

import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.response.JourneyResponse;
import org.springframework.transaction.annotation.Transactional;

public interface JourneyCommandService {

    @Transactional
    JourneyResponse.PostJourneyGoalDto postJourneyGoal(
            JourneyCommand.AddJourneyGoalCommand command
    );

    @Transactional
    JourneyResponse.PostponeRoutineDto postponeRoutine(JourneyCommand.PostponeRoutineCommand command);
}
