package com.loopon.journey.application.dto.command;

import com.loopon.journey.domain.JourneyCategory;

public class JourneyCommand {
    public record AddJourneyGoalCommand(
            Long userId,
            String goal,
            JourneyCategory category
    ) {}
}
