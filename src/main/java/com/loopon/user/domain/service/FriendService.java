package com.loopon.user.domain.service;

import com.loopon.global.domain.dto.SliceResponse;
import com.loopon.user.application.dto.response.FriendResponse;
import org.springframework.data.domain.Pageable;


public interface FriendService {
    SliceResponse<FriendResponse> getMyFriends(Long me, Pageable pageable);

    void blockFriend(Long me, Long friendId);

    void unblockFriend(Long me, Long friendId);

    void deleteFriend(Long me, Long friendId);
}
