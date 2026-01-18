package com.loopon.challenge.application.service;

import com.loopon.challenge.application.converter.ChallengeConverter;
import com.loopon.challenge.application.dto.response.ChallengePostResponse;
import com.loopon.challenge.application.dto.command.ChallengePostCommand;
import com.loopon.challenge.domain.*;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.infrastructure.ExpeditionJpaRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.security.principal.PrincipalDetails;
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
            ChallengePostCommand dto,
            PrincipalDetails principalDetails
    ) {

        if (challengeRepository.existsByJourneyId(dto.journeyId())) {
            throw new BusinessException(ErrorCode.CHALLENGE_ALREADY_EXISTS);
        }

        Journey journey = journeyJpaRepository.findById(dto.journeyId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        User user = userJpaRepository.findById(principalDetails.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Expedition expedition = expeditionJpaRepository.findById(dto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));



        List<String> imageUrls = s3Service.uploadFiles(dto.imageList());



        Challenge challenge = ChallengeConverter.postChallenge(
                dto,
                user,
                journey,
                expedition
        );
        challengeRepository.save(challenge);


        List<String> hashtagList = dto.hashtagList();
        if (hashtagList == null) {
            hashtagList = new ArrayList<>();
        }

        List<Hashtag> existingHashtags = challengeRepository.findAllHashtagByNameIn(hashtagList);
        Set<String> existingNames = new HashSet<>();
        for (Hashtag h : existingHashtags) {
            existingNames.add(h.getName());
        }

        List<Hashtag> resultList = new ArrayList<>(existingHashtags);

        List<Hashtag> newHashtags = new ArrayList<>();
        for (String tag : hashtagList) {
            if (!existingNames.contains(tag)) {
                Hashtag newTag = Hashtag.builder()
                        .name(tag)
                        .build();

                newHashtags.add(newTag);
                resultList.add(newTag);
            }
        }

        if (!newHashtags.isEmpty()) {
            challengeRepository.saveAllHashtags(newHashtags);
        }

        for (Hashtag hashtag : resultList) {
            ChallengeHashtag challengeHashtag = new ChallengeHashtag(challenge, hashtag);
            challengeRepository.saveChallengeHashtag(challengeHashtag);
        }


        for (String imageUrl : imageUrls) {
            ChallengeImage challengeImage = ChallengeImage.builder()
                    .challenge(challenge)
                    .imageUrl(imageUrl)
                    .displayOrder(imageUrls.indexOf(imageUrl))
                    .build();
            challengeRepository.saveChallengeImage(challengeImage);
        }

        return ChallengeConverter.postChallenge(challenge);
    }
}
