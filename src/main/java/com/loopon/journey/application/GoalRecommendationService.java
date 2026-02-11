package com.loopon.journey.application;

import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.llm.application.service.LLMApplicationServiceImpl;
import com.loopon.llm.domain.dto.LoopGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalRecommendationService {

    private final LLMApplicationServiceImpl llmService;

    public JourneyResponse.GoalRecommendationResponse recommendActions(String category, String goal) {
        String prompt = createPrompt(category, goal);

        String aiResponse = llmService.generateLoops(
                LoopGenerationRequest.builder()
                        .goal(prompt)
                        .loopCount(5)
                        .build()
        );

        List<String> recommendations = llmService.parseLoops(aiResponse);

        return new JourneyResponse.GoalRecommendationResponse(recommendations);
    }

    private String createPrompt(String category, String goal) {
        return String.format("""
                당신은 목표 달성 코칭 전문가입니다.
                
                [사용자 입력]
                - 카테고리: %s
                - 구체적 목표: %s
                
                [요청 사항]
                위 목표를 달성하기 위해 사용자가 실천할 수 있는 구체적인 행동 루틴 5가지를 추천해주세요.
                
                [제약 조건]
                1. 추상적인 조언이 아닌, 당장 실행 가능한 행동이어야 합니다.
                2. 문장은 간결하게 작성하세요. (예: '아침 7시 기상' O, '일찍 일어나는 것이 좋다' X)
                3. 카테고리의 특성(%s)을 잘 반영해주세요.
                4. 정확히 5개의 항목만 나열해주세요.
                """, category, goal, category
        );
    }
}