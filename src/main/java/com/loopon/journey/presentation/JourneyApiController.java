package com.loopon.journey.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.application.GoalRecommendationService;
import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.request.JourneyRequest;
import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.JourneyContinueResponse;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import com.loopon.journey.application.service.JourneyContinueService;
import com.loopon.journey.application.service.GoalRegenerationService;
import com.loopon.journey.domain.service.JourneyCommandService;
import com.loopon.journey.domain.service.JourneyQueryService;
import com.loopon.journey.presentation.docs.JourneyApiDocs;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journeys")
@RequiredArgsConstructor
public class JourneyApiController implements JourneyApiDocs {
    private final JourneyCommandService journeyCommandService;
    private final JourneyQueryService journeyQueryService;
    private final JourneyContinueService journeyContinueService;

    private final GoalRecommendationService goalRecommendationService;
    private final GoalRegenerationService goalRegenerationService;

    @Override
    @PostMapping("/goals")
    public ResponseEntity<CommonResponse<JourneyResponse.GoalRecommendationResponse>> analyzeGoal(
            @Valid @RequestBody JourneyRequest.GoalRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        JourneyResponse.GoalRecommendationResponse response =
                goalRecommendationService.recommendActions(request.category(), request.goal());

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/order")
    public ResponseEntity<CommonResponse<JourneyResponse.JourneyOrderDto>> getNextJourneyOrder(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();

        JourneyResponse.JourneyOrderDto response =
                journeyQueryService.getNextJourneyOrder(userId);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    //여정에 해당하는 루틴 미루기 API (단일, 전체 다 가능)
    @Override
    @PostMapping("/{journeyId}/routines/postpone/")
    public ResponseEntity<CommonResponse<JourneyResponse.PostponeRoutineDto>> postponeAllRoutine(
            @PathVariable Long journeyId,
            @Valid @RequestBody JourneyRequest.PostponeRoutineDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        Long userId = principalDetails.getUserId();

        JourneyCommand.PostponeRoutineCommand command =
                new JourneyCommand.PostponeRoutineCommand(
                        userId,
                        journeyId,
                        reqBody.progressIds(),
                        reqBody.reason()
                );

        JourneyResponse.PostponeRoutineDto response =
                journeyCommandService.postponeRoutine(command);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    // 여정 전체 조회
    @Override
    @GetMapping("/current")
    public ResponseEntity<CommonResponse<JourneyResponse.CurrentJourneyDto>> getCurrentJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();

        JourneyResponse.CurrentJourneyDto response =
                journeyQueryService.getCurrentJourney(userId);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/regenerate")
    public ResponseEntity<CommonResponse<LoopRegenerationResponse>> regenerateLoop(
            @Valid @RequestBody LoopRegenerationRequest request
    ) {
        LoopRegenerationResponse response = goalRegenerationService.regenerateLoop(request);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @PostMapping("/{journeyId}/continue")
    public ResponseEntity<CommonResponse<JourneyContinueResponse>> continueJourney(
            @PathVariable Long journeyId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        JourneyContinueResponse response = journeyContinueService.continueJourney(journeyId, userId);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/passport")
    public ResponseEntity<CommonResponse<Slice<JourneyResponse.JourneyPreviewDto>>> getJourneyList(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault Pageable pageable
    ) {
        Long userId = principalDetails.getUserId();

        Slice<JourneyResponse.JourneyPreviewDto> response =
                journeyQueryService.getJourneyList(userId, pageable);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/passport/search")
    public ResponseEntity<CommonResponse<Slice<JourneyResponse.JourneyPreviewDto>>> searchJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam String keyword,
            @RequestParam @Size(min = 3, max = 3) List<Boolean> categories,
            @PageableDefault Pageable pageable
    ) {
        Long userId = principalDetails.getUserId();

        Slice<JourneyResponse.JourneyPreviewDto> response =
                journeyQueryService.searchJourney(userId, keyword, categories, pageable);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
