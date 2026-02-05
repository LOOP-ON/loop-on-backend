package com.loopon.llm.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoopGenerationRequest {
    private final String goal;
    private final int loopCount;
}
