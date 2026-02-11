package com.loopon.llm.application.service;

import com.loopon.llm.domain.LLMProvider;
import com.loopon.llm.domain.dto.LoopGenerationRequest;
import com.loopon.llm.infrastructure.LlmClient;
import com.loopon.llm.domain.service.LlmService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LlmApplicationServiceImpl implements LlmService {

    private final LlmClient llmRepository;

    public LlmApplicationServiceImpl(LlmClient llmRepository) {
        this.llmRepository = llmRepository;
    }

    @Override
    public String generateLoops(LoopGenerationRequest request) {
        return llmRepository.generateLoops(request.goal(), LLMProvider.GEMINI);
    }

    public List<String> parseLoops(String response) {
        return response.lines()
                .filter(line -> line.matches("^\\d+\\.\\s+.*"))
                .map(line -> line.replaceFirst("^\\d+\\.\\s+", "").trim())
                .limit(5)
                .toList();
    }
}
