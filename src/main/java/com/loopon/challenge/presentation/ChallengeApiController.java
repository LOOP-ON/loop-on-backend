package com.loopon.challenge.presentation;

import com.loopon.challenge.application.converter.ChallengeConverter;
import com.loopon.challenge.application.dto.command.*;
import com.loopon.challenge.application.dto.request.ChallengeCommentRequest;
import com.loopon.challenge.application.dto.request.ChallengeLikeCommentRequest;
import com.loopon.challenge.application.dto.request.ChallengeLikeRequest;
import com.loopon.challenge.application.dto.request.ChallengeModifyRequest;
import com.loopon.challenge.application.dto.request.ChallengePostRequest;
import com.loopon.challenge.application.dto.response.*;
import com.loopon.challenge.application.service.ChallengeCommandService;
import com.loopon.challenge.application.service.ChallengeQueryService;
import com.loopon.challenge.presentation.docs.ChallengeApiDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class ChallengeApiController implements ChallengeApiDocs {

    private final ChallengeCommandService challengeCommandService;
    private final ChallengeQueryService challengeQueryService;

    @Override
    @PostMapping(value = "/api/challenges", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<ChallengePostResponse> postChallenge(
            ChallengePostRequest requestDto,
            List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengePostCommand commandDto =
                ChallengeConverter.postChallenge(requestDto, imageFiles, principalDetails.getUserId());

        return CommonResponse.onSuccess(challengeCommandService.postChallenge(commandDto));
    }

    @Override
    @GetMapping("/api/challenges/{challengeId}")
    public CommonResponse<ChallengeGetResponse> getChallenge(
            @PathVariable Long challengeId
    ) {
        return CommonResponse.onSuccess(challengeQueryService.getChallenge(challengeId));
    }

    @Override
    @PostMapping("/api/challenges/{challengeId}/like")
    public CommonResponse<ChallengeLikeResponse> likeChallenge(
            @PathVariable Long challengeId,
            @RequestBody ChallengeLikeRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengeLikeCommand commandDto = ChallengeConverter.likeChallenge(challengeId, requestDto, principalDetails);
        return CommonResponse.onSuccess(challengeCommandService.likeChallenge(commandDto));
    }

    @Override
    @PatchMapping(value = "/api/challenges/{challengeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<ChallengeModifyResponse> patchChallenge(
            ChallengeModifyRequest requestDto,
            List<MultipartFile> imageFiles,
            @PathVariable Long challengeId
    ) {
        ChallengeModifyCommand commandDto = ChallengeConverter.modifyChallenge(requestDto, challengeId, imageFiles);
        return CommonResponse.onSuccess(challengeCommandService.modifyChallenge(commandDto));
    }

    @Override
    @PostMapping("/api/challenges/{challengeId}/comments")
    public CommonResponse<ChallengeCommentResponse> commentChallenge(
            @PathVariable Long challengeId,
            @RequestBody ChallengeCommentRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengeCommentCommand commandDto = ChallengeConverter.commentChallenge(challengeId, requestDto, principalDetails.getUserId());
        return CommonResponse.onSuccess(challengeCommandService.commentChallenge(commandDto));
    }

    @Override
    @GetMapping("/api/challenges/{challengeId}/comments")
    public CommonResponse<Slice<ChallengeGetCommentResponse>> getCommentChallenge(
            @PathVariable Long challengeId,
            Pageable pageable
    ) {
        ChallengeGetCommentCommand commandDto = ChallengeConverter.getCommentChallenge(challengeId, pageable);
        return CommonResponse.onSuccess(challengeQueryService.getCommentChallenge(commandDto));
    }

    @Override
    @PostMapping("/api/challenges/comment/{commentId}/like")
    public CommonResponse<ChallengeLikeCommentResponse> likeCommentChallenge(
            @PathVariable Long commentId,
            @RequestBody ChallengeLikeCommentRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengeLikeCommentCommand commandDto = ChallengeConverter.likeCommentChallenge(commentId, requestDto, principalDetails.getUserId());
        return CommonResponse.onSuccess(challengeCommandService.likeCommentChallenge(commandDto));
    }

    @Override
    @DeleteMapping("/api/challenges/{challengeId}/comments/{commentId}")
    public CommonResponse<Void> deleteCommentChallenge(
            @PathVariable Long commentId,
            @PathVariable Long challengeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengeDeleteCommentCommand commandDto = ChallengeConverter.deleteCommentChallenge(commentId, challengeId, principalDetails.getUserId());
        return CommonResponse.onSuccess(challengeCommandService.deleteCommentChallenge(commandDto));
    }

    @Override
    @DeleteMapping("/api/challenges/{challengeId}")
    public CommonResponse<Void> deleteChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengeDeleteCommand commandDto = ChallengeConverter.deleteChallenge(challengeId, principalDetails.getUserId());
        return CommonResponse.onSuccess(challengeCommandService.deleteChallenge(commandDto));
    }

    @Override
    @GetMapping("/api/challenges/users/me")
    public CommonResponse<Slice<ChallengePreviewResponse>> myChallenge(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    ) {
        ChallengeMyCommand commandDto = ChallengeConverter.myChallenge(principalDetails.getUserId(), pageable);
        return CommonResponse.onSuccess(challengeQueryService.myChallenge(commandDto));
    }

    @Override
    @GetMapping("/api/challenges/users/{nickname}")
    public CommonResponse<Slice<ChallengePreviewResponse>> othersChallenge(
            @PathVariable String nickname,
            Pageable pageable,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengeOthersCommand commandDto = ChallengeConverter.othersChallenge(principalDetails.getUserId(), nickname, pageable);
        return CommonResponse.onSuccess(challengeQueryService.othersChallenge(commandDto));
    }

    @Override
    @GetMapping("/api/challenges")
    public CommonResponse<ChallengeCombinedViewResponse> viewChallenge(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Qualifier("trending") Pageable trendingPageable,
            @Qualifier("friends") Pageable friendsPageable
    ) {
        ChallengeViewCommand commandDto = ChallengeConverter.viewChallenge(principalDetails.getUserId(), trendingPageable, friendsPageable);
        return CommonResponse.onSuccess(challengeQueryService.viewChallenge(commandDto));
    }


    @Override
    @GetMapping("/api/challenges/users/{nickname}/details")
    public CommonResponse<Slice<ChallengeDetailResponse>> detailsChallenge(
            @PathVariable String nickname,
            @PageableDefault Pageable pageable
    ) {
        ChallengeDetailCommand commandDto = ChallengeConverter.detailChallenge(nickname, pageable);
        return CommonResponse.onSuccess(challengeQueryService.detailsChallenge(commandDto));
    }
}
