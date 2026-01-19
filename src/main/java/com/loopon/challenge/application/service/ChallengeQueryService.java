package com.loopon.challenge.application.service;

import com.loopon.challenge.application.converter.ChallengeConverter;
import com.loopon.challenge.application.dto.response.ChallengeGetResponse;
import com.loopon.challenge.domain.Challenge;
import com.loopon.challenge.domain.ChallengeImage;
import com.loopon.challenge.domain.Hashtag;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;

    @Transactional(readOnly = true)
    public ChallengeGetResponse getChallenge(
            Long challengeId
    ) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        List<ChallengeImage> imageList = challengeRepository.findAllImageByChallengeId(challengeId);
        List<Hashtag> tagList = challengeRepository.findAllHashtagByChallengeId(challengeId);

        List<String> urlList = new ArrayList<>();
        for (ChallengeImage image : imageList){
            urlList.add(image.getImageUrl());
        }

        List<String> hashtagList = new ArrayList<>();
        for (Hashtag hashtag : tagList){
            hashtagList.add(hashtag.getName());
        }

        return ChallengeConverter.getChallenge(
                challenge.getId(),
                urlList,
                hashtagList,
                challenge.getContent(),
                challenge.getExpedition().getId()
        );
    }
}
