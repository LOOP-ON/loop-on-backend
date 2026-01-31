package com.loopon.user.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.exception.BusinessException;
import com.loopon.notification.application.event.FriendRequestCreatedEvent;
import com.loopon.user.application.dto.request.FriendRequestRespondRequest;
import com.loopon.user.application.dto.response.*;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.FriendRepository;
import com.loopon.user.domain.repository.FriendRequestRepository;
import com.loopon.user.domain.repository.UserRepository;
import com.loopon.user.domain.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.loopon.user.domain.FriendStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestServiceImpl implements FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public PageResponse<FriendSearchResponse> findNewFriend(Long me, String query, Pageable pageable) {
        if (query == null || query.trim().length() < 2) {
            return PageResponse.of(Page.empty(pageable));
        }
        Page<User> newFriend = userRepository.searchByNickname(me, query, pageable);
        return PageResponse.of(newFriend.map(FriendSearchResponse::from));
    }

    @Override
    public PageResponse<FriendRequestReceivedResponse> getFriendRequests(Long me, Pageable pageable) {
        Page<Friend> friendRequests =
                friendRequestRepository.findByReceiverIdAndStatusOrderByUpdatedAtDesc(me, PENDING, pageable);
        return PageResponse.of(friendRequests.map(FriendRequestReceivedResponse::from));
    }

    @Override
    public FriendRequestCreateResponse sendFriendRequest(Long me, Long receiverId) {
        //자기 자신 요청 방지
        if (me.equals(receiverId)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_SELF);
        }
        // 양방향 체크를 한 번에 (Repository 메서드 추가)
        if (friendRequestRepository.existsFriendship(me, receiverId, ACCEPTED)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_ALREADY_FRIEND);
        }

        if (friendRequestRepository.existsFriendship(me, receiverId, PENDING)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_ALREADY_PENDING);
        }
        //새로운 친구 요청 생성(로직 추가 필요)
    User requester = userRepository.findById(me)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    Friend friendRequest = Friend.request(requester, receiver);
    //친구 요청 저장
    Friend saved = friendRequestRepository.save(friendRequest);
    //이벤트 발생
        applicationEventPublisher.publishEvent(
                new FriendRequestCreatedEvent(saved.getId(), me, receiverId)
        );
        return FriendRequestCreateResponse.from(saved);
    }

    @Override
    @Transactional
    public FriendRequestRespondResponse respondOneFriendRequest(Long me, FriendRequestRespondRequest friendRequestRespondRequest) {
        Friend friendRequest = friendRequestRepository
                .findByRequesterIdAndReceiverIdAndStatus(friendRequestRespondRequest.requesterId(), me, PENDING)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendRequest.getReceiver().getId().equals(me)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_FORBIDDEN);
        }
        FriendStatus requestedStatus = friendRequestRespondRequest.friendStatus();
        if (requestedStatus == ACCEPTED) {
            friendRequest.accept();
        } else if (requestedStatus == REJECTED) {
            friendRequest.reject();
        } else {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_INVALID_STATUS);
        }
        Friend saved = friendRepository.save(friendRequest);
        return FriendRequestRespondResponse.from(saved);
    }

    @Override
    @Transactional
    public FriendRequestBulkRespondResponse respondAllFriendRequests(Long me, FriendStatus friendStatus) {
        List<Long> requesterIdList = friendRequestRepository.getAllRequesterIdByStatus(me, PENDING);
        //요청이 비어있을 때
        if (requesterIdList == null || requesterIdList.isEmpty()) {
            return new FriendRequestBulkRespondResponse(0L);
        }
        //모두 수락/거절이 아닐때(형식 에러)
        if (friendStatus != ACCEPTED && friendStatus != REJECTED) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_INVALID_STATUS);
        }
        List<Friend> toUpdate = new ArrayList<>();
        for (Long requesterId : requesterIdList) {
            Friend friendRequest = friendRequestRepository
                    .findByRequesterIdAndReceiverIdAndStatus(requesterId, me, PENDING)
                    .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
            if (!friendRequest.getReceiver().getId().equals(me)) {
                throw new BusinessException(ErrorCode.FRIEND_REQUEST_FORBIDDEN);
            }
            if (friendStatus == ACCEPTED) {
                friendRequest.accept();
            } else {
                friendRequest.reject();
            }
            toUpdate.add(friendRequest);
        }
        //일괄 저장
        List<Friend> saved = friendRepository.saveAll(toUpdate);

        // Bulk 응답은 '처리된 요청 개수'만 반환
        Long processedCount = (long) saved.size();
        return new FriendRequestBulkRespondResponse(processedCount);
    }

    @Override
    public Long countByReceiverIdAndStatus(Long me, FriendStatus friendStatus) {
        return friendRepository.countByReceiver_IdAndStatus(
                me, FriendStatus.PENDING
        );
    }
}
