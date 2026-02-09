package com.loopon.journey.application.dto.command;

import com.loopon.journey.domain.JourneyCategory;

import java.util.List;
import java.util.Optional;

public class JourneyCommand {
    public record AddJourneyGoalCommand(
            Long userId,
            String goal,
            JourneyCategory category
    ) {
    }

    public record PostponeRoutineCommand(
            Long userId,
            Long journeyId,
            Optional<List<Long>> routineIds,
            String reason
    ) {
    }
}
