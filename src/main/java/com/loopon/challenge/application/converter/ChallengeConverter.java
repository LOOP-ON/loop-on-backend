package com.loopon.challenge.application.converter;

import com.loopon.challenge.application.dto.response.ChallengePostResponse;
import com.loopon.challenge.application.dto.command.ChallengePostCommand;
import com.loopon.challenge.domain.Challenge;
import com.loopon.expedition.domain.Expedition;
import com.loopon.journey.domain.Journey;
import com.loopon.user.domain.User;

public class ChallengeConverter {

    public static Challenge postChallenge(
            ChallengePostCommand dto,
            User user,
            Journey journey,
            Expedition expedition
    ) {
        return Challenge.builder()
                .user(user)
                .journey(journey)
                .expedition(expedition)
                .content(dto.content())
                .build();
    }

    public static ChallengePostResponse postChallenge(
            Challenge challenge
    ) {
        return ChallengePostResponse.builder()
                .challengeId(challenge.getId())
                .build();
    }

}
