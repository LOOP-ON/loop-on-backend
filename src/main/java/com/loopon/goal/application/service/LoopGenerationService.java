package com.loopon.goal.application.service;

import com.loopon.goal.application.dto.request.LoopGenerationRequest;
import com.loopon.goal.application.dto.response.LoopGenerationResponse;
import com.loopon.journey.domain.Goal;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyCategory;
import com.loopon.journey.domain.repository.GoalRepository;
import com.loopon.journey.domain.repository.JourneyRepository;
import com.loopon.llm.application.service.LLMApplicationServiceImpl;
import com.loopon.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoopGenerationService {

    private final GoalRepository goalRepository;
    private final JourneyRepository journeyRepository;
    private final LLMApplicationServiceImpl llmService;

    @Transactional
    public LoopGenerationResponse generateLoops(LoopGenerationRequest request, User user) {
        Goal goal = saveGoal(request.getGoal(), user);

        String llmResponse = llmService.generateLoops(
                com.loopon.llm.domain.dto.LoopGenerationRequest.builder()
                        .goal(request.getGoal())
                        .loopCount(request.getLoopCount())
                        .build()
        );

        List<String> loopGoals = llmService.parseLoops(llmResponse);
        List<Journey> journeys = saveJourneys(loopGoals, user, goal);

        return LoopGenerationResponse.builder()
                .goalId(goal.getId())
                .goal(goal.getContent())
                .loops(journeys.stream()
                        .map(journey -> LoopGenerationResponse.LoopResponse.builder()
                                .journeyId(journey.getId())
                                .goal(journey.getGoal())
                                .build())
                        .toList())
                .build();
    }

    private Goal saveGoal(String goalContent, User user) {
        Goal goal = Goal.builder()
                .content(goalContent)
                .build();
        return goalRepository.save(goal);
    }

    private List<Journey> saveJourneys(List<String> loopGoals, User user, Goal goal) {
        return loopGoals.stream()
                .map(loopGoal -> Journey.builder()
                        .user(user)
                        .category(JourneyCategory.ROUTINE)
                        .goal(loopGoal)
                        .build())
                .map(journeyRepository::save)
                .toList();
    }
}
