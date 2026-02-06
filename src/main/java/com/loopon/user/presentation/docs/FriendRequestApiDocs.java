package com.loopon.user.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.request.FriendRequestCreateRequest;
import com.loopon.user.application.dto.response.FriendRequestBulkRespondResponse;
import com.loopon.user.application.dto.response.FriendRequestCreateResponse;
import com.loopon.user.application.dto.response.FriendRequestReceivedResponse;
import com.loopon.user.application.dto.response.FriendRequestRespondResponse;
import com.loopon.user.application.dto.response.FriendSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "08. 친구요청(FriendRequest)", description = "친구 요청 응답 및 요청 조회 요청 전송 API")
public interface FriendRequestApiDocs {
    @Operation(summary = "새로운 친구 검색", description = "요청을 보낼 새로운 친구를 검색합니다.(닉네임)")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<PageResponse<FriendSearchResponse>>> findNewFriend(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam String query,
                                                                                     @PageableDefault(sort = "nickname", direction = Sort.Direction.ASC) Pageable pageable);

    @Operation(summary = "친구 요청 목록 조회", description = "회원이 받은 친구 요청 목록을 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<PageResponse<FriendRequestReceivedResponse>>> getFriendRequests(@AuthenticationPrincipal PrincipalDetails principalDetails, Pageable pageable);

    @Operation(summary = "친구 요청 전송", description = "친구 요청을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<FriendRequestCreateResponse>> sendFriendRequest(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody FriendRequestCreateRequest friendRequestRespondRequest);

    @Operation(summary = "친구 1개 요청 수락", description = "친구 요청을 1개 수락합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<FriendRequestRespondResponse>> acceptOneFriendRequest(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long friendRequestId);

    @Operation(summary = "친구 요청 모두 수락", description = "모든 친구 요청을 모두 수락합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<FriendRequestBulkRespondResponse>> acceptAllFriendRequests(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "친구 1개 요청 거절", description = "친구 요청을 1개 거절합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> deleteOneFriendRequest(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long friendRequestId);

    @Operation(summary = "친구 요청 모두 거절", description = "모든 친구 요청을 모두 거절합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<FriendRequestBulkRespondResponse>> deleteAllFriendRequests(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "친구 요청 개수", description = "받은 친구 요청 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    public ResponseEntity<CommonResponse<Long>> getFriendRequestCount(@AuthenticationPrincipal PrincipalDetails principalDetails);

}

