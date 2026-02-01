package com.loopon.journey.application.service;

import com.loopon.journey.application.dto.response.JourneyGenerationResponse;
import com.loopon.journey.infrastructure.llm.GeminiClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JourneyGenerationService {

    private final GeminiClient geminiClient;

    public JourneyGenerationService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public JourneyGenerationResponse generateJourneys(String goal) {
        List<String> journeys = geminiClient.generateMultipleJourneys(goal);
        return new JourneyGenerationResponse(journeys);
    }

    public JourneyGenerationResponse regenerateJourneys(String goal, List<String> excludeJourneyTitles) {
        List<String> journeys = geminiClient.regenerateJourneys(goal, excludeJourneyTitles);
        return new JourneyGenerationResponse(journeys);
    }
}