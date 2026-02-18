package com.loopon.user.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.s3.S3Service;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.UserCommandService;
import com.loopon.user.application.UserQueryService;
import com.loopon.user.application.dto.request.ChangePasswordRequest;
import com.loopon.user.application.dto.request.UpdateProfileRequest;
import com.loopon.user.application.dto.request.UserSignUpRequest;
import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import com.loopon.user.application.dto.response.UserOthersProfileResponse;
import com.loopon.user.application.dto.response.UserProfileResponse;
import com.loopon.user.application.validator.ImageValidator;
import com.loopon.user.presentation.docs.UserApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController implements UserApiDocs {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final S3Service s3Service;

    private final ImageValidator imageValidator;

    @Override
    @PostMapping("/check-email")
    public ResponseEntity<CommonResponse<UserDuplicateCheckResponse>> checkEmailExists(@RequestParam String email) {
        UserDuplicateCheckResponse response = userQueryService.isEmailAvailable(email);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PostMapping("/check-nickname")
    public ResponseEntity<CommonResponse<UserDuplicateCheckResponse>> checkNicknameExists(@RequestParam String nickname) {
        UserDuplicateCheckResponse response = userQueryService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<Long>> signUp(@Valid @RequestBody UserSignUpRequest request) {
        Long userId = userCommandService.signUp(request.toCommand());
        return ResponseEntity.ok(CommonResponse.onSuccess(userId));
    }

    @Override
    @PostMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<String>> uploadProfileImage(@RequestPart("file") MultipartFile file) {
        imageValidator.validate(file);
        String imageUrl = s3Service.uploadFile(file);
        return ResponseEntity.ok(CommonResponse.onSuccess(imageUrl));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserProfileResponse>> getUserProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        UserProfileResponse response = userQueryService.getUserProfile(principalDetails.getUserId(), pageable);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/{nickname}")
    public ResponseEntity<CommonResponse<UserOthersProfileResponse>> getOthersProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable String nickname,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        UserOthersProfileResponse response = userQueryService.getOthersProfile(principalDetails.getUserId(), nickname, pageable);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PatchMapping("/profile")
    public ResponseEntity<CommonResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserProfileResponse response = userCommandService.updateProfile(principalDetails.getUserId(), request.toCommand());
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PatchMapping("/password")
    public ResponseEntity<CommonResponse<Void>> changePassword(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userCommandService.changePassword(principalDetails.getUserId(), request);
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }
}
