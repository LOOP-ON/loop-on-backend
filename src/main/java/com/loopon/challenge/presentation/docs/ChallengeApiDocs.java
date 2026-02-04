package com.loopon.challenge.presentation.docs;

import com.loopon.challenge.application.dto.request.ChallengeCommentRequest;
import com.loopon.challenge.application.dto.request.ChallengeLikeRequest;
import com.loopon.challenge.application.dto.request.ChallengeModifyRequest;
import com.loopon.challenge.application.dto.request.ChallengePostRequest;
import com.loopon.challenge.application.dto.response.*;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "챌린지(Challenge)", description = "챌린지, 댓글 및 좋아요 관련 API")
public interface ChallengeApiDocs {

    @Operation(summary = "챌린지 업로드", description = "이미지와 내용을 포함한 새로운 챌린지를 게시합니다. (Multipart/form-data)")
    CommonResponse<ChallengePostResponse> postChallenge(
            @RequestBody(description = "챌린지 게시글 정보 (JSON)") ChallengePostRequest requestDto,
            @Parameter(description = "인증 이미지 파일 목록 (최대 10개)") List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 업로드 상세 조회", description = "수정을 위해 업로드된 특정 챌린지의 상세 정보를 조회합니다.")
    CommonResponse<ChallengeGetResponse> getChallenge(
            @Parameter(description = "챌린지 ID") Long challengeId
    );

    @Operation(summary = "챌린지 좋아요/취소", description = "챌린지 게시글에 좋아요를 누르거나 취소합니다.")
    CommonResponse<ChallengeLikeResponse> likeChallenge(
            @Parameter(description = "챌린지 ID") Long challengeId,
            @RequestBody(description = "좋아요 상태 정보") ChallengeLikeRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 수정", description = "기존 챌린지의 내용이나 이미지를 수정합니다.")
    CommonResponse<ChallengeModifyResponse> patchChallenge(
            @RequestBody(description = "수정할 내용 (JSON)") ChallengeModifyRequest requestDto,
            @Parameter(description = "새로 업로드할 이미지 파일 목록") List<MultipartFile> imageFiles,
            @Parameter(description = "챌린지 ID") Long challengeId
    );

    @Operation(summary = "챌린지 댓글 작성", description = "챌린지 게시글에 댓글을 작성합니다.")
    CommonResponse<ChallengeCommentResponse> commentChallenge(
            @Parameter(description = "챌린지 ID") Long challengeId,
            @RequestBody(description = "댓글 내용") ChallengeCommentRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 댓글 목록 조회", description = "특정 챌린지에 달린 댓글들을 페이징하여 조회합니다.")
    CommonResponse<Slice<ChallengeGetCommentResponse>> getCommentChallenge(
            @Parameter(description = "챌린지 ID") Long challengeId,
            Pageable pageable
    );

    @Operation(summary = "댓글 좋아요/취소", description = "댓글에 좋아요를 누르거나 취소합니다.")
    CommonResponse<ChallengeLikeCommentResponse> likeCommentChallenge(
            @Parameter(description = "댓글 ID") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    CommonResponse<Void> deleteCommentChallenge(
            @Parameter(description = "댓글 ID") Long commentId,
            @Parameter(description = "챌린지 ID") Long challengeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 삭제", description = "본인이 작성한 챌린지를 삭제합니다.")
    CommonResponse<Void> deleteChallenge(
            @Parameter(description = "챌린지 ID") Long challengeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "내 챌린지 모아보기", description = "내가 작성한 챌린지 목록의 미리보기를 조회합니다.")
    CommonResponse<Slice<ChallengePreviewResponse>> myChallenge(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    );

    @Operation(summary = "타인의 챌린지 모아보기", description = "특정 닉네임을 가진 사용자의 챌린지 목록의 미리보기를 조회합니다.")
    CommonResponse<Slice<ChallengePreviewResponse>> othersChallenge(
            @Parameter(description = "상대방 닉네임") String nickname,
            Pageable pageable,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "챌린지 홈 피드 조회", description = "트렌딩(인기) 챌린지와 친구들의 챌린지를 동시에 조회합니다.")
    CommonResponse<ChallengeCombinedViewResponse> viewChallenge(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Parameter(hidden = true) Pageable trendingPageable,
            @Parameter(hidden = true) Pageable friendsPageable
    );
}