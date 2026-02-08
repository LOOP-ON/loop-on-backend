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
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoopGenerationService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final JourneyRepository journeyRepository;
    private final LLMApplicationServiceImpl llmService;

    @Transactional
    public LoopGenerationResponse generateLoops(LoopGenerationRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = saveGoal(request.getGoal(), user);

        String llmResponse = llmService.generateLoops(
                com.loopon.llm.domain.dto.LoopGenerationRequest.builder()
                        .goal(request.getGoal())
                        .loopCount(request.getLoopCount())
                        .build()
        );

        List<String> loopGoals = llmService.parseLoops(llmResponse);
        List<Journey> journeys = saveJourneys(user, JourneyCategory.ROUTINE, loopGoals);

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

    private List<Journey> saveJourneys(User user, JourneyCategory category, List<String> loops) {
        int base = journeyRepository.findMaxJourneyOrderByUserId(user.getId());
        int order = base + 1;

        List<Journey> saved = new ArrayList<>();
        for (String loopGoal : loops) {
            Journey journey = Journey.builder()
                    .user(user)
                    .category(category)     // ✅ 절대 null이면 안됨
                    .goal(loopGoal)
                    .journeyOrder(order++)
                    .build();
            saved.add(journeyRepository.save(journey));
        }
        return saved;
    }

}
