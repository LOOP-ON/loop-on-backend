package com.loopon.llm.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopon.llm.domain.LLMProvider;
import com.loopon.llm.domain.repository.LLMRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Repository
public class GeminiLLMRepository implements LLMRepository {
    
    private final RestClient restClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    
    public GeminiLLMRepository(@Value("${gemini.api.key}") String apiKey, RestClient geminiRestClient) {
        this.apiKey = apiKey;
        this.restClient = geminiRestClient;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String generateLoops(String goal, LLMProvider provider) {
        String prompt = createPrompt(goal);
        
        try {
            String response = restClient.post()
                .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                .body(Map.of(
                    "contents", Map.of(
                        "parts", Map.of(
                            "text", prompt
                        )
                    )
                ))
                .retrieve()
                .body(String.class);
                
            return parseResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate loops from Gemini API", e);
        }
    }
    
    private String parseResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            throw new RuntimeException("Invalid response format from Gemini API");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini API response", e);
        }
    }
    
    private String createPrompt(String goal) {
        return String.format("""
            당신은 목표 달성을 위한 구체적인 여정(루프)을 만드는 전문가입니다.
            사용자의 목표가 주어지면, 이를 달성하기 위한 5개의 구체적이고 실행 가능한 여정을 만들어주세요.
            
            목표: %s
            
            다음 형식으로 정확히 5개의 여정을 만들어주세요:
            1. [첫 번째 여정]
            2. [두 번째 여정]
            3. [세 번째 여정]
            4. [네 번째 여정]
            5. [다섯 번째 여정]
            
            각 여정은 다음 지침을 따라주세요:
            - 구체적이고 실행 가능해야 합니다
            - 단기적으로 달성 가능해야 합니다
            - 사용자의 목표와 직접적으로 관련이 있어야 합니다
            - 간결하고 명확하게 표현해주세요 (한 문장으로)
            """, goal);
    }
}
