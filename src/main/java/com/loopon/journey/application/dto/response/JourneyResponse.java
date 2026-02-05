package com.loopon.journey.application.dto.response;

import com.loopon.journey.domain.JourneyCategory;
import com.loopon.journey.domain.ProgressStatus;
import lombok.Builder;

import java.time.LocalTime;
import java.util.List;

public class JourneyResponse {

    @Builder
    public record PostJourneyGoalDto(
            Long journeyId
    ) {
    }

    public record PostponeRoutineDto(
            Long routineId,
            String reason
    ) {
    }

    public record CurrentJourneyDto(
            JourneyInfoDto journey,
            TodayProgressDto todayProgress,
            List<RoutineDto> routines
    ) {
    }

    //여정 기본 정보
    public record JourneyInfoDto(
            Long journeyId,
            JourneyCategory journeyCategory,
            String goal
    ) {
    }

    //여정 진행률
    public record TodayProgressDto(
            int completedCount,
            int totalCount
    ) {
    }

    //루틴 정보
    public record RoutineDto(
            Long routineId,
            String content,
            LocalTime notificationTime,
            ProgressStatus status
    ) {
    }
}
