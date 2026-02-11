package com.loopon.journey.application;

import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import com.loopon.llm.application.service.LLMApplicationServiceImpl;
import com.loopon.llm.domain.dto.LoopGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyAiService {

    private final LLMApplicationServiceImpl llmService;

    /**
     * [온보딩] 목표에 맞는 루프 5가지 추천
     */
    public JourneyResponse.GoalRecommendationResponse recommendActions(String category, String goal) {
        String prompt = createRecommendationPrompt(category, goal);

        String aiResponse = llmService.generateLoops(
                LoopGenerationRequest.builder()
                        .goal(prompt)
                        .loopCount(5)
                        .build()
        );

        List<String> recommendations = llmService.parseLoops(aiResponse);

        return new JourneyResponse.GoalRecommendationResponse(recommendations);
    }

    /**
     * [수정] 마음에 안 드는 루프 1개 재생성
     */
    public LoopRegenerationResponse regenerateLoop(LoopRegenerationRequest request) {
        String prompt = createRegenerationPrompt(request.getMainGoal(), request.getOriginalGoal());

        String aiResponse = llmService.generateLoops(
                LoopGenerationRequest.builder()
                        .goal(prompt)
                        .loopCount(1)
                        .build()
        );

        String newGoalContent = llmService.parseLoops(aiResponse).get(0);

        return LoopRegenerationResponse.builder()
                .newGoal(newGoalContent)
                .build();
    }

    private String createRecommendationPrompt(String category, String goal) {
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

    private String createRegenerationPrompt(String mainGoal, String originalGoal) {
        return String.format("""
                당신은 목표 달성을 위한 여정(루프)을 개선하는 전문가입니다.
                
                사용자의 전체 목표: %s
                마음에 들지 않는 기존 여정: %s
                
                위 전체 목표를 달성하기 위한 더 나은 여정을 1개만 제안해주세요.
                다음 지침을 따라주세요:
                - 기존 여정과는 다른 새로운 접근 방식을 제안해주세요
                - 구체적이고 실행 가능해야 합니다
                - 사용자의 목표와 직접적으로 관련이 있어야 합니다
                - 간결하고 명확하게 표현해주세요 (한 문장으로)
                """, mainGoal, originalGoal);
    }
}
