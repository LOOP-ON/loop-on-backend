package com.loopon.user.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
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
            throw new BusinessException(ErrorCode.FRIEND_BLOCK_NOT_ALLOWED);
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
            throw new BusinessException(ErrorCode.FRIEND_UNBLOCK_NOT_ALLOWED);
        }
    }

    @Override
    public void deleteFriend(Long me, Long friendId) {
        int deleted = friendRepository.deleteByIdAndParticipant(friendId, me);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.FRIEND_DELETE_NOT_ALLOWED);
        }
    }
}
