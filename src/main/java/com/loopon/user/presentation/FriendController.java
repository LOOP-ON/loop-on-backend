package com.loopon.user.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.application.dto.response.FriendResponse;
import com.loopon.user.domain.service.FriendService;
import com.loopon.user.presentation.docs.FriendApiDocs;
import com.loopon.user.presentation.docs.UserApiDocs;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController implements FriendApiDocs {
    private final FriendService friendService;

    //내 친구 목록 조회 API
    @GetMapping
    public ResponseEntity<CommonResponse<List<FriendResponse>>> getMyFriend(@AuthenticationPrincipal PrincipalDetails principalDetails){
        Long me = principalDetails.getUserId();
        List<FriendResponse> res = friendService.getMyFriends(me);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }
    @DeleteMapping("/{friendId}")
    public ResponseEntity<CommonResponse<Void>> deleteFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long friendId
    ) {
        Long me = principalDetails.getUserId();
        friendService.deleteFriend(me, friendId);
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }
}
