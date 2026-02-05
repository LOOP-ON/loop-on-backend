package com.loopon.goal.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoopGenerationResponse {
    private final Long goalId;
    private final String goal;
    private final java.util.List<LoopResponse> loops;

    @Getter
    @Builder
    public static class LoopResponse {
        private final Long journeyId;
        private final String goal;
    }
}
