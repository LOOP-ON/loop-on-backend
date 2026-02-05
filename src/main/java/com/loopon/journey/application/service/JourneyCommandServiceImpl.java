package com.loopon.journey.application.service;

import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.converter.JourneyConverter;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.journey.domain.service.JourneyCommandService;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JourneyCommandServiceImpl implements JourneyCommandService {
    private final JourneyJpaRepository journeyRepository;
    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public JourneyResponse.PostJourneyGoalDto postJourneyGoal(
            JourneyCommand.AddJourneyGoalCommand command
    ){
        //사용자 찾기
        User user = userRepository.findById(command.userId())
                .orElseThrow(()->new IllegalArgumentException("user not found"));

        //사용자가 현재 진행중인 여정이 있는지 검사
        journeyRepository.findByUserAndStatus(user, JourneyStatus.IN_PROGRESS)
            .ifPresent(e-> {
                throw new IllegalArgumentException("이미 진행중인 여정이 있습니다.");
            });

        //여정 객체 생성
        Journey journey = JourneyConverter.commandToJourney(command, user);

        //여정 생성
        journeyRepository.save(journey);

        return new JourneyResponse.PostJourneyGoalDto(journey.getId());
    };
}
