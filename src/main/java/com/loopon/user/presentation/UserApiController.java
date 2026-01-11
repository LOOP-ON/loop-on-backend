package com.loopon.user.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.user.application.UserCommandService;
import com.loopon.user.application.UserQueryService;
import com.loopon.user.application.dto.request.UserSignUpRequest;
import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/check-email")
    public CommonResponse<UserDuplicateCheckResponse> checkEmailExists(@RequestParam String email) {
        UserDuplicateCheckResponse response = userQueryService.isEmailAvailable(email);
        return CommonResponse.onSuccess(response);
    }

    @PostMapping("/check-nickname")
    public CommonResponse<UserDuplicateCheckResponse> checkNicknameExists(@RequestParam String nickname) {
        UserDuplicateCheckResponse response = userQueryService.isNicknameAvailable(nickname);
        return CommonResponse.onSuccess(response);
    }

    @PostMapping
    public CommonResponse<Long> signUp(@Valid @RequestBody UserSignUpRequest request) {
        Long userId = userCommandService.signUp(request.toCommand());
        return CommonResponse.onSuccess(userId);
    }
}
