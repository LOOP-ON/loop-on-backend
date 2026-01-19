package com.loopon.user.domain.service;

import com.loopon.global.domain.dto.PageResponse;
import com.loopon.user.application.dto.request.FriendRequestRespondRequest;
import com.loopon.user.application.dto.response.*;
import com.loopon.user.domain.FriendStatus;
import org.springframework.data.domain.Pageable;


public interface FriendRequestService {
    //요청을 보낼 친구 검색
    PageResponse<FriendSearchResponse> findNewFriend(Long me, String query, Pageable pageable);
    //받은 친구 요청 목록 조회
    PageResponse<FriendRequestReceivedResponse> getFriendRequests(Long me, Pageable pageable);
    //친구 요청 전송
    FriendRequestCreateResponse sendFriendRequest(Long me, Long receiverId);
    //단일 친구 요청 관리
    FriendRequestRespondResponse respondOneFriendRequest(Long me, FriendRequestRespondRequest friendRequestRespondRequest);
    //일괄 친구 요청 관리
    FriendRequestBulkRespondResponse respondAllFriendRequests(Long me, FriendStatus friendStatus);
}
