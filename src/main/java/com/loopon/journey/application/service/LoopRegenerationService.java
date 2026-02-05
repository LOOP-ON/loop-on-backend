package com.loopon.journey.application.service;

import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.repository.JourneyRepository;
import com.loopon.llm.application.service.LLMApplicationServiceImpl;
import com.loopon.llm.domain.dto.LoopGenerationRequest;
import com.loopon.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoopRegenerationService {

    private final JourneyRepository journeyRepository;
    private final LLMApplicationServiceImpl llmService;

    @Transactional
    public LoopRegenerationResponse regenerateLoop(Long journeyId, LoopRegenerationRequest request, User user) {
        Journey existingJourney = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new RuntimeException("Journey not found"));

        if (!existingJourney.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to journey");
        }

        String prompt = createRegenerationPrompt(request.getMainGoal(), request.getOriginalGoal());
        String newGoalContent = generateSingleLoop(prompt);

        existingJourney.updateGoal(newGoalContent);
        Journey updatedJourney = journeyRepository.save(existingJourney);

        return LoopRegenerationResponse.builder()
                .journeyId(updatedJourney.getId())
                .newGoal(updatedJourney.getGoal())
                .build();
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
