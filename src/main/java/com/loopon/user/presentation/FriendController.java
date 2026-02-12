package com.loopon.user.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.domain.dto.SliceResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.response.FriendResponse;
import com.loopon.user.domain.service.FriendService;
import com.loopon.user.presentation.docs.FriendApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController implements FriendApiDocs {
    private final FriendService friendService;

    //내 친구 목록 조회 API
    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<SliceResponse<FriendResponse>>> getMyFriend(@AuthenticationPrincipal PrincipalDetails principalDetails, @PageableDefault(sort = "nickname", direction = Sort.Direction.ASC) Pageable pageable) {
        Long me = principalDetails.getUserId();
        SliceResponse<FriendResponse> res = friendService.getMyFriends(me, pageable);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    @Override
    @DeleteMapping("/{friendId}")
    public ResponseEntity<CommonResponse<Void>> deleteFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long friendId
    ) {
        Long me = principalDetails.getUserId();
        friendService.deleteFriend(me, friendId);
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }

    @Override
    @PutMapping("/{friendId}/block")
    public ResponseEntity<CommonResponse<Void>> blockFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long friendId) {
        Long me = principalDetails.getUserId();
        friendService.blockFriend(me, friendId);
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }

    @Override
    @DeleteMapping("/{friendId}/block")
    public ResponseEntity<CommonResponse<Void>> unblockFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long friendId) {
        Long me = principalDetails.getUserId();
        friendService.unblockFriend(me, friendId);
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }
}
