package com.loopon.journey.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoopRegenerationResponse {
    private Long journeyId;
    private String newGoal;
}
