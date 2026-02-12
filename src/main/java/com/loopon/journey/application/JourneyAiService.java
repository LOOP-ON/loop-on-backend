package com.loopon.journey.application;

import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import com.loopon.llm.application.service.LlmApplicationServiceImpl;
import com.loopon.llm.domain.dto.LoopGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyAiService {

    private final LlmApplicationServiceImpl llmService;

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
                역할: 당신은 간결하고 실용적인 목표 달성 코치입니다.
                
                [입력 정보]
                - 카테고리: %s
                - 사용자 목표: %s
                
                [요청 사항]
                위 목표를 달성하기 위한 구체적인 실천 행동(루프) 5가지를 추천해주세요.
                
                [필수 제약 조건]
                1. **길이 제한**: 공백 포함 **15자 이내**로 작성하세요. (UI 표시를 위해 필수)
                2. **형식**: 번호나 기호 없이, 한 줄에 하나씩 작성하세요.
                3. **어조**: '~하기', '~가기' 등 **명사형**으로 간결하게 끝내세요.
                4. 내용은 구체적이고 당장 실행 가능해야 합니다.
                
                [작성 예시]
                매일 10분 스트레칭하기
                주 3회 가볍게 걷기
                점심시간 계단 오르기
                잠들기 전 독서 10분
                하루 물 1리터 마시기
                """, category, goal
        );
    }

    private String createRegenerationPrompt(String mainGoal, String originalGoal) {
        return String.format("""
                역할: 당신은 여정(루프) 개선 전문가입니다.
                
                [입력 정보]
                - 전체 목표: %s
                - 기존 여정(삭제 예정): %s
                
                [요청 사항]
                기존 여정을 대체할 **새로운 행동 1가지**를 제안해주세요.
                
                [필수 제약 조건]
                1. **길이 제한**: 공백 포함 **15자 이내**로 작성하세요.
                2. **형식**: 번호나 기호 없이 내용만 한 줄로 출력하세요.
                3. **어조**: '~하기' 등 **명사형**으로 간결하게 끝내세요.
                4. 기존 여정과 다른 방식의 접근이어야 합니다.
                """, mainGoal, originalGoal);
    }
}
