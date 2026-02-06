package com.loopon.challenge.presentation.docs;

import com.loopon.challenge.application.dto.request.ChallengeCommentRequest;
import com.loopon.challenge.application.dto.request.ChallengeLikeCommentRequest;
import com.loopon.challenge.application.dto.request.ChallengeLikeRequest;
import com.loopon.challenge.application.dto.request.ChallengeModifyRequest;
import com.loopon.challenge.application.dto.request.ChallengePostRequest;
import com.loopon.challenge.application.dto.response.*;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "10. 챌린지(Challenge)", description = "챌린지, 댓글 및 좋아요 관련 API")
public interface ChallengeApiDocs {

    @Operation(summary = "챌린지 업로드")
    CommonResponse<ChallengePostResponse> postChallenge(
            @RequestPart("requestDto")
            @Valid
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            ChallengePostRequest requestDto,

            @RequestPart("imageFiles")
            @Size(min = 1, max = 10, message = "사진은 최대 10개까지 가능합니다.")
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            List<MultipartFile> imageFiles,

            PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 업로드 상세 조회")
    CommonResponse<ChallengeGetResponse> getChallenge(
            @PathVariable("challengeId") Long challengeId
    );

    @Operation(summary = "챌린지 좋아요/취소")
    CommonResponse<ChallengeLikeResponse> likeChallenge(
            @PathVariable("challengeId") Long challengeId,
            ChallengeLikeRequest requestDto,
            PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 수정")
    CommonResponse<ChallengeModifyResponse> patchChallenge(
            @RequestPart("requestDto")
            @Valid
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            ChallengeModifyRequest requestDto,

            @RequestPart(value = "imageFiles", required = false)
            @Size(max = 10, message = "사진은 최대 10개까지 가능합니다.")
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            List<MultipartFile> imageFiles,

            @PathVariable("challengeId")
            Long challengeId
    );

    @Operation(summary = "챌린지 댓글 작성")
    CommonResponse<ChallengeCommentResponse> commentChallenge(
            @PathVariable("challengeId") Long challengeId,
            @Valid ChallengeCommentRequest requestDto,
            PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 댓글 목록 조회")
    CommonResponse<Slice<ChallengeGetCommentResponse>> getCommentChallenge(
            @PathVariable("challengeId") Long challengeId,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "댓글 좋아요/취소")
    CommonResponse<ChallengeLikeCommentResponse> likeCommentChallenge(
            @PathVariable("commentId") Long commentId,
            @Valid ChallengeLikeCommentRequest requestDto,
            PrincipalDetails principalDetails
    );

    @Operation(summary = "댓글 삭제")
    CommonResponse<Void> deleteCommentChallenge(
            @PathVariable("commentId") Long commentId,
            @PathVariable("challengeId") Long challengeId,
            PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 삭제")
    CommonResponse<Void> deleteChallenge(
            @PathVariable("challengeId") Long challengeId,
            PrincipalDetails principalDetails
    );

    @Operation(summary = "내 챌린지 모아보기")
    CommonResponse<Slice<ChallengePreviewResponse>> myChallenge(
            PrincipalDetails principalDetails,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "타인의 챌린지 모아보기")
    CommonResponse<Slice<ChallengePreviewResponse>> othersChallenge(
            @PathVariable("nickname") String nickname,
            @PageableDefault Pageable pageable,
            PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 홈 피드 조회. 트렌딩 챌린지와 친구 챌린지의 비율은 기본적으로 1:3을 유지합니다.")
    CommonResponse<ChallengeCombinedViewResponse> viewChallenge(
            PrincipalDetails principalDetails,
            @PageableDefault @Qualifier("trending") Pageable trendingPageable,
            @PageableDefault @Qualifier("friends") Pageable friendsPageable
    );

    @Operation(summary = "개인 챌린지 상세보기.")
    CommonResponse<Slice<ChallengeDetailResponse>> detailsChallenge(
            @PathVariable("nickname") String nickname,
            @PageableDefault Pageable pageable
    );
}
