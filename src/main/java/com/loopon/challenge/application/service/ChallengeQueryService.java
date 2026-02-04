package com.loopon.challenge.application.service;

import com.loopon.challenge.application.converter.ChallengeConverter;
import com.loopon.challenge.application.dto.command.ChallengeGetCommentCommand;
import com.loopon.challenge.application.dto.command.ChallengeMyCommand;
import com.loopon.challenge.application.dto.command.ChallengeOthersCommand;
import com.loopon.challenge.application.dto.command.ChallengeViewCommand;
import com.loopon.challenge.application.dto.response.*;
import com.loopon.challenge.domain.Challenge;
import com.loopon.challenge.domain.ChallengeHashtag;
import com.loopon.challenge.domain.ChallengeImage;
import com.loopon.challenge.domain.Comment;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.application.converter.ExpeditionConverter;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.User;
import com.loopon.user.domain.UserVisibility;
import com.loopon.user.domain.repository.FriendRepository;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional(readOnly = true)
    public ChallengeGetResponse getChallenge(
            Long challengeId
    ) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        List<ChallengeImage> imageList = challengeRepository.findAllImageByChallengeId(challengeId);
        List<ChallengeHashtag> tagList = challengeRepository.findAllChallengeHashtagWithHashtagByChallengeId(challengeId);

        imageList.sort(Comparator.comparing(ChallengeImage::getDisplayOrder));

        List<String> urlList = new ArrayList<>();
        for (ChallengeImage image : imageList){
            urlList.add(image.getImageUrl());
        }

        List<String> hashtagList = new ArrayList<>();
        for (ChallengeHashtag hashtag : tagList){
            hashtagList.add(hashtag.getHashtag().getName());
        }

        return ChallengeConverter.getChallenge(
                challenge.getId(),
                urlList,
                hashtagList,
                challenge.getContent(),
                (challenge.getExpedition() != null) ? challenge.getExpedition().getId() : null
        );
    }

    @Transactional(readOnly = true)
    public Slice<ChallengeGetCommentResponse> getCommentChallenge(
            ChallengeGetCommentCommand commandDto
    ) {
        Challenge challenge = challengeRepository.findById(commandDto.challengeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Slice<Comment> comments = challengeRepository.findCommentsWithUserByChallengeId(challenge.getId(), commandDto.pageable());

        List<Long> parentIds = new ArrayList<>();
        for (Comment comment : comments.getContent()) {
            parentIds.add(comment.getId());
        }

        List<Comment> children = challengeRepository.findAllCommentWithUserByParentIdIn(parentIds);

        Map<Long, List<Comment>> childrenMap = new HashMap<>();
        for (Comment child : children) {
            Long parentId = child.getParent().getId();

            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(child);
        }

        return comments.map(comment ->
            ChallengeConverter.getCommentChallenge(comment, childrenMap.getOrDefault(comment.getId(), new ArrayList<>()))
        );
    }

    @Transactional(readOnly = true)
    public Slice<ChallengePreviewResponse> myChallenge(
            ChallengeMyCommand commandDto
    ) {
        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        return challengeRepository.findViewByUserId(user.getId(), commandDto.pageable());
    }

    @Transactional(readOnly = true)
    public Slice<ChallengePreviewResponse> othersChallenge(
            ChallengeOthersCommand commandDto
    ) {
        User myself = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User target = userRepository.findByNickname(commandDto.nickname())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkAllowed(target, myself);

        return challengeRepository.findViewByUserId(target.getId(), commandDto.pageable());
    }

    @Transactional(readOnly = true)
    public ChallengeCombinedViewResponse viewChallenge(
            ChallengeViewCommand commandDto
    ) {

        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        Slice<Challenge> trendingChallenges = challengeRepository.findTrendingChallenges(
                threeDaysAgo, commandDto.trendingPage());


        List<Long> trendingIds = new ArrayList<>();
        for (Challenge challenge : trendingChallenges.getContent()) {
            trendingIds.add(challenge.getId());
        }

        List<Long> friendsIds = getFriendsIds(user);

        Slice<Challenge> friendsChallenges = challengeRepository.findFriendsChallenges(
                friendsIds, trendingIds, commandDto.friendsPage());

        List<Long> challengesIds = Stream.concat(
                trendingChallenges.getContent().stream(),
                friendsChallenges.getContent().stream()
        ).map(Challenge::getId).distinct().toList();

        Set<Long> likedChallengeIds;

        if (challengesIds.isEmpty()) {
            likedChallengeIds = Collections.emptySet();
        } else {
            likedChallengeIds = challengeRepository.findLikedChallengeIds(user.getId(), challengesIds);
        }

        Slice<ChallengeViewResponse> trendingResponse = trendingChallenges.map(challenge ->
                convertToResponse(challenge, user, likedChallengeIds));

        Slice<ChallengeViewResponse> friendsResponse = friendsChallenges.map(challenge ->
                convertToResponse(challenge, user, likedChallengeIds));


        return ChallengeConverter.combineChallenge(trendingResponse, friendsResponse);
    }





    // --------------------------- Helper Method --------------------------------

    private void checkAllowed(User targetUser, User myself) {
        if (targetUser.getId().equals(myself.getId())) {
            return;
        }

        if (targetUser.getVisibility().equals(UserVisibility.PUBLIC)) {
            return;
        }

        boolean isFriend = friendRepository.existsFriendship(
                targetUser.getId(),
                myself.getId(),
                FriendStatus.ACCEPTED
        );

        if (!isFriend) {
            throw new BusinessException(ErrorCode.CHALLENGE_FORBIDDEN);
        }
    }

    private List<Long> getFriendsIds(User user) {
        List<Friend> friends = friendRepository.findAcceptedFriendsByUserId(user.getId(), FriendStatus.ACCEPTED);

        List<Long> friendIds = new ArrayList<>();
        for (Friend friend : friends) {
            Long requesterId = friend.getRequester().getId();
            Long receiverId = friend.getReceiver().getId();

            if (requesterId.equals(user.getId())) {
                friendIds.add(receiverId);
            } else {
                friendIds.add(requesterId);
            }
        }

        return friendIds;
    }


    private ChallengeViewResponse convertToResponse(Challenge challenge, User user, Set<Long> likedChallengeIds) {
        List<String> imageUrls = getImageUrls(challenge);
        List<String> hashtags = getHashtags(challenge);
        Boolean isLiked = likedChallengeIds.contains(challenge.getId());
        return ChallengeConverter.viewChallenge(challenge, imageUrls, hashtags, isLiked);
    }

    private List<String> getHashtags(Challenge challenge) {
        List<ChallengeHashtag> challengeHashtagList = challenge.getChallengeHashtags();

        List<String> hashtags = new ArrayList<>();
        for (ChallengeHashtag challengeHashtag : challengeHashtagList) {
            hashtags.add(challengeHashtag.getHashtag().getName());
        }

        return hashtags;
    }

    private List<String> getImageUrls(Challenge challenge) {
        List<ChallengeImage> imageList = challenge.getChallengeImages();

        List<String> imageUrls = new ArrayList<>();
        for (ChallengeImage challengeImage : imageList) {
            imageUrls.add(challengeImage.getImageUrl());
        }

        return imageUrls;
    }

}
