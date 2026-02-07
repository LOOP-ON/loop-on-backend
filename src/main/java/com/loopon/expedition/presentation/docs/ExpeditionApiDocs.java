package com.loopon.expedition.presentation.docs;

import com.loopon.expedition.application.dto.request.*;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "9. 탐험대(Expedition)", description = "탐험대 생성, 참여 및 관리 API")
public interface ExpeditionApiDocs {

    @Operation(summary = "내 탐험대 목록 조회", description = "내가 참여 중인 모든 탐험대 리스트를 조회합니다.")
    CommonResponse<ExpeditionGetResponseList> getExpeditionList(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 생성", description = "새로운 탐험대를 개설합니다.")
    CommonResponse<ExpeditionPostResponse> postExpedition(
            @RequestBody ExpeditionPostRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 참여", description = "초대 코드 등을 통해 탐험대에 참여합니다.")
    CommonResponse<ExpeditionJoinResponse> joinExpedition(
            @RequestBody ExpeditionJoinRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 탈퇴", description = "참여 중인 탐험대에서 스스로 나갑니다.")
    CommonResponse<ExpeditionWithdrawResponse> withdrawExpedition(
            @Parameter(description = "탐험대 ID") @PathVariable("expeditionId") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 검색", description = "키워드와 카테고리를 필터로 탐험대를 검색합니다.")
    CommonResponse<Slice<ExpeditionSearchResponse>> searchExpeditions(
            @NotBlank
            @RequestParam("keyword")
            String keyword,

            @Size(min = 3, max = 3)
            @RequestParam("categories")
            List<@NotNull Boolean> categories,

            Pageable pageable,

            @AuthenticationPrincipal
            PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 삭제 (방장 전용)", description = "방장이 탐험대 자체를 삭제합니다.")
    CommonResponse<ExpeditionDeleteResponse> deleteExpedition(
            @Parameter(description = "탐험대 ID") @PathVariable("expeditionId") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 멤버 조회", description = "특정 탐험대에 속한 멤버 목록을 조회합니다.")
    CommonResponse<ExpeditionUsersResponse> usersExpedition(
            @Parameter(description = "탐험대 ID") @PathVariable("expeditionId") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "멤버 추방 (방장 전용)", description = "방장이 특정 멤버를 탐험대에서 내보냅니다.")
    CommonResponse<ExpeditionExpelResponse> expelExpedition(
            @Parameter(description = "탐험대 ID") @PathVariable("expeditionId") Long expeditionId,
            @RequestBody ExpeditionExpelRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 내 챌린지 조회", description = "탐험대원들이 올린 챌린지 게시글들을 조회합니다.")
    CommonResponse<Slice<ExpeditionChallengesResponse>> challengesExpedition(
            @Parameter(description = "탐험대 ID") @PathVariable("expeditionId") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    );

    @Operation(summary = "추방 취소 (방장 전용)", description = "추방했던 멤버의 추방 상태를 해제합니다.")
    CommonResponse<ExpeditionCancelExpelResponse> cancelExpelExpedition(
            @Parameter(description = "탐험대 ID") @PathVariable("expeditionId") Long expeditionId,
            @RequestBody ExpeditionCancelExpelRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 설정 조회", description = "탐험대 설정을 조회합니다.")
    CommonResponse<ExpeditionGetResponse> getExpedition(
            @Parameter(description = "탐험대 ID") @PathVariable("expeditionId") Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "탐험대 설정 수정", description = "탐험대 설정을 수정합니다.")
    CommonResponse<ExpeditionModifyResponse> modifyExpedition(
            @Parameter(description = "탐험대 ID") @PathVariable("expeditionId") Long expeditionId,
            @RequestBody ExpeditionModifyRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );
}