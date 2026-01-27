package com.loopon.friendRequest.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.application.dto.request.FriendRequestRespondRequest;
import com.loopon.user.application.dto.response.*;
import com.loopon.user.application.service.FriendRequestServiceImpl;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.FriendRepository;
import com.loopon.user.domain.repository.FriendRequestRepository;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.loopon.user.domain.FriendStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("FriendRequestService 테스트")
class FriendRequestServiceTest {

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendRequestServiceImpl friendRequestService;

    private User user1;
    private User user2;
    private Friend friendRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user1 = User.createLocalUser(
                "test1@loopon.com",
                "loopon1",
                "password123!",
                null
        );
        ReflectionTestUtils.setField(user1, "id", 1L);

        user2 = User.createLocalUser(
                "test2@loopon.com",
                "loopon2",
                "password123!",
                null
        );
        ReflectionTestUtils.setField(user2, "id", 2L);

        friendRequest = Friend.builder()
                .id(1L)
                .requester(user1)
                .receiver(user2)
                .status(PENDING)
                .build();

        pageable = PageRequest.of(0, 10, Sort.by("updatedAt").descending());
    }

    @Nested
    @DisplayName("새로운 친구 찾기")
    class FindNewFriend {

        @Test
        @DisplayName("검색어가 2자 미만이면 빈 페이지를 반환한다")
        void findNewFriend_ShortQuery_ReturnsEmptyPage() {
            // given
            String shortQuery = "a";

            // when
            PageResponse<FriendSearchResponse> result =
                    friendRequestService.findNewFriend(1L, shortQuery, pageable);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
            verify(userRepository, never()).searchByNickname(anyLong(), anyString(), any());
        }

        @Test
        @DisplayName("검색어가 null이면 빈 페이지를 반환한다")
        void findNewFriend_NullQuery_ReturnsEmptyPage() {
            // when
            PageResponse<FriendSearchResponse> result =
                    friendRequestService.findNewFriend(1L, null, pageable);

            // then
            assertThat(result.content()).isEmpty();
            verify(userRepository, never()).searchByNickname(anyLong(), anyString(), any());
        }

        @Test
        @DisplayName("유효한 검색어로 사용자를 검색한다")
        void findNewFriend_ValidQuery_ReturnsUsers() {
            // given
            String query = "user";
            Page<User> userPage = new PageImpl<>(List.of(user2));
            given(userRepository.searchByNickname(1L, query, pageable)).willReturn(userPage);

            // when
            PageResponse<FriendSearchResponse> result =
                    friendRequestService.findNewFriend(1L, query, pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1);
            verify(userRepository).searchByNickname(1L, query, pageable);
        }
    }

    @Nested
    @DisplayName("친구 요청 목록 조회")
    class GetFriendRequests {

        @Test
        @DisplayName("받은 친구 요청 목록을 조회한다")
        void getFriendRequests_Success() {
            // given
            Page<Friend> friendPage = new PageImpl<>(List.of(friendRequest));
            given(friendRequestRepository.findByReceiverIdAndStatusOrderByUpdatedAtDesc(1L, PENDING, pageable))
                    .willReturn(friendPage);

            // when
            PageResponse<FriendRequestReceivedResponse> result =
                    friendRequestService.getFriendRequests(1L, pageable);

            // then
            assertThat(result.content()).hasSize(1);
            verify(friendRequestRepository).findByReceiverIdAndStatusOrderByUpdatedAtDesc(1L, PENDING, pageable);
        }

        @Test
        @DisplayName("받은 요청이 없으면 빈 페이지를 반환한다")
        void getFriendRequests_NoRequests_ReturnsEmptyPage() {
            // given
            Page<Friend> emptyPage = Page.empty(pageable);
            given(friendRequestRepository.findByReceiverIdAndStatusOrderByUpdatedAtDesc(1L, PENDING, pageable))
                    .willReturn(emptyPage);

            // when
            PageResponse<FriendRequestReceivedResponse> result =
                    friendRequestService.getFriendRequests(1L, pageable);

            // then
            assertThat(result.content()).isEmpty();
        }
    }

    @Nested
    @DisplayName("친구 요청 보내기")
    class SendFriendRequest {

        @Test
        @DisplayName("자기 자신에게 친구 요청을 보낼 수 없다")
        void sendFriendRequest_ToSelf_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> friendRequestService.sendFriendRequest(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_SELF);
        }

        @Test
        @DisplayName("이미 친구인 경우 요청을 보낼 수 없다")
        void sendFriendRequest_AlreadyFriend_ThrowsException() {

            given(friendRequestRepository.existsFriendship(1L, 2L, ACCEPTED)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> friendRequestService.sendFriendRequest(1L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_ALREADY_FRIEND);
        }

        @Test
        @DisplayName("이미 대기 중인 요청이 있으면 보낼 수 없다")
        void sendFriendRequest_AlreadyPending_ThrowsException() {
            // given
            given(friendRequestRepository.existsFriendship(1L, 2L, ACCEPTED)).willReturn(false);
            given(friendRequestRepository.existsFriendship(1L, 2L, PENDING)).willReturn(true);
            // when & then
            assertThatThrownBy(() -> friendRequestService.sendFriendRequest(1L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_ALREADY_PENDING);
        }

        @Test
        @DisplayName("친구 요청을 성공적으로 보낸다")
        void sendFriendRequest_Success() {
            given(friendRequestRepository.existsFriendship(1L, 2L, ACCEPTED)).willReturn(false);
            given(friendRequestRepository.existsFriendship(1L, 2L, PENDING)).willReturn(false);

            given(userRepository.findById(1L)).willReturn(Optional.ofNullable(user1));
            given(userRepository.findById(2L)).willReturn(Optional.ofNullable(user2));

            given(friendRequestRepository.save(any(Friend.class)))
                    .willAnswer(inv -> inv.getArgument(0));

            FriendRequestCreateResponse result = friendRequestService.sendFriendRequest(1L, 2L);

            assertThat(result).isNotNull();
            verify(friendRequestRepository).save(any(Friend.class));
        }
    }

    @Nested
    @DisplayName("친구 요청 응답")
    class RespondFriendRequest {

        @Test
        @DisplayName("존재하지 않는 요청에 응답하면 예외가 발생한다")
        void respondOneFriendRequest_NotFound_ThrowsException() {
            // given
            FriendRequestRespondRequest request = new FriendRequestRespondRequest(2L, ACCEPTED);
            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(2L, 1L, PENDING))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> friendRequestService.respondOneFriendRequest(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_NOT_FOUND);
        }

        @Test
        @DisplayName("수신자가 아닌 사용자가 응답하면 예외가 발생한다")
        void respondOneFriendRequest_Forbidden_ThrowsException() {
            // given
            User user3 = User.createLocalUser(
                            "test3@loopon.com",
                            "loopon3",
                            "password123!",
                            null
                    );

            FriendRequestRespondRequest request = new FriendRequestRespondRequest(1L, ACCEPTED);
            Friend wrongReceiverRequest = Friend.builder()
                    .id(1L)
                    .requester(user1)
                    .receiver(user2)
                    .status(PENDING)
                    .build();

            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(1L, 3L, PENDING))
                    .willReturn(Optional.of(wrongReceiverRequest));

            // when & then
            assertThatThrownBy(() -> friendRequestService.respondOneFriendRequest(3L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_FORBIDDEN);
        }

        @Test
        @DisplayName("친구 요청을 수락한다")
        void respondOneFriendRequest_Accept_Success() {
            // given
            FriendRequestRespondRequest request = new FriendRequestRespondRequest(1L, ACCEPTED);
            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(1L, 2L, PENDING))
                    .willReturn(Optional.of(friendRequest));
            given(friendRepository.save(any(Friend.class))).willReturn(friendRequest);

            // when
            FriendRequestRespondResponse result =
                    friendRequestService.respondOneFriendRequest(2L, request);

            // then
            assertThat(result).isNotNull();
            verify(friendRepository).save(any(Friend.class));
        }
    }

    @Nested
    @DisplayName("친구 요청 일괄 응답")
    class RespondAllFriendRequests {

        @Test
        @DisplayName("요청이 없으면 0을 반환한다")
        void respondAllFriendRequests_NoRequests_ReturnsZero() {
            // given
            given(friendRequestRepository.getAllRequesterIdByStatus(1L, PENDING))
                    .willReturn(Collections.emptyList());

            // when
            FriendRequestBulkRespondResponse result =
                    friendRequestService.respondAllFriendRequests(1L, ACCEPTED);

            // then
            assertThat(result.processCount()).isZero();
        }

        @Test
        @DisplayName("잘못된 상태로 일괄 응답하면 예외가 발생한다")
        void respondAllFriendRequests_InvalidStatus_ThrowsException() {
            // given
            given(friendRequestRepository.getAllRequesterIdByStatus(1L, PENDING))
                    .willReturn(List.of(2L));

            // when & then
            assertThatThrownBy(() -> friendRequestService.respondAllFriendRequests(1L, PENDING))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_INVALID_STATUS);
        }

        @Test
        @DisplayName("모든 친구 요청을 일괄 수락한다")
        void respondAllFriendRequests_AcceptAll_Success() {
            // given
            User user3 = User.createLocalUser(
                    "test3@loopon.com",
                    "loopon3",
                    "password123!",
                    null
            );

            Friend request1 = Friend.builder()
                    .id(1L)
                    .requester(user2)
                    .receiver(user1)
                    .status(PENDING)
                    .build();

            Friend request2 = Friend.builder()
                    .id(2L)
                    .requester(user3)
                    .receiver(user1)
                    .status(PENDING)
                    .build();

            given(friendRequestRepository.getAllRequesterIdByStatus(1L, PENDING))
                    .willReturn(Arrays.asList(2L, 3L));
            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(2L, 1L, PENDING))
                    .willReturn(Optional.of(request1));
            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(3L, 1L, PENDING))
                    .willReturn(Optional.of(request2));
            given(friendRepository.saveAll(anyList()))
                    .willReturn(Arrays.asList(request1, request2));

            // when
            FriendRequestBulkRespondResponse result =
                    friendRequestService.respondAllFriendRequests(1L, ACCEPTED);

            // then
            assertThat(result.processCount()).isEqualTo(2);
            verify(friendRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("모든 친구 요청을 일괄 거절한다")
        void respondAllFriendRequests_RejectAll_Success() {
            // given
            Friend request = Friend.builder()
                    .id(1L)
                    .requester(user2)
                    .receiver(user1)
                    .status(PENDING)
                    .build();

            given(friendRequestRepository.getAllRequesterIdByStatus(1L, PENDING))
                    .willReturn(List.of(2L));
            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(2L, 1L, PENDING))
                    .willReturn(Optional.of(request));
            given(friendRepository.saveAll(anyList()))
                    .willReturn(List.of(request));

            // when
            FriendRequestBulkRespondResponse result =
                    friendRequestService.respondAllFriendRequests(1L, REJECTED);

            // then
            assertThat(result.processCount()).isEqualTo(1);
            verify(friendRepository).saveAll(anyList());
        }
    }
}