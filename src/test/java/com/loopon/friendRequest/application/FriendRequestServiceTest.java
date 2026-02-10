package com.loopon.friendRequest.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.exception.BusinessException;
import com.loopon.notification.application.event.FriendRequestCreatedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private FriendRequestServiceImpl friendRequestService;

    private User user1;
    private User user2;
    private Friend friendRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user1 = User.createLocalUser("test1@loopon.com", "loopon1", "password123!", null);
        ReflectionTestUtils.setField(user1, "id", 1L);

        user2 = User.createLocalUser("test2@loopon.com", "loopon2", "password123!", null);
        ReflectionTestUtils.setField(user2, "id", 2L);

        friendRequest = Friend.builder().id(1L).requester(user1).receiver(user2).status(PENDING).build();

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

            // null 가능성/매칭 이슈 없이 안전하게 검증
            verify(friendRequestRepository, never())
                    .searchByNickname(anyLong(), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("검색어가 null이면 빈 페이지를 반환한다")
        void findNewFriend_NullQuery_ReturnsEmptyPage() {
            // when
            PageResponse<FriendSearchResponse> result =
                    friendRequestService.findNewFriend(1L, null, pageable);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();

            // anyString()은 null 매칭이 안 되므로 any()로
            verify(friendRequestRepository, never())
                    .searchByNickname(anyLong(), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("유효한 검색어로 사용자를 검색한다")
        void findNewFriend_ValidQuery_ReturnsUsers() {
            // given
            String query = "user";
            String trimmed = query.trim();

            FriendSearchResponse response = new FriendSearchResponse(
                    user2.getNickname(),
                    user2.getBio(),
                    NOT_FRIENDS,           // 또는 null (서비스/프론트 정책에 맞게)
                    user2.getProfileImageUrl(),
                    user2.getId()
            );
            Page<FriendSearchResponse> page =
                    new PageImpl<>(List.of(response), pageable, 1);

            given(friendRequestRepository.searchByNickname(1L, trimmed, pageable))
                    .willReturn(page);

            // when
            PageResponse<FriendSearchResponse> result =
                    friendRequestService.findNewFriend(1L, query, pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1);

            // query.trim() 적용 여부에 따라 verify 인자도 맞춰야 함
            verify(friendRequestRepository).searchByNickname(1L, trimmed, pageable);
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
            given(friendRequestRepository.findByReceiverIdAndStatusOrderByUpdatedAtDesc(1L, PENDING, pageable)).willReturn(friendPage);

            // when
            PageResponse<FriendRequestReceivedResponse> result = friendRequestService.getFriendRequests(1L, pageable);

            // then
            assertThat(result.content()).hasSize(1);
            verify(friendRequestRepository).findByReceiverIdAndStatusOrderByUpdatedAtDesc(1L, PENDING, pageable);
        }

        @Test
        @DisplayName("받은 요청이 없으면 빈 페이지를 반환한다")
        void getFriendRequests_NoRequests_ReturnsEmptyPage() {
            // given
            Page<Friend> emptyPage = Page.empty(pageable);
            given(friendRequestRepository.findByReceiverIdAndStatusOrderByUpdatedAtDesc(1L, PENDING, pageable)).willReturn(emptyPage);

            // when
            PageResponse<FriendRequestReceivedResponse> result = friendRequestService.getFriendRequests(1L, pageable);

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
            assertThatThrownBy(() -> friendRequestService.sendFriendRequest(1L, 1L)).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_SELF);
        }

        @Test
        @DisplayName("이미 친구인 경우 요청을 보낼 수 없다")
        void sendFriendRequest_AlreadyFriend_ThrowsException() {

            given(friendRequestRepository.existsFriendship(1L, 2L, ACCEPTED)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> friendRequestService.sendFriendRequest(1L, 2L)).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_ALREADY_FRIEND);
        }

        @Test
        @DisplayName("이미 대기 중인 요청이 있으면 보낼 수 없다")
        void sendFriendRequest_AlreadyPending_ThrowsException() {
            // given
            given(friendRequestRepository.existsFriendship(1L, 2L, ACCEPTED)).willReturn(false);
            given(friendRequestRepository.existsFriendship(1L, 2L, PENDING)).willReturn(true);
            // when & then
            assertThatThrownBy(() -> friendRequestService.sendFriendRequest(1L, 2L)).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_ALREADY_PENDING);
        }

        @Test
        @DisplayName("친구 요청을 성공적으로 보낸다")
        void sendFriendRequest_Success() {
            given(friendRequestRepository.existsFriendship(1L, 2L, ACCEPTED)).willReturn(false);
            given(friendRequestRepository.existsFriendship(1L, 2L, PENDING)).willReturn(false);

            given(userRepository.findById(1L)).willReturn(Optional.ofNullable(user1));
            given(userRepository.findById(2L)).willReturn(Optional.ofNullable(user2));

            given(friendRequestRepository.save(any(Friend.class))).willAnswer(inv -> inv.getArgument(0));

            FriendRequestCreateResponse result = friendRequestService.sendFriendRequest(1L, 2L);

            assertThat(result).isNotNull();
            verify(friendRequestRepository).save(any(Friend.class));

            verify(applicationEventPublisher).publishEvent(any(FriendRequestCreatedEvent.class));
        }
    }

    @Nested
    @DisplayName("친구 요청 단건 처리")
    class OneRequestActions {

        @Test
        @DisplayName("acceptOneFriendRequest: PENDING 요청 수락")
        void acceptOne_success() {
            Friend pending = Friend.request(user2, user1); // requester=2, receiver=1
            ReflectionTestUtils.setField(pending, "id", 10L);

            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(2L, 1L, PENDING)).willReturn(Optional.of(pending));
            given(friendRepository.save(any(Friend.class))).willAnswer(inv -> inv.getArgument(0));

            FriendRequestRespondResponse res = friendRequestService.acceptOneFriendRequest(1L, 2L);

            assertThat(res).isNotNull();
            assertThat(pending.getStatus()).isEqualTo(ACCEPTED);
            then(friendRepository).should(times(1)).save(pending);
        }

        @Test
        @DisplayName("acceptOneFriendRequest: 요청이 없으면 예외")
        void acceptOne_notFound_throws() {
            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(2L, 1L, PENDING)).willReturn(Optional.empty());

            assertThatThrownBy(() -> friendRequestService.acceptOneFriendRequest(1L, 2L)).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("errorCode", ErrorCode.FRIEND_REQUEST_NOT_FOUND);
        }

        @Test
        @DisplayName("deleteOneFriendRequest: PENDING 요청 삭제")
        void deleteOne_success() {
            Friend pending = Friend.request(user2, user1);
            ReflectionTestUtils.setField(pending, "id", 10L);
            given(friendRequestRepository.findByRequesterIdAndReceiverIdAndStatus(2L, 1L, PENDING)).willReturn(Optional.of(pending));

            friendRequestService.deleteOneFriendRequest(1L, 2L);

            then(friendRequestRepository).should(times(1)).delete(pending);
            then(friendRepository).shouldHaveNoInteractions();
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  accept/delete all
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("친구 요청 일괄 처리")
    class BulkActions {

        @Test
        @DisplayName("acceptAllFriendRequests: 없으면 0 반환")
        void acceptAll_empty_returns0() {
            given(friendRequestRepository.findAllByReceiverIdAndStatus(1L, PENDING)).willReturn(List.of());

            FriendRequestBulkRespondResponse res = friendRequestService.acceptAllFriendRequests(1L);

            assertThat(res.processCount()).isZero();
            then(friendRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("acceptAllFriendRequests: 모두 ACCEPTED로 변경 후 saveAll")
        void acceptAll_success() {
            Friend fr1 = Friend.request(user2, user1);
            Friend fr2 = Friend.request(user2, user1);

            given(friendRequestRepository.findAllByReceiverIdAndStatus(1L, PENDING)).willReturn(List.of(fr1, fr2));

            FriendRequestBulkRespondResponse res = friendRequestService.acceptAllFriendRequests(1L);

            assertThat(fr1.getStatus()).isEqualTo(ACCEPTED);
            assertThat(fr2.getStatus()).isEqualTo(ACCEPTED);
            assertThat(res.processCount()).isEqualTo(2L);

            then(friendRepository).should(times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("deleteAllFriendRequests: 없으면 0 반환")
        void deleteAll_empty_returns0() {
            given(friendRequestRepository.findAllByReceiverIdAndStatus(1L, PENDING)).willReturn(List.of());

            FriendRequestBulkRespondResponse res = friendRequestService.deleteAllFriendRequests(1L);

            assertThat(res.processCount()).isZero();
            then(friendRequestRepository).should(never()).deleteAllInBatch(anyList());
        }

        @Test
        @DisplayName("deleteAllFriendRequests: 요청들 batch 삭제")
        void deleteAll_success() {
            Friend fr1 = Friend.request(user2, user1);
            Friend fr2 = Friend.request(user2, user1);

            given(friendRequestRepository.findAllByReceiverIdAndStatus(1L, PENDING)).willReturn(List.of(fr1, fr2));

            FriendRequestBulkRespondResponse res = friendRequestService.deleteAllFriendRequests(1L);

            assertThat(res.processCount()).isEqualTo(2L);
            then(friendRequestRepository).should(times(1)).deleteAllInBatch(anyList());
            then(friendRepository).shouldHaveNoInteractions();
        }
    }

}