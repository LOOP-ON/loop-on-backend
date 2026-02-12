package com.loopon.journey.application.dto.response;

import com.loopon.journey.domain.JourneyCategory;
import com.loopon.journey.domain.ProgressStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class JourneyResponse {

    public record GoalRecommendationResponse(
            List<String> recommendations
    ) {
    }

    public record JourneyOrderDto(
            Integer order
    ) {
    }

    public record PostponeRoutineDto(
            List<Long> routineIds,
            String reason
    ) {
    }

    public record CurrentJourneyDto(
            JourneyInfoDto journey,
            TodayProgressDto todayProgress,
            List<RoutineDto> routines,
            boolean isNotReady,
            LocalDate targetDate
    ) {
    }

    //여정 기본 정보
    public record JourneyInfoDto(
            Long journeyId,
            int journeyOrder,
            int journeyDate,
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
            Long routineProgressId,
            String content,
            LocalTime notificationTime,
            ProgressStatus status
    ) {
    }

    //여권 여정 조회
    public record JourneyPreviewDto(
            Long journeyId,
            String goal,
            JourneyCategory journeyCategory,
            Integer journeyOrder
    ) {
    }

    public record JourneyRecordDto(
            Long journeyId,
            String goal,

            List<RoutineSummaryDto> routines,

            Integer day1Rate,
            Integer day2Rate,
            Integer day3Rate,
            Integer totalRate
    ) {}

    public record RoutineSummaryDto(
            Long routineId,
            String routineName
    ) {}

    @Builder
    public record MonthlyCompletedDto(
            LocalDate date,
            Long completedCount
    ) {}

    public record DailyJourneyReportDto(
            Long journeyId,
            String goal,

            Integer day1Rate,
            Integer day2Rate,
            Integer day3Rate,
            Integer totalRate,

            Long completedRoutineCount,

            Optional<String> recordContent,

            List<DailyRoutineDto> routines
    )
    {}

    public record DailyRoutineDto(
            Long routineId,
            String content,
            String status
    ) {}
}
