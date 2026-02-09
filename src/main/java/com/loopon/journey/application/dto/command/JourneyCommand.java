package com.loopon.journey.application.dto.command;

import com.loopon.journey.domain.JourneyCategory;

import java.time.LocalDate;
import java.util.List;

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
            List<Long> routineProgressIds,
            String reason
    ) {
    }
}
