package com.loopon.challenge.domain.repository;

import com.loopon.challenge.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository {

    Boolean existsByJourneyId(Long journeyId);

    Long save(Challenge challenge);

    ChallengeHashtagId saveChallengeHashtag(ChallengeHashtag challengeHashtag);

    Long saveChallengeImage(ChallengeImage challengeImage);

    Hashtag saveHashtag(Hashtag hashtag);

    List<ChallengeHashtag> findAllChallengeHashtagByChallengeId(Long id);

    List<Hashtag> findAllHashtagByChallengeId(Long id);

    Optional<Hashtag> findHashtagByName(String name);

    List<ChallengeImage> findAllImageByChallengeId(Long challengeId);

    Optional<Challenge> findById(Long challengeId);

    void deleteChallengeHashtag(ChallengeHashtagId challengeHashtagId);
}
