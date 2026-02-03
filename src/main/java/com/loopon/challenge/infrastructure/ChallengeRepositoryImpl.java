package com.loopon.challenge.infrastructure;

import com.loopon.challenge.domain.*;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.challenge.infrastructure.jpa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

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
    private final ChallengeLikeJpaRepository challengeLikeJpaRepository;

    @Override
    public Boolean existsByJourneyId(Long journeyId){
        return challengeJpaRepository.existsByJourneyId(journeyId);
    }

    @Override
    public Optional<Challenge> findById(Long challengeId) {
        return challengeJpaRepository.findById(challengeId);
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
    public Hashtag saveHashtag(Hashtag hashtag) {
        return hashtagJpaRepository.save(hashtag);
    }


    @Override
    public Optional<Hashtag> findHashtagByName(String name) {
        return hashtagJpaRepository.findByName(name);
    }

    @Override
    public List<ChallengeHashtag> findAllChallengeHashtagByChallengeId(Long challengeId) {
        return challengeHashtagJpaRepository.findAllByChallengeId(challengeId);
    }

    @Override
    public List<ChallengeHashtag> findAllChallengeHashtagWithHashtagByChallengeId(Long challengeId) {
        return challengeHashtagJpaRepository.findAllWithHashtagByChallengeId(challengeId);
    }

    @Override
    public List<ChallengeImage> findAllImageByChallengeId(Long challengeId) {
        return challengeImageJpaRepository.findAllByChallengeId(challengeId);
    }

    @Override
    public void deleteChallengeHashtag(ChallengeHashtagId challengeHashtagId) {
        challengeHashtagJpaRepository.deleteById(challengeHashtagId);
    }

    @Override
    public void deleteAllByExpeditionId(Long expeditionId) {
        challengeJpaRepository.deleteAllByExpeditionId(expeditionId);
    }

    @Override
    public Slice<Challenge> findAllWithJourneyAndUserByExpeditionId(Long expeditionId, Pageable pageable){
        return challengeJpaRepository.findAllWithJourneyAndUserByExpeditionId(expeditionId, pageable);
    }

    @Override
    public Boolean existsChallengeLikeByIdAndUserId(Long challengeId, Long userId) {
        return challengeLikeJpaRepository.existsByChallengeIdAndUserId(challengeId, userId);
    }

    @Override
    public Page<ChallengeImage> findThumbnailsByUserId(Long userId, Pageable pageable) {
        return challengeImageJpaRepository.findThumbnailsByUserId(userId, pageable);
    }
}
