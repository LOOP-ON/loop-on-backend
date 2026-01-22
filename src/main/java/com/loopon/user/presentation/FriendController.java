package com.loopon.user.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.response.FriendResponse;
import com.loopon.user.domain.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController {
    private final FriendService friendService;

    //내 친구 목록 조회 API
    @GetMapping
    @Operation(summary= "친구 목록 조회", description = "친구 목록을 가져옵니다.")
    public ResponseEntity<CommonResponse<List<FriendResponse>>> getMyFriend(@AuthenticationPrincipal PrincipalDetails principalDetails){
        Long me = principalDetails.getUserId();
        List<FriendResponse> res = friendService.getMyFriends(me);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }
    @DeleteMapping("/{friendId}")
    @Operation(summary = "친구 삭제", description = "친구 목록에서 친구를 삭제합니다.")
    public ResponseEntity<CommonResponse<Void>> deleteFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long friendId
    ) {
        Long me = principalDetails.getUserId();
        friendService.deleteFriend(me, friendId);
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }
}
