package com.loopon.expedition.presentation;

import com.loopon.expedition.application.converter.ExpeditionConverter;
import com.loopon.expedition.application.dto.command.*;
import com.loopon.expedition.application.dto.request.ExpeditionCancelExpelRequest;
import com.loopon.expedition.application.dto.request.ExpeditionExpelRequest;
import com.loopon.expedition.application.dto.request.ExpeditionJoinRequest;
import com.loopon.expedition.application.dto.request.ExpeditionPostRequest;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.expedition.application.service.ExpeditionCommandService;
import com.loopon.expedition.application.service.ExpeditionQueryService;
import com.loopon.expedition.presentation.docs.ExpeditionApiDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
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
    public CommonResponse<ExpeditionGetResponseList> getExpeditions (
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return CommonResponse.onSuccess(
                expeditionQueryService.getExpeditions(principalDetails.getUserId())
        );
    }

    @Override
    @PostMapping("/api/expeditions")
    public CommonResponse<ExpeditionPostResponse> postExpedition (
            @RequestBody ExpeditionPostRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ExpeditionPostCommand commandDto
                = ExpeditionConverter.postExpedition(requestDto, principalDetails.getUserId());

        return CommonResponse.onSuccess(
                expeditionCommandService.postExpedition(commandDto)
        );
    }

    @Override
    @PostMapping("/api/expeditions/join")
    public CommonResponse<ExpeditionJoinResponse> joinExpedition (
            @RequestBody ExpeditionJoinRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionJoinCommand commandDto
                = ExpeditionConverter.joinExpedition(requestDto, principalDetails.getUserId());

        return CommonResponse.onSuccess(
                expeditionCommandService.joinExpedition(commandDto)
        );
    }

    @Override
    @DeleteMapping("/api/expeditions/{expeditionId}/withdraw")
    public CommonResponse<ExpeditionWithdrawResponse> withdrawExpedition (
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionWithdrawCommand commandDto
                = ExpeditionConverter.withdrawExpedition(expeditionId, principalDetails.getUserId());

        return CommonResponse.onSuccess(
                expeditionCommandService.withdrawExpedition(commandDto)
        );
    }

    @Override
    @GetMapping("/api/expeditions/search")
    public CommonResponse<Slice<ExpeditionSearchResponse>> searchExpeditions (
            @RequestParam
            String keyword,

            @RequestParam
            List<Boolean> categories,

            @PageableDefault Pageable pageable,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionSearchCommand commandDto
                = ExpeditionConverter.searchExpedition(keyword, categories, pageable, principalDetails.getUserId());

        return CommonResponse.onSuccess(
                expeditionQueryService.searchExpedition(commandDto)
        );
    }

    @Override
    @DeleteMapping("/api/expeditions/{expeditionId}")
    public CommonResponse<ExpeditionDeleteResponse> deleteExpedition (
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionDeleteCommand commandDto
                = ExpeditionConverter.deleteExpedition(expeditionId, principalDetails.getUserId());

        return CommonResponse.onSuccess(
                expeditionCommandService.deleteExpedition(commandDto)
        );
    }

    @Override
    @GetMapping("/api/expeditions/{expeditionId}/users")
    public CommonResponse<ExpeditionUsersResponse> usersExpedition (
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionUsersCommand commandDto
                = ExpeditionConverter.usersExpedition(expeditionId, principalDetails.getUserId());

        return CommonResponse.onSuccess(
                expeditionQueryService.usersExpedition(commandDto)
        );
    }

    @Override
    @PatchMapping("/api/expeditions/{expeditionId}/expel")
    public CommonResponse<ExpeditionExpelResponse> expelExpedition (
            @PathVariable Long expeditionId,
            @RequestBody ExpeditionExpelRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionExpelCommand commandDto
                = ExpeditionConverter.expelExpedition(expeditionId, requestDto.userId(), principalDetails.getUserId());

        return CommonResponse.onSuccess(
                expeditionCommandService.expelExpedition(commandDto)
        );
    }

    @Override
    @GetMapping("/api/expeditions/{expeditionId}/challenges")
    public CommonResponse<Slice<ExpeditionChallengesResponse>> challengesExpedition (
            @PathVariable Long expeditionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault Pageable pageable
    ) {
        ExpeditionChallengesCommand commandDto
                = ExpeditionConverter.challengesExpedition(expeditionId, principalDetails.getUserId(), pageable);

        return CommonResponse.onSuccess(
                expeditionQueryService.challengesExpedition(commandDto)
        );
    }

    @Override
    @DeleteMapping("/api/expeditions/{expeditionId}/expel")
    public CommonResponse<ExpeditionCancelExpelResponse> cancelExpelExpedition (
            @PathVariable Long expeditionId,
            @RequestBody ExpeditionCancelExpelRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ExpeditionCancelExpelCommand commandDto
                = ExpeditionConverter.cancelExpelExpedition(expeditionId, requestDto, principalDetails.getUserId());

        return CommonResponse.onSuccess(
                expeditionCommandService.cancelExpelExpedition(commandDto)
        );
    }
}
