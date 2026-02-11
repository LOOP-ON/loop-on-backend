package com.loopon.llm.domain.service;

import com.loopon.llm.domain.dto.LoopGenerationRequest;

public interface LlmService {
    String generateLoops(LoopGenerationRequest request);
}
