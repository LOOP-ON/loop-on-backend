package com.loopon.llm.domain.service;

import com.loopon.llm.domain.dto.LoopGenerationRequest;

public interface LLMService {
    String generateLoops(LoopGenerationRequest request);
}