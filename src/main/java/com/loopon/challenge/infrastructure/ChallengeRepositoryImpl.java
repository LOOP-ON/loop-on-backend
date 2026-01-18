package com.loopon.challenge.infrastructure;

import com.loopon.challenge.domain.*;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.challenge.infrastructure.jpa.ChallengeHashtagJpaRepository;
import com.loopon.challenge.infrastructure.jpa.ChallengeImageJpaRepository;
import com.loopon.challenge.infrastructure.jpa.ChallengeJpaRepository;
import com.loopon.challenge.infrastructure.jpa.HashtagJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChallengeRepositoryImpl implements ChallengeRepository {
    private final ChallengeJpaRepository challengeJpaRepository;
    private final ChallengeImageJpaRepository challengeImageJpaRepository;
    private final HashtagJpaRepository hashtagJpaRepository;
    private final ChallengeHashtagJpaRepository challengeHashtagJpaRepository;

    @Override
    public Boolean existsByJourneyId(Long journeyId){
        return challengeJpaRepository.existsByJourneyId(journeyId);
    }

    @Override
    public Optional<Hashtag> findHashtagByName(String hashtag){
        return hashtagJpaRepository.findByName(hashtag);
    }

    @Override
    public Long save(Challenge challenge) {
        return challengeJpaRepository.save(challenge).getId();
    }

    @Override
    public ChallengeHashtagId saveChallengeHashtag(ChallengeHashtag challengeHashtag) {
        return challengeHashtagJpaRepository.save(challengeHashtag).getId();
    }

    @Override
    public Long saveChallengeImage(ChallengeImage challengeImage) {
        return challengeImageJpaRepository.save(challengeImage).getId();
    }


    @Override
    public List<Hashtag> findAllHashtagByNameIn(List<String> nameList) {
        return hashtagJpaRepository.findAllByNameIn(nameList);
    }

    @Override
    public List<Long> saveAllHashtags(List<Hashtag> hashtagList) {
        List<Hashtag> resultList = hashtagJpaRepository.saveAll(hashtagList);

        List<Long> hashtagIdList = new ArrayList<>();
        for (Hashtag hashtag : resultList) {
            hashtagIdList.add(hashtag.getId());
        }

        return hashtagIdList;
    }
}
