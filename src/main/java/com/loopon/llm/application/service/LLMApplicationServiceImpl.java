package com.loopon.llm.application.service;

import com.loopon.llm.domain.LLMProvider;
import com.loopon.llm.domain.dto.LoopGenerationRequest;
import com.loopon.llm.domain.repository.LLMRepository;
import com.loopon.llm.domain.service.LLMService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LLMApplicationServiceImpl implements LLMService {
    
    private final LLMRepository llmRepository;
    
    public LLMApplicationServiceImpl(LLMRepository llmRepository) {
        this.llmRepository = llmRepository;
    }
    
    @Override
    public String generateLoops(LoopGenerationRequest request) {
        return llmRepository.generateLoops(request.getGoal(), LLMProvider.GEMINI);
    }
    
    public List<String> parseLoops(String response) {
        return response.lines()
                .filter(line -> line.matches("^\\d+\\.\\s+.*"))
                .map(line -> line.replaceFirst("^\\d+\\.\\s+", "").trim())
                .limit(5)
                .toList();
    }
}