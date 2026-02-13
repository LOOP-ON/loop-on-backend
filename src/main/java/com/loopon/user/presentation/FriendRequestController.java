package com.loopon.user.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.request.FriendRequestCreateRequest;
import com.loopon.user.application.dto.response.FriendRequestBulkRespondResponse;
import com.loopon.user.application.dto.response.FriendRequestCreateResponse;
import com.loopon.user.application.dto.response.FriendRequestReceivedResponse;
import com.loopon.user.application.dto.response.FriendRequestRespondResponse;
import com.loopon.user.application.dto.response.FriendSearchResponse;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.service.FriendRequestService;
import com.loopon.user.presentation.docs.FriendRequestApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend-request")
public class FriendRequestController implements FriendRequestApiDocs {
    private final FriendRequestService friendRequestService;

    @Override
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<PageResponse<FriendSearchResponse>>> findNewFriend(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam String query,
                                                                                            @PageableDefault(sort = "nickname", direction = Sort.Direction.ASC) Pageable pageable) {
        Long me = principalDetails.getUserId();
        PageResponse<FriendSearchResponse> friendSearchResponse = friendRequestService.findNewFriend(me, query, pageable);
        return ResponseEntity.ok(CommonResponse.onSuccess(friendSearchResponse));
    }

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<FriendRequestReceivedResponse>>> getFriendRequests(@AuthenticationPrincipal PrincipalDetails principalDetails,@PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Long me = principalDetails.getUserId();
        PageResponse<FriendRequestReceivedResponse> page = friendRequestService.getFriendRequests(me, pageable);

        return ResponseEntity.ok(CommonResponse.onSuccess(page));
    }

    @Override
    @GetMapping("/pending-count")
    public ResponseEntity<CommonResponse<Long>> getFriendRequestCount(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long me = principalDetails.getUserId();
        Long count = friendRequestService.countByReceiverIdAndStatus(me, FriendStatus.PENDING);
        return ResponseEntity.ok(CommonResponse.onSuccess(count));
    }

    //내가 원하는 친구에게 요청을 보내는 API
    @Override
    @PostMapping("/send")
    public ResponseEntity<CommonResponse<FriendRequestCreateResponse>> sendFriendRequest(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody FriendRequestCreateRequest friendRequestRespondRequest) {
        Long me = principalDetails.getUserId();
        FriendRequestCreateResponse res = friendRequestService.sendFriendRequest(me, friendRequestRespondRequest.receiverId());
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    //내가 받은 친구 요청에 대해 수락/거절/차단 API
    @Override
    @PatchMapping("/{requesterId}/accept-one")
    public ResponseEntity<CommonResponse<FriendRequestRespondResponse>> acceptOneFriendRequest(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long requesterId) {
        Long me = principalDetails.getUserId();
        FriendRequestRespondResponse res = friendRequestService.acceptOneFriendRequest(me, requesterId);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    @Override
    @PatchMapping("/accept-all")
    public ResponseEntity<CommonResponse<FriendRequestBulkRespondResponse>> acceptAllFriendRequests(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long me = principalDetails.getUserId();
        FriendRequestBulkRespondResponse res = friendRequestService.acceptAllFriendRequests(me);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    @Override
    @DeleteMapping("/{requesterId}/delete-one")
    public ResponseEntity<CommonResponse<Void>> deleteOneFriendRequest(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long requesterId) {
        Long me = principalDetails.getUserId();
        friendRequestService.deleteOneFriendRequest(me, requesterId);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    @Override
    @DeleteMapping("/delete-all")
    public ResponseEntity<CommonResponse<FriendRequestBulkRespondResponse>> deleteAllFriendRequests(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long me = principalDetails.getUserId();
        FriendRequestBulkRespondResponse res = friendRequestService.deleteAllFriendRequests(me);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }
}
