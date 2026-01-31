package com.loopon.user.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.request.FriendRequestCreateRequest;
import com.loopon.user.application.dto.request.FriendRequestRespondRequest;
import com.loopon.user.application.dto.response.*;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.service.FriendRequestService;
import com.loopon.user.presentation.docs.FriendRequestApiDocs;
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
public class FriendRequestController implements FriendRequestApiDocs {
    private final FriendRequestService friendRequestService;

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<PageResponse<FriendSearchResponse>>> findNewFriend(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam String query,
                                                                                            @PageableDefault(sort = "nickname", direction = Sort.Direction.ASC) Pageable pageable) {
        Long me = principalDetails.getUserId();
        PageResponse<FriendSearchResponse> friendSearchResponse = friendRequestService.findNewFriend(me, query, pageable);
        return ResponseEntity.ok(CommonResponse.onSuccess(friendSearchResponse));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<FriendRequestReceivedResponse>>> getFriendRequests(@AuthenticationPrincipal PrincipalDetails principalDetails, Pageable pageable) {
        Long me = principalDetails.getUserId();
        PageResponse<FriendRequestReceivedResponse> page = friendRequestService.getFriendRequests(me, pageable);

        return ResponseEntity.ok(CommonResponse.onSuccess(page));
    }

    @GetMapping("/pending-count")
    public ResponseEntity<CommonResponse<Long>> getFriendRequestCount(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long me = principalDetails.getUserId();
        Long count = friendRequestService.countByReceiverIdAndStatus(me, FriendStatus.PENDING);
        return ResponseEntity.ok(CommonResponse.onSuccess(count));
    }

    //내가 원하는 친구에게 요청을 보내는 API
    @PostMapping("/send")
    public ResponseEntity<CommonResponse<FriendRequestCreateResponse>> sendFriendRequest(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody FriendRequestCreateRequest friendRequestRespondRequest) {
        Long me = principalDetails.getUserId();
        FriendRequestCreateResponse res = friendRequestService.sendFriendRequest(me, friendRequestRespondRequest.receiverId());
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    //내가 받은 친구 요청에 대해 수락/거절/차단 API
    @PatchMapping("/respond")
    public ResponseEntity<CommonResponse<FriendRequestRespondResponse>> friendRequestResponse(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody FriendRequestRespondRequest friendRequestRespondRequest) {
        Long me = principalDetails.getUserId();
        FriendRequestRespondResponse res = friendRequestService.respondOneFriendRequest(me, friendRequestRespondRequest);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    @PatchMapping("/respond-all")
    public ResponseEntity<CommonResponse<FriendRequestBulkRespondResponse>> friendRequestResponseAll(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam FriendStatus friendStatus) {
        Long me = principalDetails.getUserId();
        FriendRequestBulkRespondResponse res = friendRequestService.respondAllFriendRequests(me, friendStatus);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

}
