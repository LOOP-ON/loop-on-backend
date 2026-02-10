package com.loopon.llm.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopon.journey.infrastructure.llm.GeminiProperties;
import com.loopon.llm.domain.LLMProvider;
import com.loopon.llm.domain.repository.LLMRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Repository
public class GeminiLLMRepository implements LLMRepository {

    private final RestClient restClient;
    private final GeminiProperties props;
    private final ObjectMapper objectMapper;

    public GeminiLLMRepository(GeminiProperties props, RestClient geminiRestClient) {
        this.props = props;
        this.restClient = geminiRestClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String generateLoops(String goal, LLMProvider provider) {
        String prompt = createPrompt(goal);

        try {
            String response = restClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=" + props.apiKey())
                    .body(Map.of(
                            "contents", new Object[]{
                                    Map.of(
                                            "parts", new Object[]{
                                                    Map.of("text", prompt)
                                            }
                                    )
                            }
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
                사용자 목표: %s
                
                위 목표를 위한 여정 5개를 만들어주세요.
                
                조건:
                - 한 문장으로 (15자 이내)
                - 구체적이고 실행 가능
                - 간결하고 명확
                
                형식:
                1. [여정]
                2. [여정]
                3. [여정]
                4. [여정]
                5. [여정]
                
                예시:
                목표: 운동하기
                1. 매일 10분 스트레칭
                2. 주 3회 걷기
                3. 계단 오르기
                4. 요가 15분
                5. 수영 30분
                """, goal);
    }
}
