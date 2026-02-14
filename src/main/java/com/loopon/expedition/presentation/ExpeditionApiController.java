package com.loopon.expedition.presentation;

import com.loopon.expedition.application.converter.ExpeditionConverter;
import com.loopon.expedition.application.dto.command.*;
import com.loopon.expedition.application.dto.request.*;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.expedition.application.service.ExpeditionCommandService;
import com.loopon.expedition.application.service.ExpeditionQueryService;
import com.loopon.expedition.presentation.docs.ExpeditionApiDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.domain.dto.SliceResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class ExpeditionApiController implements ExpeditionApiDocs {

    private final ExpeditionQueryService expeditionQueryService;
    private final ExpeditionCommandService expeditionCommandService;

    @Override
    @GetMapping("/api/expeditions")
    public ResponseEntity<CommonResponse<ExpeditionGetResponseList>> getExpeditionList(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionQueryService.getExpeditionList(principalDetails.getUserId())));
    }

    @Override
    @PostMapping("/api/expeditions")
    public ResponseEntity<CommonResponse<ExpeditionPostResponse>> postExpedition (
            @RequestBody ExpeditionPostRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ExpeditionPostCommand commandDto
                = ExpeditionConverter.postExpedition(requestDto, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionCommandService.postExpedition(commandDto)));
    }

    @Override
    @PostMapping("/api/expeditions/join")
    public ResponseEntity<CommonResponse<ExpeditionJoinResponse>> joinExpedition (
            @RequestBody ExpeditionJoinRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionJoinCommand commandDto
                = ExpeditionConverter.joinExpedition(requestDto, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionCommandService.joinExpedition(commandDto)));
    }

    @Override
    @DeleteMapping("/api/expeditions/{expeditionId}/withdraw")
    public ResponseEntity<CommonResponse<ExpeditionWithdrawResponse>> withdrawExpedition (
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionWithdrawCommand commandDto
                = ExpeditionConverter.withdrawExpedition(expeditionId, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionCommandService.withdrawExpedition(commandDto)));
    }

    @Override
    @GetMapping("/api/expeditions/search")
    public ResponseEntity<CommonResponse<SliceResponse<ExpeditionSearchResponse>>> searchExpeditions (
            @RequestParam
            String keyword,

            @RequestParam
            List<Boolean> categories,

            @PageableDefault Pageable pageable,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionSearchCommand commandDto
                = ExpeditionConverter.searchExpedition(keyword, categories, pageable, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionQueryService.searchExpedition(commandDto)));
    }

    @Override
    @DeleteMapping("/api/expeditions/{expeditionId}")
    public ResponseEntity<CommonResponse<ExpeditionDeleteResponse>> deleteExpedition (
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionDeleteCommand commandDto
                = ExpeditionConverter.deleteExpedition(expeditionId, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionCommandService.deleteExpedition(commandDto)));
    }

    @Override
    @GetMapping("/api/expeditions/{expeditionId}/users")
    public ResponseEntity<CommonResponse<ExpeditionUsersResponse>> usersExpedition (
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionUsersCommand commandDto
                = ExpeditionConverter.usersExpedition(expeditionId, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionQueryService.usersExpedition(commandDto)));
    }

    @Override
    @PatchMapping("/api/expeditions/{expeditionId}/expel")
    public ResponseEntity<CommonResponse<ExpeditionExpelResponse>> expelExpedition (
            @PathVariable Long expeditionId,
            @RequestBody ExpeditionExpelRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionExpelCommand commandDto
                = ExpeditionConverter.expelExpedition(expeditionId, requestDto.userId(), principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionCommandService.expelExpedition(commandDto)));
    }

    @Override
    @GetMapping("/api/expeditions/{expeditionId}/challenges")
    public ResponseEntity<CommonResponse<SliceResponse<ExpeditionChallengesResponse>>> challengesExpedition (
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault Pageable pageable
    ) {
        ExpeditionChallengesCommand commandDto
                = ExpeditionConverter.challengesExpedition(expeditionId, principalDetails.getUserId(), pageable);

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionQueryService.challengesExpedition(commandDto)));
    }

    @Override
    @DeleteMapping("/api/expeditions/{expeditionId}/expel")
    public ResponseEntity<CommonResponse<ExpeditionCancelExpelResponse>> cancelExpelExpedition (
            @PathVariable Long expeditionId,
            @RequestBody ExpeditionCancelExpelRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionCancelExpelCommand commandDto
                = ExpeditionConverter.cancelExpelExpedition(expeditionId, requestDto, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionCommandService.cancelExpelExpedition(commandDto)));
    }

    @Override
    @GetMapping("/api/expeditions/{expeditionId}")
    public ResponseEntity<CommonResponse<ExpeditionGetResponse>> getExpedition(
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionGetCommand commandDto
                = ExpeditionConverter.getExpedition(expeditionId, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionQueryService.getExpedition(commandDto)));
    }

    @Override
    @PatchMapping("/api/expeditions/{expeditionId}")
    public ResponseEntity<CommonResponse<ExpeditionModifyResponse>> modifyExpedition(
            @PathVariable Long expeditionId,
            @RequestBody ExpeditionModifyRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionModifyCommand commandDto
                = ExpeditionConverter.modifyExpedition(expeditionId, requestDto, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(expeditionCommandService.modifyExpedition(commandDto)));
    }
}
