package com.loopon.challenge.application.converter;

import com.loopon.challenge.application.dto.command.ChallengePostCommand;
import com.loopon.challenge.application.dto.response.ChallengeGetResponse;
import com.loopon.challenge.application.dto.response.ChallengePostResponse;
import com.loopon.challenge.application.dto.request.ChallengePostRequest;
import com.loopon.challenge.domain.Challenge;
import com.loopon.expedition.domain.Expedition;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.domain.Journey;
import com.loopon.user.domain.User;
import java.util.List;


public class ChallengeConverter {

    public static ChallengePostCommand postChallenge(
            ChallengePostRequest requestDto,
            PrincipalDetails principalDetails
    ) {
        return ChallengePostCommand.builder()
                .imageList(requestDto.imageList())
                .hashtagList(requestDto.hashtagList())
                .content(requestDto.content())
                .expeditionId(requestDto.expeditionId())
                .journeyId(requestDto.journeyId())
                .userId(principalDetails.getUserId())
                .build();
    }

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

    public static ChallengeGetResponse getChallenge(
            List<String> imageList,
            List<String> hashtagList,
            String content,
            Long expeditionId

    ) {
        return ChallengeGetResponse.builder()
                .imageList(imageList)
                .hashtagList(hashtagList)
                .content(content)
                .expeditionId(expeditionId)
                .build();
    }

}
