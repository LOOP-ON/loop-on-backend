package com.loopon.llm.infrastructure;

import com.loopon.llm.domain.LLMProvider;

public interface LlmClient {
    String generateLoops(String goal, LLMProvider provider);
}
