package com.loopon.challenge.domain.repository;

import com.loopon.challenge.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
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

    List<ChallengeHashtag> findAllChallengeHashtagWithHashtagByChallengeId(Long id);

    Optional<Hashtag> findHashtagByName(String name);

    List<ChallengeImage> findAllImageByChallengeId(Long challengeId);

    Optional<Challenge> findById(Long challengeId);

    void deleteChallengeHashtag(ChallengeHashtagId challengeHashtagId);

    void deleteAllByExpeditionId(Long expeditionId);

    Slice<Challenge> findAllWithJourneyAndUserByExpeditionId(Long expeditionId, Pageable pageable);

    Boolean existsChallengeLikeByIdAndUserId(Long challengeId, Long userId);

    Page<ChallengeImage> findThumbnailsByUserId(Long userId, Pageable pageable);
}
