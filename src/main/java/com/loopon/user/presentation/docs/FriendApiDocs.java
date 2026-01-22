package com.loopon.user.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.user.application.dto.response.FriendResponse;
import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "2. 사용자(User)", description = "회원가입 및 중복 확인 관련 API")
public interface FriendApiDocs {
    @Operation(summary= "친구 목록 조회", description = "친구 목록을 가져옵니다.")
        @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
        @CommonBadRequestResponseDocs
        @CommonInternalServerErrorResponseDocs
        ResponseEntity<CommonResponse<List<FriendResponse>>> getMyFriend(
                @Parameter(description = "확인할 이메일", example = "test@loopon.com", required = true)
                String email
        );


    }
