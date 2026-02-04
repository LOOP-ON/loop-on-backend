package com.loopon.expedition.presentation.docs;

import com.loopon.expedition.application.dto.request.ExpeditionCancelExpelRequest;
import com.loopon.expedition.application.dto.request.ExpeditionExpelRequest;
import com.loopon.expedition.application.dto.request.ExpeditionJoinRequest;
import com.loopon.expedition.application.dto.request.ExpeditionPostRequest;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "Expedition (탐험대)", description = "탐험대 생성, 조회, 가입 및 멤버 관리 API")
public interface ExpeditionApiDocs {

    @Operation(summary = "내 탐험대 목록 조회", description = "현재 사용자가 참여 중이거나 생성한 탐험대 목록을 조회합니다.")
    CommonResponse<ExpeditionGetResponseList> getExpeditions(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 생성", description = "새로운 탐험대를 생성합니다.")
    CommonResponse<ExpeditionPostResponse> postExpedition(
            @RequestBody(description = "탐험대 생성 정보") ExpeditionPostRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 가입 신청", description = "특정 탐험대에 가입 신청을 보냅니다.")
    CommonResponse<ExpeditionJoinResponse> joinExpedition(
            @RequestBody(description = "가입할 탐험대 ID") ExpeditionJoinRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 탈퇴", description = "참여 중인 탐험대에서 스스로 탈퇴합니다.")
    CommonResponse<ExpeditionWithdrawResponse> withdrawExpedition(
            @Parameter(description = "탈퇴할 탐험대 ID") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 검색", description = "키워드와 카테고리를 기반으로 탐험대를 검색합니다. (Slice 페이징)")
    CommonResponse<Slice<ExpeditionSearchResponse>> searchExpeditions(
            @Parameter(description = "검색 키워드") String keyword,
            @Parameter(description = "카테고리 선택 여부 (3개 고정)") List<Boolean> categories,
            Pageable pageable,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 해체/삭제", description = "방장이 탐험대를 해체하거나 관리자가 삭제합니다.")
    CommonResponse<ExpeditionDeleteResponse> deleteExpedition(
            @Parameter(description = "삭제할 탐험대 ID") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 멤버 명단 조회", description = "특정 탐험대에 소속된 멤버들의 명단과 친구 상태를 조회합니다.")
    CommonResponse<ExpeditionUsersResponse> usersExpedition(
            @Parameter(description = "탐험대 ID") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "멤버 강퇴", description = "방장이 특정 멤버를 탐험대에서 강제 퇴장시킵니다.")
    CommonResponse<ExpeditionExpelResponse> expelExpedition(
            @Parameter(description = "탐험대 ID") Long expeditionId,
            @RequestBody(description = "강퇴할 유저 정보") ExpeditionExpelRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 내 챌린지 조회", description = "탐험대에 소속된 멤버들이 올린 챌린지 인증샷 목록을 조회합니다.")
    CommonResponse<Slice<ExpeditionChallengesResponse>> challengesExpedition(
            @Parameter(description = "탐험대 ID") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    );

    @Operation(summary = "강퇴 취소/철회", description = "강퇴했던 멤버에 대한 강퇴 처리를 취소합니다.")
    CommonResponse<ExpeditionCancelExpelResponse> cancelExpelExpedition(
            @Parameter(description = "탐험대 ID") Long expeditionId,
            @RequestBody(description = "강퇴 취소할 유저 정보") ExpeditionCancelExpelRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );
}