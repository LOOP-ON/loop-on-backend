package com.loopon.user.application.service;

import com.loopon.user.application.dto.response.FriendResponse;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.repository.FriendRepository;
import com.loopon.user.domain.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.loopon.user.domain.FriendStatus.ACCEPTED;
import static com.loopon.user.domain.FriendStatus.BLOCKED;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getMyFriends(Long me) {
        List<Friend> friends = friendRepository.findAcceptedFriendsByUserId(me, ACCEPTED);
        return friends.stream()
                .map(friend -> FriendResponse.from(friend, me))
                .collect(Collectors.toList());
    }

    @Override
    public void blockFriend(Long me, Long friendId) {
        int updated = friendRepository.updateStatusByIdAndParticipantAndStatus(
                friendId,
                me,
                ACCEPTED,
                BLOCKED
        );
        if (updated == 0) {
            throw new IllegalArgumentException("차단할 수 없는 친구 관계입니다.");
        }
    }

    @Override
    public void unblockFriend(Long me, Long friendId) {
        int updated = friendRepository.updateStatusByIdAndParticipantAndStatus(
                friendId,
                me,
                BLOCKED,
                ACCEPTED
        );
        if (updated == 0) {
            throw new IllegalArgumentException("차단 해제할 수 없는 상태입니다.");
        }
    }

    @Override
    public void deleteFriend(Long me, Long friendId) {
        int deleted = friendRepository.deleteByIdAndParticipant(friendId, me);
        if (deleted == 0) {
            throw new IllegalArgumentException("해당 친구 관계가 없거나 삭제 권한이 없습니다.");
        }
    }
}
