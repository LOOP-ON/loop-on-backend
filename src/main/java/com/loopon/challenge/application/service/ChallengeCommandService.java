package com.loopon.challenge.application.service;

import com.loopon.challenge.application.converter.ChallengeConverter;
import com.loopon.challenge.application.dto.command.*;
import com.loopon.challenge.application.dto.response.*;
import com.loopon.challenge.domain.*;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.repository.ExpeditionRepository;
import com.loopon.global.s3.S3Service;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@AllArgsConstructor
public class ChallengeCommandService {

    private final ChallengeRepository challengeRepository;

    private final JourneyJpaRepository journeyJpaRepository;
    private final UserRepository userRepository;
    private final ExpeditionRepository expeditionRepository;

    private final S3Service s3Service;

    @Transactional
    public ChallengePostResponse postChallenge(
            ChallengePostCommand dto
    ) {

        if (challengeRepository.existsByJourneyId(dto.journeyId())) {
            throw new BusinessException(ErrorCode.CHALLENGE_ALREADY_EXISTS);
        }

        Journey journey = journeyJpaRepository.findById(dto.journeyId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Expedition expedition = null;
        if (dto.expeditionId() != null) {
            expedition = expeditionRepository.findById(dto.expeditionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));
        }

        Challenge challenge = ChallengeConverter.postChallenge(dto, user, journey, expedition);
        challengeRepository.save(challenge);

        updateChallengeHashtags(challenge, dto.hashtagList());
        updateChallengeImages(challenge, dto.imageList());


        return ChallengeConverter.postChallenge(challenge);
    }

    @Transactional
    public ChallengeLikeResponse likeChallenge(
            ChallengeLikeCommand dto
    ) {
        Challenge challenge = challengeRepository.findById(dto.challengeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Long challengeLikeId = null;

        if (dto.isLiked()) {
            ChallengeLike challengeLike = challengeRepository.findChallengeLikeByUserIdAndId(user.getId(), challenge.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
            challengeRepository.deleteChallengeLikeById(challengeLike.getId());

            challenge.updateLikeCount(-1);
        } else {
            ChallengeLike challengeLike = ChallengeLike.builder()
                    .user(user)
                    .challenge(challenge)
                    .build();
            challengeRepository.saveChallengeLike(challengeLike);
            challengeLikeId = challengeLike.getId();

            challenge.updateLikeCount(1);
        }

        return ChallengeConverter.likeChallenge(dto.challengeId(), challengeLikeId);
    }

    @Transactional
    public ChallengeModifyResponse modifyChallenge(
            ChallengeModifyCommand dto
    ) {

        Challenge challenge = challengeRepository.findById(dto.challengeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));
        Expedition expedition = expeditionRepository.findById(dto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));


        challenge.updateContent(dto.content());
        challenge.updateExpedition(expedition);

        updateChallengeHashtags(challenge, dto.hashtags());
        updateChallengeImages(challenge, dto.newImages(), dto.newSequence(), dto.remainImages(), dto.remainSequence());

        return ChallengeConverter.modifyChallenge(challenge.getId());
    }

    @Transactional
    public ChallengeCommentResponse commentChallenge(
            ChallengeCommentCommand dto
    ) {
        Challenge challenge = challengeRepository.findById(dto.challengeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Comment parent = null;
        if (dto.parentId() != null) {
            parent = challengeRepository.findCommentByCommentId(dto.parentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        }

        Comment comment = Comment.builder()
                .challenge(challenge)
                .user(user)
                .parent(parent)
                .content(dto.content())
                .likeCount(0)
                .build();

        challengeRepository.saveComment(comment);

        challenge.updateCommentCount(1);

        return ChallengeConverter.commentChallenge(comment.getId());
    }

    @Transactional
    public ChallengeLikeCommentResponse likeCommentChallenge(
            ChallengeLikeCommentCommand commandDto
    ) {
        Comment comment = challengeRepository.findCommentByCommentId(commandDto.commentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Optional<CommentLike> commentLike = challengeRepository.findCommentLikeByCommentIdAndUserId(commandDto.commentId(), commandDto.userId());
        Long commentLikeId = null;

        if (commentLike.isPresent()) {
            challengeRepository.deleteCommentLikeById(commentLike.get().getId());
            comment.updateLikeCount(-1);
        } else {
            CommentLike newCommentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();

            challengeRepository.saveCommentLike(newCommentLike);
            commentLikeId = newCommentLike.getId();
            comment.updateLikeCount(1);
        }

        return ChallengeConverter.likeCommentChallenge(commentLikeId);
    }

    @Transactional
    public Void deleteCommentChallenge(
            ChallengeDeleteCommentCommand commandDto
    ) {
        Comment comment = challengeRepository.findCommentByCommentId(commandDto.commentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        Challenge challenge = challengeRepository.findById(commandDto.challengeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));
        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkCommentWriter(comment, user);

        challengeRepository.deleteComment(comment);
        challenge.updateCommentCount(-1);

        return null;
    }

    @Transactional
    public Void deleteChallenge(
            ChallengeDeleteCommand commandDto
    ) {
        Challenge challenge = challengeRepository.findById(commandDto.challengeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));
        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkWriter(challenge, user);

        challengeRepository.delete(challenge);
        return null;
    }




    // ------------------------------- Helper Method ----------------------------------

    private void updateChallengeImages(
            Challenge challenge,
            List<MultipartFile> imageList
    ) {
        List<Integer> newSequence = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            newSequence.add(i);
        }

        updateChallengeImages(challenge, imageList, newSequence, new ArrayList<>(), new ArrayList<>());
    }

    private void updateChallengeImages(
            Challenge challenge,
            List<MultipartFile> newImages,
            List<Integer> newSequence,
            List<String> remainUrls,
            List<Integer> remainSequence
    ) {

        if (newImages.size() + remainUrls.size() > 10) {
            throw new BusinessException(ErrorCode.CHALLENGE_IMAGE_LIMIT);
        }

        List<ChallengeImage> challengeImages = challenge.getChallengeImages();

        for (ChallengeImage challengeImage : challengeImages) {
            if (!remainUrls.contains(challengeImage.getImageUrl())) {
                s3Service.deleteFile(challengeImage.getImageUrl());
            }
        }

        challengeImages.clear();
        challengeRepository.deleteAllChallengeImageById(challenge.getId());

        for (int i=0; i<remainUrls.size(); i++) {
            ChallengeImage challengeImage = ChallengeImage.builder()
                    .displayOrder(remainSequence.get(i))
                    .challenge(challenge)
                    .imageUrl(remainUrls.get(i))
                    .build();

            challenge.getChallengeImages().add(challengeImage);
            challengeRepository.saveChallengeImage(challengeImage);
        }

        for (int i = 0; i < newSequence.size(); i++) {
            String imageUrl = s3Service.uploadFile(newImages.get(i));

            ChallengeImage challengeImage = ChallengeImage.builder()
                    .challenge(challenge)
                    .imageUrl(imageUrl)
                    .displayOrder(newSequence.get(i))
                    .build();

            challengeRepository.saveChallengeImage(challengeImage);
            challenge.getChallengeImages().add(challengeImage);
        }
    }

    private void updateChallengeHashtags(Challenge challenge, List<String> newHashtagNames) {
        Set<String> distinctNameSet = new HashSet<>(newHashtagNames);

        if (distinctNameSet.size() > 5) {
            throw new BusinessException(ErrorCode.CHALLENGE_HASHTAG_LIMIT);
        }

        Iterator<ChallengeHashtag> iterator = challenge.getChallengeHashtags().iterator();
        while (iterator.hasNext()) {
            ChallengeHashtag existing = iterator.next();
            String existingName = existing.getHashtag().getName();

            if (!distinctNameSet.contains(existingName)) {
                iterator.remove();
            }
        }


        Set<String> currentNames = new HashSet<>();
        for (ChallengeHashtag ch : challenge.getChallengeHashtags()) {
            currentNames.add(ch.getHashtag().getName());
        }


        for (String name : distinctNameSet) {
            if (!currentNames.contains(name)) {
                Hashtag hashtag = challengeRepository.findHashtagByName(name)
                        .orElseGet(() -> challengeRepository.saveHashtag(Hashtag.builder().name(name).build()));

                ChallengeHashtag challengeHashtag = new ChallengeHashtag(challenge, hashtag);
                challenge.getChallengeHashtags().add(challengeHashtag);
            }
        }
    }

    private void checkCommentWriter(Comment comment, User user) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NOT_COMMENT_WRITER);
        }
    }

    private void checkWriter(Challenge challenge, User user) {
        User writer = challenge.getUser();
        if (!user.getId().equals(writer.getId())) {
            throw new BusinessException(ErrorCode.NOT_CHALLENGE_WRITER);
        }
    }

}
