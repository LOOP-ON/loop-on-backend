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

    List<Hashtag> findAllHashtagByNameIn(List<String> strings);

    List<Long> saveAllHashtags(List<Hashtag> newHashtags);
}
