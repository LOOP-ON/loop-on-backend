package com.loopon.journey.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.request.JourneyRequest;
import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import com.loopon.journey.application.service.LoopRegenerationService;
import com.loopon.journey.domain.service.JourneyCommandService;
import com.loopon.journey.domain.service.JourneyQueryService;
import com.loopon.journey.presentation.docs.JourneyApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/journeys")
@RequiredArgsConstructor
public class JourneyApiController implements JourneyApiDocs {
    private final JourneyCommandService journeyCommandService;
    private final JourneyQueryService journeyQueryService;
    private final LoopRegenerationService loopRegenerationService;

    @Override
    @PostMapping("/goals")
    public ResponseEntity<CommonResponse<JourneyResponse.PostJourneyGoalDto>> postJourneyGoal(
            @Valid @RequestBody JourneyRequest.AddJourneyDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        JourneyCommand.AddJourneyGoalCommand command = new JourneyCommand.AddJourneyGoalCommand(userId, reqBody.goal(), reqBody.category());

        JourneyResponse.PostJourneyGoalDto journeyId = journeyCommandService.postJourneyGoal(command);

        return ResponseEntity.ok(CommonResponse.onSuccess(journeyId));
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
        LoopRegenerationResponse response = loopRegenerationService.regenerateLoop(request);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
