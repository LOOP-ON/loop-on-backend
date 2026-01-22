package com.loopon.challenge.application.service;

import com.loopon.challenge.application.converter.ChallengeConverter;
import com.loopon.challenge.application.dto.command.ChallengePostCommand;
import com.loopon.challenge.application.dto.response.ChallengePostResponse;
import com.loopon.challenge.domain.*;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.infrastructure.ExpeditionJpaRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.infrastructure.UserJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class ChallengeCommandService {

    private final ChallengeRepository challengeRepository;

    private final JourneyJpaRepository journeyJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ExpeditionJpaRepository expeditionJpaRepository;

    private final S3Service s3Service;


    @Transactional
    public ChallengePostResponse postChallenge(
            ChallengePostCommand dto
    ) {

        if (challengeRepository.existsByJourneyId(dto.journeyId())) {
            throw new BusinessException(ErrorCode.CHALLENGE_ALREADY_EXISTS);
        }

        Journey journey = journeyJpaRepository.findById(dto.journeyId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        User user = userJpaRepository.findById(dto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        Expedition expedition = expeditionJpaRepository.findById(dto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));


        Challenge challenge = ChallengeConverter.postChallenge(dto, user, journey, expedition);
        challengeRepository.save(challenge);


        updateChallengeHashtags(challenge, dto.hashtagList());
        saveNewImages(challenge, s3Service.uploadFiles(dto.imageList()));


        return ChallengeConverter.postChallenge(challenge);
    }



    private void saveNewImages(Challenge challenge, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            ChallengeImage challengeImage = ChallengeImage.builder()
                    .challenge(challenge)
                    .imageUrl(imageUrls.get(i))
                    .displayOrder(i)
                    .build();
            challengeRepository.saveChallengeImage(challengeImage);
        }
    }

    private void updateChallengeHashtags(Challenge challenge, List<String> newHashtagNames) {

        List<ChallengeHashtag> originChallengeHashtags = challengeRepository.findAllChallengeHashtagByChallengeId(challenge.getId());


        for (ChallengeHashtag challengeHashtag : originChallengeHashtags) {
            if (!newHashtagNames.contains(challengeHashtag.getHashtag().getName())) {
                challengeRepository.deleteChallengeHashtag(challengeHashtag.getId());
            }
        }


        Set<String> currentNames = new HashSet<>();
        for (ChallengeHashtag relation : originChallengeHashtags) {
            if (newHashtagNames.contains(relation.getHashtag().getName())) {
                currentNames.add(relation.getHashtag().getName());
            }
        }


        for (String name : newHashtagNames) {
            if (!currentNames.contains(name)) {
                Hashtag hashtag = challengeRepository.findHashtagByName(name)
                        .orElseGet(() -> challengeRepository.saveHashtag(Hashtag.builder().name(name).build()));

                challengeRepository.saveChallengeHashtag(new ChallengeHashtag(challenge, hashtag));
            }
        }
    }

}
