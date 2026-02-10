package com.loopon.routine.application.dto.response;

import com.loopon.journey.domain.ProgressStatus;
import lombok.Builder;

import java.time.LocalTime;
import java.util.List;

public class RoutineResponse {
    @Builder
    public record PostRoutinesDto(
            Long journeyId,
            List<RoutineSummaryDto> routines
    ) {}

    @Builder
    public record RoutineSummaryDto(
            Long routineId,
            String content,
            LocalTime notificationTime
    ){}

    @Builder
    public record RoutineCertifyDto(
            Long progressId,
            ProgressStatus status,
            String imageUrl
    ){}
}
