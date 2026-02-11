package com.loopon.journey.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.application.JourneyAiService;
import com.loopon.journey.application.JourneyContinueService;
import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.request.JourneyRequest;
import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.JourneyContinueResponse;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import com.loopon.journey.domain.service.JourneyCommandService;
import com.loopon.journey.domain.service.JourneyQueryService;
import com.loopon.journey.presentation.docs.JourneyApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/journeys")
@RequiredArgsConstructor
public class JourneyApiController implements JourneyApiDocs {

    private final JourneyCommandService journeyCommandService;
    private final JourneyQueryService journeyQueryService;
    private final JourneyContinueService journeyContinueService;
    private final JourneyAiService journeyAiService;

    @Override
    @GetMapping("/order")
    public ResponseEntity<CommonResponse<JourneyResponse.JourneyOrderDto>> getNextJourneyOrder(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        JourneyResponse.JourneyOrderDto response =
                journeyQueryService.getNextJourneyOrder(principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/goals")
    public ResponseEntity<CommonResponse<JourneyResponse.GoalRecommendationResponse>> analyzeGoal(
            @Valid @RequestBody JourneyRequest.GoalRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        JourneyResponse.GoalRecommendationResponse response =
                journeyAiService.recommendActions(request.category(), request.goal());

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/regenerate")
    public ResponseEntity<CommonResponse<LoopRegenerationResponse>> regenerateLoop(
            @Valid @RequestBody LoopRegenerationRequest request
    ) {
        LoopRegenerationResponse response = journeyAiService.regenerateLoop(request);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/{journeyId}/continue")
    public ResponseEntity<CommonResponse<JourneyContinueResponse>> continueJourney(
            @PathVariable Long journeyId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        JourneyContinueResponse response =
                journeyContinueService.continueJourney(journeyId, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    // [Refactor] URL 끝 슬래시 제거 (/postpone/ -> /postpone)
    @Override
    @PostMapping("/{journeyId}/routines/postpone")
    public ResponseEntity<CommonResponse<JourneyResponse.PostponeRoutineDto>> postponeAllRoutine(
            @PathVariable Long journeyId,
            @Valid @RequestBody JourneyRequest.PostponeRoutineDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        JourneyCommand.PostponeRoutineCommand command =
                new JourneyCommand.PostponeRoutineCommand(
                        principalDetails.getUserId(),
                        journeyId,
                        reqBody.progressIds(),
                        reqBody.reason()
                );

        JourneyResponse.PostponeRoutineDto response =
                journeyCommandService.postponeRoutine(command);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    // ========================================================================
    //  Section 3. 조회 (Query)
    // ========================================================================

    @Override
    @GetMapping("/current")
    public ResponseEntity<CommonResponse<JourneyResponse.CurrentJourneyDto>> getCurrentJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        JourneyResponse.CurrentJourneyDto response =
                journeyQueryService.getCurrentJourney(principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/passport")
    public ResponseEntity<CommonResponse<Slice<JourneyResponse.JourneyPreviewDto>>> getJourneyList(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault Pageable pageable
    ) {
        Slice<JourneyResponse.JourneyPreviewDto> response =
                journeyQueryService.getJourneyList(principalDetails.getUserId(), pageable);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }


    @Override
    @GetMapping("/passport/search")
    public ResponseEntity<CommonResponse<Slice<JourneyResponse.JourneyPreviewDto>>> searchJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @ModelAttribute JourneyRequest.JourneySearchCondition searchCondition,
            @PageableDefault Pageable pageable
    ) {
        Slice<JourneyResponse.JourneyPreviewDto> response = journeyQueryService.searchJourney(
                principalDetails.getUserId(),
                searchCondition.keyword(),
                searchCondition.categories(),
                pageable
        );

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
