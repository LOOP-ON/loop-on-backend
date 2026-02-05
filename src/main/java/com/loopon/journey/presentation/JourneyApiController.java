package com.loopon.journey.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.request.JourneyRequest;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.domain.service.JourneyCommandService;
import com.loopon.journey.domain.service.JourneyQueryService;
import com.loopon.journey.presentation.docs.JourneyApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/journeys")
@RequiredArgsConstructor
public class JourneyApiController implements JourneyApiDocs {
private final JourneyCommandService journeyCommandService;
private final JourneyQueryService journeyQueryService;

    @PostMapping("/goals")
    @Override
    public ResponseEntity<CommonResponse<JourneyResponse.PostJourneyGoalDto>> postJourneyGoal(
            @Valid @RequestBody JourneyRequest.AddJourneyDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        Long userId = principalDetails.getUserId();
        JourneyCommand.AddJourneyGoalCommand command = new JourneyCommand.AddJourneyGoalCommand(userId, reqBody.goal(), reqBody.category());

        JourneyResponse.PostJourneyGoalDto journeyId = journeyCommandService.postJourneyGoal(command);

        return ResponseEntity.ok(CommonResponse.onSuccess(journeyId));
    }

    //여정 미루기 API
    @PostMapping("/{journeyId}/routines/{routineId}/postpone")
    @Override
    public ResponseEntity<CommonResponse<JourneyResponse.PostponeRoutineDto>> postponeRoutine(
            @PathVariable Long journeyId,
            @PathVariable Long routineId,
            @Valid @RequestBody JourneyRequest.PostponeRoutineDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();

        JourneyCommand.PostponeRoutineCommand command =
                new JourneyCommand.PostponeRoutineCommand(
                        userId,
                        journeyId,
                        routineId,
                        reqBody.reason()
                );

        JourneyResponse.PostponeRoutineDto response =
                journeyCommandService.postponeRoutine(command);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    //여정 전체 조회
    @GetMapping("/current")
    public ResponseEntity<CommonResponse<JourneyResponse.CurrentJourneyDto>> getCurrentJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();

        JourneyResponse.CurrentJourneyDto response =
                journeyQueryService.getCurrentJourney(userId);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
