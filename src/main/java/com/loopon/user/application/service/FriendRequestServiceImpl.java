package com.loopon.user.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.exception.BusinessException;
import com.loopon.notification.application.event.FriendRequestCreatedEvent;
import com.loopon.user.application.dto.response.FriendRequestBulkRespondResponse;
import com.loopon.user.application.dto.response.FriendRequestCreateResponse;
import com.loopon.user.application.dto.response.FriendRequestReceivedResponse;
import com.loopon.user.application.dto.response.FriendRequestRespondResponse;
import com.loopon.user.application.dto.response.FriendSearchResponse;
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

import java.util.List;

import static com.loopon.user.domain.FriendStatus.ACCEPTED;
import static com.loopon.user.domain.FriendStatus.PENDING;

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
                friendRequestRepository.findByReceiver_IdAndStatusOrderByUpdatedAtDesc(me, PENDING, pageable);
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
    public FriendRequestRespondResponse acceptOneFriendRequest(Long me, Long requesterId) {
        Friend friendRequest = getPendingRequestOrThrow(requesterId, me);

        friendRequest.accept();
        Friend saved = friendRepository.save(friendRequest);

        return FriendRequestRespondResponse.from(saved);
    }


    @Override
    @Transactional
    public void deleteOneFriendRequest(Long me, Long requesterId) {
        Friend friendRequest = getPendingRequestOrThrow(requesterId, me);

        friendRequestRepository.delete(friendRequest);
    }

    @Override
    @Transactional
    public FriendRequestBulkRespondResponse acceptAllFriendRequests(Long me) {
        List<Friend> requests = friendRequestRepository.findAllByReceiverIdAndStatus(me, PENDING);

        if (requests.isEmpty()) return new FriendRequestBulkRespondResponse(0L);

        for (Friend fr : requests) {
            fr.accept();
        }

        friendRepository.saveAll(requests);
        return new FriendRequestBulkRespondResponse((long) requests.size());
    }


    @Override
    @Transactional
    public FriendRequestBulkRespondResponse deleteAllFriendRequests(Long me) {
        List<Friend> requests = friendRequestRepository.findAllByReceiverIdAndStatus(me, PENDING);

        if (requests.isEmpty()) return new FriendRequestBulkRespondResponse(0L);

        friendRequestRepository.deleteAllInBatch(requests);
        return new FriendRequestBulkRespondResponse((long) requests.size());
    }


    @Override
    public Long countByReceiverIdAndStatus(Long me, FriendStatus friendStatus) {
        return friendRepository.countByReceiver_IdAndStatus(
                me, FriendStatus.PENDING
        );
    }

    private Friend getPendingRequestOrThrow(Long requesterId, Long me) {
        Friend fr = friendRequestRepository
                .findByRequesterIdAndReceiverIdAndStatus(requesterId, me, PENDING)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!fr.getReceiver().getId().equals(me)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_FORBIDDEN);
        }
        return fr;
    }
}
