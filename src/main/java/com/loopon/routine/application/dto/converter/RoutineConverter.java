package com.loopon.routine.application.dto.converter;

import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;

public class RoutineConverter {

    private static RoutineResponse.RoutineSummaryDto toRoutineSummaryDto(
            Routine routine
    ) {
        return RoutineResponse.RoutineSummaryDto.builder()
                .routineId(routine.getId())
                .content(routine.getContent())
                .notificationTime(routine.getNotificationTime())
                .build();
    }

    public static RoutineResponse.RoutineCertifyDto toRoutineCertifyDto(
            RoutineProgress progress
    ) {
        return RoutineResponse.RoutineCertifyDto.builder()
                .progressId(progress.getId())
                .status(progress.getStatus())
                .imageUrl(progress.getImageUrl())
                .build();
    }
}
