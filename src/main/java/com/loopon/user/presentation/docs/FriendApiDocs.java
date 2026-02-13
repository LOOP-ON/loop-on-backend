package com.loopon.user.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.domain.dto.SliceResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.response.FriendResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "07. 친구(Friend)", description = "친구 목록 조회 및 친구 삭제 API")
public interface FriendApiDocs {
    @Operation(summary = "친구 목록 조회", description = "친구 목록을 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<SliceResponse<FriendResponse>>> getMyFriend(@AuthenticationPrincipal PrincipalDetails principalDetails, Pageable pageable);

    @Operation(summary = "친구 삭제", description = "친구 목록에서 친구를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> deleteFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long friendId
    );

    @Operation(summary = "친구 차단", description = "친구 목록에서 친구를 차단합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> blockFriend(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long friendId);

    @Operation(summary = "친구 차단 해제", description = "친구 목록에서 친구 차단을 해제합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> unblockFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long friendId);
}
