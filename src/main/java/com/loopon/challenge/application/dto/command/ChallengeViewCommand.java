package com.loopon.challenge.application.dto.command;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public record ChallengeViewCommand(
        Long userId,
        Pageable trendingPage,
        Pageable friendsPage
) {
}
