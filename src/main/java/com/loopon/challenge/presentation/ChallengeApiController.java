package com.loopon.challenge.presentation;

import com.loopon.challenge.application.dto.response.ChallengePostResponse;
import com.loopon.challenge.application.dto.command.ChallengePostCommand;
import com.loopon.challenge.application.service.ChallengeCommandService;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ChallengeApiController {

    private final ChallengeCommandService challengeCommandService;

    @PostMapping("/api/challenges")
    public CommonResponse<ChallengePostResponse> postChallenge(
            @RequestBody ChallengePostCommand dto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return CommonResponse.onSuccess(
                challengeCommandService.postChallenge(dto, principalDetails)
        );
    }

}
