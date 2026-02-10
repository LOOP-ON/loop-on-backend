package com.loopon.journey.application.dto.converter;

import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.domain.Journey;
import com.loopon.user.domain.User;

public class JourneyConverter {
    public static Journey commandToJourney(JourneyCommand.AddJourneyGoalCommand command, User user, int journeyOrder) {
        return Journey.builder()
                .user(user)
                .goal(command.goal())
                .category(command.category())
                .journeyOrder(journeyOrder)
                .build();
    }
}
