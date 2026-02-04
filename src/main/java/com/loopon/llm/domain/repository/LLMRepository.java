package com.loopon.llm.domain.repository;

import com.loopon.llm.domain.LLMProvider;

public interface LLMRepository {
    String generateLoops(String goal, LLMProvider provider);
}