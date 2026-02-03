package com.loopon.journey.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.request.JourneyRequest;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.domain.service.JourneyCommandService;
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
}
