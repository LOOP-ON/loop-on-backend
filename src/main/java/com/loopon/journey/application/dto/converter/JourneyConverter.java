package com.loopon.journey.application.dto.converter;

import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyFeedback;
import com.loopon.routine.domain.Routine;
import com.loopon.user.domain.User;

import java.util.List;

public class JourneyConverter {
    public static Journey commandToJourney(JourneyCommand.AddJourneyGoalCommand command, User user, int journeyOrder) {
        return Journey.builder()
                .user(user)
                .goal(command.goal())
                .category(command.category())
                .journeyOrder(journeyOrder)
                .build();
    }
    public static JourneyResponse.JourneyRecordDto toCompleteJourneyDto(
            Journey journey,
            JourneyFeedback feedback,
            List<Routine> routines
    ) {

        List<JourneyResponse.RoutineSummaryDto> routineDtos =
                routines.stream()
                        .map(r -> new JourneyResponse.RoutineSummaryDto(
                                r.getId(),
                                r.getContent()
                        ))
                        .toList();

        return new JourneyResponse.JourneyRecordDto(
                journey.getId(),
                journey.getGoal(),
                routineDtos,
                feedback.getDay1Rate(),
                feedback.getDay2Rate(),
                feedback.getDay3Rate(),
                feedback.getTotalRate()
        );
    }
}
