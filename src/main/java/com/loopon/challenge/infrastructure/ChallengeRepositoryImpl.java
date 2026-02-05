package com.loopon.challenge.infrastructure;

import com.loopon.challenge.application.dto.response.ChallengePreviewResponse;
import com.loopon.challenge.domain.*;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.challenge.infrastructure.jpa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChallengeRepositoryImpl implements ChallengeRepository {
    private final ChallengeJpaRepository challengeJpaRepository;
    private final ChallengeImageJpaRepository challengeImageJpaRepository;
    private final HashtagJpaRepository hashtagJpaRepository;
    private final ChallengeHashtagJpaRepository challengeHashtagJpaRepository;
    private final ChallengeLikeJpaRepository challengeLikeJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final CommentLikeJpaRepository commentLikeJpaRepository;

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
    public void deleteChallengeHashtag(ChallengeHashtag challengeHashtag) {
        challengeHashtagJpaRepository.delete(challengeHashtag);
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
    public List<Hashtag> findAllHashtagByNameIn(List<String> hashtagList) {
        return hashtagJpaRepository.findAllByNameIn(hashtagList);
    }

    @Override
    public void saveAllHashtags(List<Hashtag> hashtagList) {
        hashtagJpaRepository.saveAll(hashtagList);
    }

    @Override
    public void deleteChallengeLikeById(Long challengeLikeId) {
        challengeLikeJpaRepository.deleteById(challengeLikeId);
    }

    @Override
    public Optional<ChallengeLike> findChallengeLikeByUserIdAndId(Long userId, Long challengeId) {
        return challengeLikeJpaRepository.findByUserIdAndChallengeId(userId, challengeId);
    }

    @Override
    public void saveChallengeLike(ChallengeLike challengeLike) {
        challengeLikeJpaRepository.save(challengeLike);
    }

    @Override
    public void deleteAllChallengeImageById(Long challengeId) {
        challengeImageJpaRepository.deleteAllByChallengeId(challengeId);
        challengeImageJpaRepository.flush();
    }

    @Override
    public Optional<Comment> findCommentByCommentId(Long commentId) {
        return commentJpaRepository.findById(commentId);
    }

    @Override
    public Slice<Comment> findCommentsWithUserByChallengeId(Long challengeId, Pageable pageable) {
        return commentJpaRepository.findWithUserByChallengeId(challengeId, pageable);
    }

    @Override
    public Optional<CommentLike> findCommentLikeByCommentIdAndUserId(Long commentId, Long userId) {
        return commentLikeJpaRepository.findByCommentIdAndUserId(commentId, userId);
    }

    @Override
    public void deleteCommentLikeById(Long commentLikeId) {
        commentLikeJpaRepository.deleteById(commentLikeId);
    }

    @Override
    public void saveCommentLike(CommentLike commentLike) {
        commentLikeJpaRepository.save(commentLike);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentJpaRepository.delete(comment);
    }

    @Override
    public void delete(Challenge challenge) {
        challengeJpaRepository.delete(challenge);
    }

    @Override
    public Slice<ChallengePreviewResponse> findViewByUserId(Long userId, Pageable pageable) {
        return challengeJpaRepository.findViewByUserId(userId, pageable);
    }

    @Override
    public List<Comment> findAllCommentWithUserByParentIdIn(List<Long> parentIds) {
        return commentJpaRepository.findAllWithUserByParentIdIn(parentIds);
    }

    @Override
    public Slice<Challenge> findTrendingChallenges(LocalDateTime threeDaysAgo, Pageable pageable) {
        return challengeJpaRepository.findTrendingChallenges(threeDaysAgo, pageable);
    }

    @Override
    public Slice<Challenge> findFriendsChallenges(List<Long> friendsIds, List<Long> trendingIds, Pageable pageable) {
        return challengeJpaRepository.findFriendsChallenges(friendsIds, trendingIds, pageable);
    }

    @Override
    public Set<Long> findLikedChallengeIds(Long userId, List<Long> challengesIds) {
        return challengeLikeJpaRepository.findLikedChallengeIds(userId, challengesIds);
    }

    @Override
    public void saveComment(Comment comment) {
        commentJpaRepository.save(comment);
    }

    @Override
    public Boolean existsCommentLikeByCommentIdAndUserId(Long commentId, Long userId) {
        return commentLikeJpaRepository.existsByIdAndUserId(commentId, userId);
    }
}
