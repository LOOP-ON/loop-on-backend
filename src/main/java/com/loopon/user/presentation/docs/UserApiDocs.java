package com.loopon.user.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.user.application.dto.request.UserSignUpRequest;
import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "02. 사용자(User)", description = "회원가입 및 중복 확인 관련 API")
public interface UserApiDocs {

    @Operation(summary = "이메일 중복 확인", description = "회원가입 시 이메일이 이미 존재하는지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<UserDuplicateCheckResponse>> checkEmailExists(
            @Parameter(description = "확인할 이메일", example = "test@loopon.com", required = true)
            String email
    );

    @Operation(summary = "닉네임 중복 확인", description = "회원가입 시 닉네임이 이미 존재하는지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<UserDuplicateCheckResponse>> checkNicknameExists(
            @Parameter(description = "확인할 닉네임", example = "loopon_master", required = true)
            String nickname
    );

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공 (생성된 User ID 반환)", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Long>> signUp(UserSignUpRequest request);

    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드하고, 이미지 URL을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 성공 (이미지 URL 반환)", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<String>> uploadProfileImage(
            @Parameter(description = "업로드할 이미지 파일", required = true) MultipartFile file
    );
}
