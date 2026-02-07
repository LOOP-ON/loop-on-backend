package com.loopon.routine.application.dto.converter;

import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.Routine;
import com.loopon.journey.domain.Journey;

import java.time.LocalTime;
import java.util.List;

public class RoutineConverter {

    public static Routine bodyToRoutine(
            Journey journey,
            RoutineRequest.RoutineDataDto dto
    ) {
        return Routine.builder()
                .journey(journey)
                .content(dto.content())
                .notificationTime(LocalTime.parse(dto.notificationTime()))
                .build();
    }

    public static RoutineResponse.PostRoutinesDto toPostRoutinesDto(
            List<Routine> routines
    ) {
        Long journeyId = routines.getFirst().getJourney().getId();
        return RoutineResponse.PostRoutinesDto.builder()
                .journeyId(journeyId)
                .routines(
                        routines.stream()
                                .map(RoutineConverter::toRoutineSummaryDto)
                                .toList()
                )
                .build();
    }

    private static RoutineResponse.RoutineSummaryDto toRoutineSummaryDto(
            Routine routine
    ) {
        return RoutineResponse.RoutineSummaryDto.builder()
                .routineId(routine.getId())
                .content(routine.getContent())
                .notificationTime(routine.getNotificationTime())
                .build();
    }
}
