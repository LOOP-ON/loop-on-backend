package com.loopon.challenge.presentation;

import com.loopon.challenge.application.converter.ChallengeConverter;
import com.loopon.challenge.application.dto.command.ChallengePostCommand;
import com.loopon.challenge.application.dto.response.ChallengeGetResponse;
import com.loopon.challenge.application.dto.response.ChallengePostResponse;
import com.loopon.challenge.application.dto.request.ChallengePostRequest;
import com.loopon.challenge.application.service.ChallengeCommandService;
import com.loopon.challenge.application.service.ChallengeQueryService;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class ChallengeApiController {

    private final ChallengeCommandService challengeCommandService;
    private final ChallengeQueryService challengeQueryService;

    @PostMapping("/api/challenges")
    public CommonResponse<ChallengePostResponse> postChallenge(
            @RequestBody ChallengePostRequest requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengePostCommand commandDto =
                ChallengeConverter.postChallenge(requestDto, principalDetails);

        return CommonResponse.onSuccess(
                challengeCommandService.postChallenge(commandDto)
        );
    }

    @GetMapping("/api/challenges/{challengeId}")
    public CommonResponse<ChallengeGetResponse> getChallenge (
            @PathVariable Long challengeId
    ) {
        return CommonResponse.onSuccess(
                challengeQueryService.getChallenge(challengeId)
        );
    }

}
