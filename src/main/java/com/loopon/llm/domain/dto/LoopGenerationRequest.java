package com.loopon.llm.domain.dto;

import lombok.Builder;

@Builder
public record LoopGenerationRequest(
        String goal,
        int loopCount
) {
}
