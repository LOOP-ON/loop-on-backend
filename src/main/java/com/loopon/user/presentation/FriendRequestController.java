package com.loopon.user.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.request.FriendRequestCreateRequest;
import com.loopon.user.application.dto.request.FriendRequestRespondRequest;
import com.loopon.user.application.dto.response.*;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.service.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend-request")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;
    @GetMapping("/search")
    @Operation(summary= "새로운 친구 검색", description = "요청을 보낼 새로운 친구를 검색합니다.(닉네임)")
    public ResponseEntity<CommonResponse<PageResponse<FriendSearchResponse>>> findNewFriend(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam String query,
                                                                                            @PageableDefault(sort = "nickname", direction = Sort.Direction.ASC)Pageable pageable) {
        Long me = principalDetails.getUserId();
        PageResponse<FriendSearchResponse> friendSearchResponse = friendRequestService.findNewFriend(me, query, pageable);
        return ResponseEntity.ok(CommonResponse.onSuccess(friendSearchResponse));
    }
    @GetMapping
    @Operation(summary= "친구 요청 목록 조회", description = "회원이 받은 친구 요청 목록을 가져옵니다.")
    public ResponseEntity<CommonResponse<PageResponse<FriendRequestReceivedResponse>>>getFriendRequests(@AuthenticationPrincipal PrincipalDetails principalDetails, Pageable pageable) {
        Long me = principalDetails.getUserId();
        PageResponse<FriendRequestReceivedResponse> page = friendRequestService.getFriendRequests(me, pageable);

        return ResponseEntity.ok(CommonResponse.onSuccess(page));
    }
    //내가 원하는 친구에게 요청을 보내는 API
    @PostMapping("/send")
    @Operation(summary= "친구 요청 전송", description = "친구 요청을 전송합니다.")
    public ResponseEntity<CommonResponse<FriendRequestCreateResponse>> sendFriendRequest(@AuthenticationPrincipal PrincipalDetails principalDetails,@RequestBody FriendRequestCreateRequest friendRequestRespondRequest) {
        Long me = principalDetails.getUserId();
        FriendRequestCreateResponse  res = friendRequestService.sendFriendRequest(me, friendRequestRespondRequest.receiverId());
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }
    //내가 받은 친구 요청에 대해 수락/거절/차단 API
    @PatchMapping("/respond")
    @Operation(summary= "친구 요청 수락/거절", description = "친구 요청 1개(수락/거절)를 처리합니다.")
    public ResponseEntity<CommonResponse<FriendRequestRespondResponse>> friendRequestResponse(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody FriendRequestRespondRequest friendRequestRespondRequest) {
        Long me = principalDetails.getUserId();
        FriendRequestRespondResponse res = friendRequestService.respondOneFriendRequest(me,friendRequestRespondRequest);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }
    @PatchMapping("/respond-all")
    @Operation(summary= "친구 요청 모두 수락/거절", description = "모든 친구 요청을 처리합니다.")
    public ResponseEntity<CommonResponse<FriendRequestBulkRespondResponse>> friendRequestResponseAll(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam FriendStatus friendStatus) {
        Long me = principalDetails.getUserId();
      FriendRequestBulkRespondResponse res = friendRequestService.respondAllFriendRequests(me, friendStatus);
        return  ResponseEntity.ok(CommonResponse.onSuccess(res));
    }
}
