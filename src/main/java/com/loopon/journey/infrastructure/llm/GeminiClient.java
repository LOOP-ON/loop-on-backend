package com.loopon.journey.infrastructure.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GeminiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public GeminiClient(String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<String> generateMultipleJourneys(String goal) {
        String prompt = String.format("""
            사용자의 목표가 "%s"일 때, 이 목표를 달성하기 위한 5개의 구체적인 여정(journey)을 생성해주세요.
            
            각 여정은 다음 형식을 따라야 합니다:
            1. [여정 제목]: [여정 설명]
            
            여정은 현실적이고 구체적이어야 하며, 목표 달성에 실제로 도움이 되어야 합니다.
            한국어로 응답해주세요.
            """, goal);

        String response = generateText(prompt);
        return parseJourneyResponse(response);
    }

    public List<String> regenerateJourneys(String goal, List<String> existingJourneyTitles) {
        String excludeList = existingJourneyTitles.stream()
                .map(title -> "- " + title)
                .collect(Collectors.joining("\n"));

        String prompt = String.format("""
            사용자의 목표가 "%s"일 때, 다음 기존 여정들을 제외하고 5개의 새로운 여정(journey)을 생성해주세요:
            
            %s
            
            각 여정은 다음 형식을 따라야 합니다:
            1. [여정 제목]: [여정 설명]
            
            여정은 현실적이고 구체적이어야 하며, 기존 여정과는 다른 새로운 접근 방식을 제안해야 합니다.
            한국어로 응답해주세요.
            """, goal, excludeList);

        String response = generateText(prompt);
        return parseJourneyResponse(response);
    }

    private String generateText(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            String response = webClient.post()
                    .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate text from Gemini API", e);
        }
    }

    private List<String> parseJourneyResponse(String response) {
        return response.lines()
                .filter(line -> line.matches("^\\d+\\..*"))
                .collect(Collectors.toList());
    }
}