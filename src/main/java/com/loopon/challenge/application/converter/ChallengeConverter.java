package com.loopon.challenge.application.converter;

import com.loopon.challenge.application.dto.command.ChallengeCommentCommand;
import com.loopon.challenge.application.dto.command.ChallengeDeleteCommand;
import com.loopon.challenge.application.dto.command.ChallengeDeleteCommentCommand;
import com.loopon.challenge.application.dto.command.ChallengeGetCommentCommand;
import com.loopon.challenge.application.dto.command.ChallengeLikeCommand;
import com.loopon.challenge.application.dto.command.ChallengeLikeCommentCommand;
import com.loopon.challenge.application.dto.command.ChallengeModifyCommand;
import com.loopon.challenge.application.dto.command.ChallengeMyCommand;
import com.loopon.challenge.application.dto.command.ChallengeOthersCommand;
import com.loopon.challenge.application.dto.command.ChallengePostCommand;
import com.loopon.challenge.application.dto.command.ChallengeViewCommand;
import com.loopon.challenge.application.dto.request.ChallengeCommentRequest;
import com.loopon.challenge.application.dto.request.ChallengeLikeCommentRequest;
import com.loopon.challenge.application.dto.request.ChallengeLikeRequest;
import com.loopon.challenge.application.dto.request.ChallengeModifyRequest;
import com.loopon.challenge.application.dto.request.ChallengePostRequest;
import com.loopon.challenge.application.dto.response.ChallengeCombinedViewResponse;
import com.loopon.challenge.application.dto.response.ChallengeCommentResponse;
import com.loopon.challenge.application.dto.response.ChallengeGetCommentResponse;
import com.loopon.challenge.application.dto.response.ChallengeGetResponse;
import com.loopon.challenge.application.dto.response.ChallengeLikeCommentResponse;
import com.loopon.challenge.application.dto.response.ChallengeLikeResponse;
import com.loopon.challenge.application.dto.response.ChallengeModifyResponse;
import com.loopon.challenge.application.dto.response.ChallengePostResponse;
import com.loopon.challenge.application.dto.response.ChallengeViewResponse;
import com.loopon.challenge.domain.Challenge;
import com.loopon.challenge.domain.Comment;
import com.loopon.expedition.domain.Expedition;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.domain.Journey;
import com.loopon.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.loopon.challenge.application.dto.command.ChallengeModifyCommand.builder;


public class ChallengeConverter {

    public static ChallengePostCommand postChallenge(
            ChallengePostRequest requestDto,
            List<MultipartFile> imageFiles,
            Long userId
    ) {
        return ChallengePostCommand.builder()
                .imageList(imageFiles)
                .hashtagList(requestDto.hashtagList())
                .content(requestDto.content())
                .expeditionId(requestDto.expeditionId())
                .journeyId(requestDto.journeyId())
                .userId(userId)
                .build();
    }

    public static Challenge postChallenge(
            ChallengePostCommand dto,
            User user,
            Journey journey,
            Expedition expedition
    ) {
        return Challenge.builder()
                .user(user)
                .journey(journey)
                .expedition(expedition)
                .content(dto.content())
                .build();
    }

    public static ChallengePostResponse postChallenge(
            Challenge challenge
    ) {
        return ChallengePostResponse.builder()
                .challengeId(challenge.getId())
                .build();
    }

    public static ChallengeGetResponse getChallenge(
            Long challengeId,
            List<String> imageList,
            List<String> hashtagList,
            String content,
            Long expeditionId

    ) {
        return ChallengeGetResponse.builder()
                .challengeId(challengeId)
                .imageList(imageList)
                .hashtagList(hashtagList)
                .content(content)
                .expeditionId(expeditionId)
                .build();
    }


    public static ChallengeLikeCommand likeChallenge(
            Long challengeId,
            ChallengeLikeRequest requestDto,
            PrincipalDetails principalDetails
    ) {
        return ChallengeLikeCommand.builder()
                .challengeId(challengeId)
                .isLiked(requestDto.isLiked())
                .userId(principalDetails.getUserId())
                .build();
    }

    public static ChallengeLikeResponse likeChallenge(
            Long challengeId,
            Long challengeLikeId
    ) {
        return ChallengeLikeResponse.builder()
                .challengeId(challengeId)
                .challengeLikeId(challengeLikeId)
                .build();
    }

    public static ChallengeModifyCommand modifyChallenge(
            ChallengeModifyRequest dto,
            Long challengeId,
            List<MultipartFile> imageFiles
    ) {

        List<MultipartFile> safeImageFiles = (imageFiles != null) ? imageFiles : Collections.emptyList();
        List<Integer> safeSequence = (dto.newImagesSequence() != null) ? dto.newImagesSequence() : Collections.emptyList();

        if (safeImageFiles.size() != safeSequence.size() || dto.remainImages().size() != dto.remainImagesSequence().size()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (safeImageFiles.size() + dto.remainImages().size() > 10) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return builder()
                .challengeId(challengeId)
                .newImages(safeImageFiles)
                .newSequence(safeSequence)
                .remainImages(dto.remainImages())
                .remainSequence(dto.remainImagesSequence())
                .hashtags(dto.hashtagList())
                .content(dto.content())
                .expeditionId(dto.expeditionId())
                .build();
    }

    public static ChallengeModifyResponse modifyChallenge(
            Long challengeId
    ) {
        return ChallengeModifyResponse.builder()
                .challengeId(challengeId)
                .build();
    }

    public static ChallengeCommentCommand commentChallenge(
            Long challengeId,
            ChallengeCommentRequest requestDto,
            Long userId
    ) {
        return ChallengeCommentCommand.builder()
                .challengeId(challengeId)
                .userId(userId)
                .content(requestDto.content())
                .parentId(requestDto.parentId())
                .build();
    }

    public static ChallengeCommentResponse commentChallenge(
            Long commentId
    ) {
        return ChallengeCommentResponse.builder()
                .commentId(commentId)
                .build();
    }

    public static ChallengeGetCommentCommand getCommentChallenge(
            Long challengeId,
            Pageable pageable
    ) {
        return ChallengeGetCommentCommand.builder()
                .challengeId(challengeId)
                .pageable(pageable)
                .build();
    }

    public static ChallengeGetCommentResponse getCommentChallenge(
            Comment comment,
            List<Comment> children
    ) {
        List<ChallengeGetCommentResponse> childResponses = new ArrayList<>();

        for (Comment child : children) {
            childResponses.add(ChallengeGetCommentResponse.builder()
                    .commentId(child.getId())
                    .content(child.getContent())
                    .nickName(child.getUser().getNickname())
                    .profileImageUrl(child.getUser().getProfileImageUrl())
                    .likeCount(child.getLikeCount())
                    .children(null)
                    .build());
        }

        return ChallengeGetCommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .nickName(comment.getUser().getNickname())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .likeCount(comment.getLikeCount())
                .children(childResponses)
                .build();
    }

    public static ChallengeLikeCommentCommand likeCommentChallenge(
            Long commentId,
            ChallengeLikeCommentRequest requestDto,
            Long userId
    ) {
        return ChallengeLikeCommentCommand.builder()
                .commentId(commentId)
                .userId(userId)
                .isLiked(requestDto.isLiked())
                .build();
    }

    public static ChallengeLikeCommentResponse likeCommentChallenge(
            Long commentLikeId
    ) {
        return ChallengeLikeCommentResponse.builder()
                .commentLikeId(commentLikeId)
                .build();
    }

    public static ChallengeDeleteCommentCommand deleteCommentChallenge(
            Long commentId,
            Long challengeId,
            Long userId
    ) {
        return ChallengeDeleteCommentCommand.builder()
                .commentId(commentId)
                .challengeId(challengeId)
                .userId(userId)
                .build();

    }

    public static ChallengeDeleteCommand deleteChallenge(
            Long challengeId,
            Long userId

    ) {
        return ChallengeDeleteCommand.builder()
                .challengeId(challengeId)
                .userId(userId)
                .build();
    }


    public static ChallengeMyCommand myChallenge(
            Long userId,
            Pageable pageable
    ) {
        return ChallengeMyCommand.builder()
                .userId(userId)
                .pageable(pageable)
                .build();
    }

    public static ChallengeOthersCommand othersChallenge(
            Long userId,
            String nickname,
            Pageable pageable
    ) {
        return ChallengeOthersCommand.builder()
                .userId(userId)
                .nickname(nickname)
                .pageable(pageable)
                .build();
    }

    public static ChallengeViewCommand viewChallenge(
            Long userId,
            Pageable trendingPage,
            Pageable friendPage
    ) {
        return ChallengeViewCommand.builder()
                .userId(userId)
                .trendingPage(trendingPage)
                .friendsPage(friendPage)
                .build();
    }

    public static ChallengeViewResponse viewChallenge(
            Challenge challenge,
            List<String> imageUrls,
            List<String> hashtags,
            Boolean isLiked
    ) {
        return ChallengeViewResponse.builder()
                .challengeId(challenge.getId())
                .journeySequence(challenge.getJourney().getJourneyOrder()) // 여정 번호 필요!
                .imageUrls(imageUrls)
                .content(challenge.getContent())
                .hashtags(hashtags)
                .createdAt(challenge.getCreatedAt())
                .nickname(challenge.getUser().getNickname())
                .profileImageUrl(challenge.getUser().getProfileImageUrl())
                .isLiked(isLiked)
                .likeCount(challenge.getLikeCount())
                .build();
    }

    public static ChallengeCombinedViewResponse combineChallenge(
            Slice<ChallengeViewResponse> trendingResponse,
            Slice<ChallengeViewResponse> friendsResponse
    ) {
        return ChallengeCombinedViewResponse.builder()
                .trendingChallenges(trendingResponse)
                .friendChallenges(friendsResponse)
                .build();
    }
}
