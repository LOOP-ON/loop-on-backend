package com.loopon.journey.application.dto.response;

import com.loopon.journey.domain.JourneyCategory;
import com.loopon.journey.domain.JourneyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JourneyContinuationResponse {
    private Long goalId;
    private String mainGoal;
    private List<JourneyStamp> journeyStamps;
    private int completedCount;
    private int totalCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JourneyStamp {
        private Long journeyId;
        private String goal;
        private JourneyCategory category;
        private LocalDate startDate;
        private LocalDate endDate;
        private JourneyStatus status;
        private boolean isCompleted;
        private int order;
    }
}