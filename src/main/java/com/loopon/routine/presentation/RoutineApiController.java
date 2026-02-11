package com.loopon.routine.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.service.RoutineCommandService;
import com.loopon.routine.presentation.docs.RoutineApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineApiController implements RoutineApiDocs {
    private final RoutineCommandService routineCommandService;

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<RoutineResponse.PostRoutinesDto>> postRoutine(
            @Valid @RequestBody RoutineRequest.CreateJourneyWithRoutineDto routineRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();

        RoutineResponse.PostRoutinesDto response =
                routineCommandService.postRoutine(userId, routineRequest);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @PostMapping(value = "/{progressId}/certify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<CommonResponse<RoutineResponse.RoutineCertifyDto>> certifyRoutine(
            @PathVariable Long progressId,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();

        RoutineResponse.RoutineCertifyDto response =
                routineCommandService.certifyRoutine(progressId, userId, image);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));

    }
}
