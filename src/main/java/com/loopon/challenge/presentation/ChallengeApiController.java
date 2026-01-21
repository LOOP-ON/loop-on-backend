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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Validated
public class ChallengeApiController {

    private final ChallengeCommandService challengeCommandService;
    private final ChallengeQueryService challengeQueryService;

    @PostMapping(value = "/api/challenges", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<ChallengePostResponse> postChallenge(
            @RequestPart("requestDto") @Valid ChallengePostRequest requestDto,

            @RequestPart("imageFiles")
            @Size(min = 1, max = 10, message = "사진은 최대 10개까지 가능합니다.")
            List<MultipartFile> imageFiles,

            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ChallengePostCommand commandDto =
                ChallengeConverter.postChallenge(requestDto, imageFiles, principalDetails);

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
