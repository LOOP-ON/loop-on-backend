package com.loopon.journey.application.service;

import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import com.loopon.llm.application.service.LlmApplicationServiceImpl;
import com.loopon.llm.domain.dto.LoopGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoopRegenerationService {

    private final LlmApplicationServiceImpl llmService;

    public LoopRegenerationResponse regenerateLoop(LoopRegenerationRequest request) {
        String prompt = createRegenerationPrompt(request.getMainGoal(), request.getOriginalGoal());
        String newGoalContent = generateSingleLoop(prompt);

        return LoopRegenerationResponse.builder()
                .newGoal(newGoalContent)
                .build();
    }

    private String createRegenerationPrompt(String mainGoal, String originalGoal) {
        return String.format("""
                전체 목표: %s
                안되는 여정: %s
                
                더 나은 여정 1개를 제안해주세요.
                
                조건:
                - 기존과 다른 접근
                - 한 문장으로 (15자 이내)
                - 구체적이고 실행 가능
                
                예시:
                전체 목표: 운동하기
                안되는 여정: 매일 1시간 헬스
                제안: 주 3회 20분 산책
                """, mainGoal, originalGoal);
    }

    private String generateSingleLoop(String prompt) {
        String response = llmService.generateLoops(
                LoopGenerationRequest.builder()
                        .goal(prompt)
                        .loopCount(1)
                        .build()
        );

        return llmService.parseLoops(response).get(0);
    }
}
