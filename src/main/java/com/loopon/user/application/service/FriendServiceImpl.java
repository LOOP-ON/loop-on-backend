package com.loopon.user.application.service;

import com.loopon.user.application.dto.response.FriendResponse;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.repository.FriendRepository;
import com.loopon.user.domain.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;

    @Override
    public  List<FriendResponse> getMyFriends(Long me){
        List<Friend> friends = friendRepository.findAcceptedFriendsByUserId(me, FriendStatus.ACCEPTED);
        return friends.stream()
                .map(friend -> FriendResponse.from(friend, me))
                .collect(Collectors.toList());
    }
    @Override
    public void deleteFriend(Long me, Long friendId){
        boolean isFriend = friendRepository.existsByIdAndUserId(friendId, me);
        if (!isFriend) {
            throw new IllegalArgumentException("해당 친구 관계가 없거나 삭제 권한이 없습니다.");
        }
        friendRepository.deleteById(friendId);
    }
}
