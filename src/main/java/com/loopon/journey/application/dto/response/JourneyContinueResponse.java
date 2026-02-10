package com.loopon.journey.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JourneyContinueResponse {
    private String goal;
    private boolean isContinuation;
    private Long originalJourneyId;
}