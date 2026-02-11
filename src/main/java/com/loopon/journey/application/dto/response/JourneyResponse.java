package com.loopon.journey.application.dto.response;

import com.loopon.journey.domain.JourneyCategory;
import com.loopon.journey.domain.ProgressStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class JourneyResponse {

    public record GoalRecommendationResponse(
            List<String> recommendations // AI가 추천한 5가지 행동 (예: "기상 후 물 마시기" 등)
    ) {}

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

}
