package com.loopon.challenge.domain.repository;

import com.loopon.challenge.application.dto.response.ChallengePreviewResponse;
import com.loopon.challenge.domain.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    List<Hashtag> findAllHashtagByNameIn(List<String> hashtagList);

    List<ChallengeImage> findAllImageByChallengeId(Long challengeId);

    Optional<Challenge> findById(Long challengeId);

    void deleteChallengeHashtag(ChallengeHashtag challengeHashtag);

    void deleteAllByExpeditionId(Long expeditionId);

    Slice<Challenge> findAllWithJourneyAndUserByExpeditionId(Long expeditionId, Pageable pageable);

    Boolean existsChallengeLikeByIdAndUserId(Long challengeId, Long userId);

    void saveAllHashtags(List<Hashtag> hashtagList);

    Optional<ChallengeLike> findChallengeLikeByUserIdAndId(Long userId, Long challengeId);

    void deleteChallengeLikeById(Long challengeLikeId);

    void saveChallengeLike(ChallengeLike challengeLike);

    void deleteAllChallengeImageById(Long challengeId);

    Optional<Comment> findCommentByCommentId(Long commentId);

    Slice<Comment> findCommentsWithUserByChallengeId(Long challengeId, Pageable pageable);

    Optional<CommentLike> findCommentLikeByCommentIdAndUserId(Long commentId, Long userId);

    void deleteCommentLikeById(Long commentLikeId);

    void saveCommentLike(CommentLike newCommentLike);

    void deleteComment(Comment comment);

    void delete(Challenge challenge);

    Slice<ChallengePreviewResponse> findViewByUserId(Long userId, Pageable pageable);

    List<Comment> findAllCommentWithUserByParentIdIn(List<Long> parentIds);

    Slice<Challenge> findTrendingChallenges(LocalDateTime threeDaysAgo, Pageable pageable);

    Slice<Challenge> findFriendsChallenges(List<Long> friendsIds, List<Long> trendingIds, Pageable pageable);

    Set<Long> findLikedChallengeIds(Long userId, List<Long> challengesIds);

    void saveComment(Comment comment);
}
