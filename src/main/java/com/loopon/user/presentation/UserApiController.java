package com.loopon.user.presentation;

import com.loopon.global.s3.S3Service;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.user.application.UserCommandService;
import com.loopon.user.application.UserQueryService;
import com.loopon.user.application.dto.request.UserSignUpRequest;
import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import com.loopon.user.application.validator.ProfileImageValidator;
import com.loopon.user.presentation.docs.UserApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController implements UserApiDocs {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final S3Service s3Service;

    private final ProfileImageValidator profileImageValidator;

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/upload-profile-image")
    public ResponseEntity<CommonResponse<String>> uploadProfileImage(@RequestPart("file") MultipartFile file) {
        profileImageValidator.validate(file);
        String imageUrl = s3Service.uploadFile(file);
        return ResponseEntity.ok(CommonResponse.onSuccess(imageUrl));
    }
}
