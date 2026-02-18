package com.loopon.user.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.request.ChangePasswordRequest;
import com.loopon.user.application.dto.request.UpdateProfileRequest;
import com.loopon.user.application.dto.request.UserSignUpRequest;
import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import com.loopon.user.application.dto.response.UserOthersProfileResponse;
import com.loopon.user.application.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
    ResponseEntity<CommonResponse<Long>> signUp(
            @RequestBody(description = "회원가입 요청 정보", required = true)
            @Valid UserSignUpRequest request
    );

    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드하고, 이미지 URL을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 성공 (이미지 URL 반환)", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<String>> uploadProfileImage(
            @Parameter(description = "업로드할 이미지 파일", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            MultipartFile file
    );

    @Operation(summary = "내 프로필 조회", description = "인증된 사용자의 프로필 정보(닉네임, 이미지, 참여 이력 등)를 조회합니다.")
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "한 페이지 크기", in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "sort", description = "정렬 기준 (예: createdAt,desc)", in = ParameterIn.QUERY, example = "createdAt,desc")
    })
    @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<UserProfileResponse>> getUserProfile(
            @Parameter(hidden = true) PrincipalDetails principalDetails,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "타인 프로필 조회", description = "공개/친구 사용자의 프로필 정보(닉네임, 이미지, 한줄 소개 등)를 조회합니다.")
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "한 페이지 크기", in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "sort", description = "정렬 기준 (예: createdAt,desc)", in = ParameterIn.QUERY, example = "createdAt,desc")
    })
    @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<UserOthersProfileResponse>> getOthersProfile(
            @Parameter(hidden = true) PrincipalDetails principalDetails,
            @Parameter String nickname,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "프로필 수정", description = "닉네임, Bio, 상태메시지, 프로필 이미지를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공 (변경된 프로필 정보 반환)", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<UserProfileResponse>> updateProfile(
            @Parameter(hidden = true) PrincipalDetails principalDetails,
            @RequestBody(description = "프로필 수정 정보", required = true)
            @Valid UpdateProfileRequest request
    );

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> changePassword(
            @Parameter(hidden = true) PrincipalDetails principalDetails,
            @RequestBody(description = "비밀번호 변경 정보", required = true)
            @Valid ChangePasswordRequest request
    );
}
