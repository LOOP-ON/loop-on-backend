package com.loopon.user.domain.service;

import com.loopon.user.application.dto.response.FriendResponse;

import java.util.List;

public interface FriendService {
    List<FriendResponse> getMyFriends(Long me);
    void blockFriend(Long me, Long friendId);
    void unblockFriend(Long me, Long friendId);
    void deleteFriend(Long me, Long friendId);

}
