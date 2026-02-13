package com.loopon.expedition.application;

import com.loopon.challenge.domain.Challenge;
import com.loopon.challenge.domain.ChallengeHashtag;
import com.loopon.challenge.domain.ChallengeImage;
import com.loopon.challenge.domain.Hashtag;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.application.dto.command.ExpeditionChallengesCommand;
import com.loopon.expedition.application.dto.command.ExpeditionGetCommand;
import com.loopon.expedition.application.dto.command.ExpeditionSearchCommand;
import com.loopon.expedition.application.dto.command.ExpeditionUsersCommand;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.expedition.application.service.ExpeditionQueryService;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.ExpeditionUser;
import com.loopon.expedition.domain.ExpeditionUserStatus;
import com.loopon.expedition.domain.repository.ExpeditionRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.domain.Journey;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.FriendRepository;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpeditionQueryServiceTest {

    @InjectMocks
    private ExpeditionQueryService expeditionQueryService;

    @Mock
    private ExpeditionRepository expeditionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    // -------------------------- Helper Methods (Mocking) --------------------------

    private User createMockUser(Long id, String nickname) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(id);
        lenient().when(user.getNickname()).thenReturn(nickname);
        lenient().when(user.getProfileImageUrl()).thenReturn("image_" + id);
        return user;
    }

    private Expedition createMockExpedition(Long id, String title, User admin, int limit, int current) {
        Expedition expedition = mock(Expedition.class);
        lenient().when(expedition.getId()).thenReturn(id);
        lenient().when(expedition.getTitle()).thenReturn(title);
        lenient().when(expedition.getAdmin()).thenReturn(admin);
        lenient().when(expedition.getUserLimit()).thenReturn(limit);
        lenient().when(expedition.getCurrentUsers()).thenReturn(current);
        return expedition;
    }

    // -------------------------- Test Cases --------------------------

    @Nested
    @DisplayName("내 탐험대 목록 조회 (getExpeditions)")
    class GetExpeditions {
        @Test
        @DisplayName("성공: 참여 중인 탐험대 목록과 관리자명, 멤버 수를 반환한다.")
        void success() {
            // given
            Long userId = 1L;
            User admin = createMockUser(2L, "adminUser");
            Expedition exp = createMockExpedition(100L, "Exp Title", admin, 10, 5);

            given(expeditionRepository.findApprovedExpeditionsByUserId(userId)).willReturn(List.of(exp));
            given(expeditionRepository.findAllExpeditionUserById(100L))
                    .willReturn(List.of(mock(ExpeditionUser.class), mock(ExpeditionUser.class)));

            // when
            ExpeditionGetResponseList result = expeditionQueryService.getExpeditionList(userId);

            // then
            assertThat(result.expeditionGetResponses()).hasSize(1);
            assertThat(result.expeditionGetResponses().get(0).admin()).isEqualTo("adminUser");
            assertThat(result.expeditionGetResponses().get(0).currentUsers()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("탐험대 검색 (searchExpedition)")
    class SearchExpedition {
        @Test
        @DisplayName("성공: 가입 조건(미가입, 인원여유, 탐험대수제한)을 모두 만족하면 canJoin이 true가 된다.")
        void success_can_join_true() {
            // given
            Long userId = 1L;
            User user = createMockUser(userId, "me");
            ExpeditionSearchCommand command = new ExpeditionSearchCommand("keyword", List.of(true, false, false), PageRequest.of(0, 10), userId);

            // 조건 2: 5 < 10 (notAboveUserLimit = true)
            Expedition exp = createMockExpedition(100L, "Title", user, 10, 5);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(expeditionRepository.findByTitleContainingAndCategoryIn(anyString(), anyList(), any(Pageable.class)))
                    .willReturn(new SliceImpl<>(List.of(exp)));

            // 조건 1: 미가입 상태 (notJoined = true)
            given(expeditionRepository.findJoinedExpeditionIds(eq(userId), anyList())).willReturn(new ArrayList<>());

            // 조건 3: 탐험대 수 제한 (현재 3개 < 5개, notAboveExpeditionLimit = true)
            ExpeditionUser eu = mock(ExpeditionUser.class);
            lenient().when(eu.getStatus()).thenReturn(ExpeditionUserStatus.APPROVED);
            given(expeditionRepository.findAllExpeditionUserByUserId(userId)).willReturn(Collections.nCopies(3, eu));

            // when
            Slice<ExpeditionSearchResponse> result = expeditionQueryService.searchExpedition(command);

            // then
            assertThat(result.getContent().get(0).isJoined()).isTrue();
        }

        @Test
        @DisplayName("실패: 탐험대 정원이 가득 찬 경우 canJoin은 false이다.")
        void fail_can_join_false_due_to_limit() {
            // given
            Long userId = 1L;
            User user = createMockUser(userId, "me");
            ExpeditionSearchCommand command = new ExpeditionSearchCommand("keyword", List.of(true, true, true), PageRequest.of(0, 10), userId);

            // 조건 2 실패: 10 < 10 (notAboveUserLimit = false)
            Expedition fullExp = createMockExpedition(100L, "Full Exp", user, 10, 10);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(expeditionRepository.findByTitleContainingAndCategoryIn(any(), any(), any())).willReturn(new SliceImpl<>(List.of(fullExp)));
            given(expeditionRepository.findJoinedExpeditionIds(any(), any())).willReturn(new ArrayList<>());
            given(expeditionRepository.findAllExpeditionUserByUserId(any())).willReturn(new ArrayList<>());

            // when
            Slice<ExpeditionSearchResponse> result = expeditionQueryService.searchExpedition(command);

            // then
            assertThat(result.getContent().get(0).isJoined()).isFalse();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 유저 아이디로 검색 시 예외가 발생한다.")
        void fail_user_not_found() {
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());
            ExpeditionSearchCommand command = new ExpeditionSearchCommand("kw", List.of(true, true, true), PageRequest.of(0, 10), 1L);

            assertThatThrownBy(() -> expeditionQueryService.searchExpedition(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("탐험대 멤버 목록 조회 (usersExpedition)")
    class UsersExpedition {
        @Test
        @DisplayName("성공: 멤버들의 본인여부, 방장여부, 친구상태를 정확히 매핑한다.")
        void success() {
            // given
            Long myId = 1L;
            Long friendId = 2L;
            Long expId = 100L;
            User me = createMockUser(myId, "me");
            User friendUser = createMockUser(friendId, "friend");
            Expedition exp = createMockExpedition(expId, "Exp", me, 10, 2);

            ExpeditionUser eu1 = mock(ExpeditionUser.class);
            lenient().when(eu1.getUser()).thenReturn(me);
            ExpeditionUser eu2 = mock(ExpeditionUser.class);
            lenient().when(eu2.getUser()).thenReturn(friendUser);

            given(expeditionRepository.findById(expId)).willReturn(Optional.of(exp));
            given(userRepository.findById(myId)).willReturn(Optional.of(me));
            given(expeditionRepository.findAllExpeditionUserWithUserById(expId)).willReturn(List.of(eu1, eu2));
            given(expeditionRepository.existsExpeditionUserByIdAndUserId(expId, myId)).willReturn(true);
            // 친구 목록 모킹
            Friend friendRecord = mock(Friend.class);
            lenient().when(friendRecord.getId()).thenReturn(friendId);
            lenient().when(friendRecord.getStatus()).thenReturn(FriendStatus.ACCEPTED);
            given(friendRepository.findFriendsByUserIdAndStatus(myId, FriendStatus.ACCEPTED)).willReturn(new ArrayList<>(List.of(friendRecord)));
            given(friendRepository.findFriendsByUserIdAndStatus(myId, FriendStatus.PENDING)).willReturn(new ArrayList<>());

            // when
            ExpeditionUsersResponse result = expeditionQueryService.usersExpedition(new ExpeditionUsersCommand(expId, myId));

            // then
            assertThat(result.userList()).hasSize(2);

            // 나(Me) 검증
            ExpeditionUsersResponse.UserInfo meInfo = result.userList().stream().filter(u -> u.userId().equals(myId)).findFirst().get();
            assertThat(meInfo.isMe()).isTrue();
            assertThat(meInfo.isHost()).isTrue();

            // 친구(Friend) 검증
            ExpeditionUsersResponse.UserInfo friendInfo = result.userList().stream().filter(u -> u.userId().equals(friendId)).findFirst().get();
            assertThat(friendInfo.isMe()).isFalse();
            assertThat(friendInfo.friendStatus()).isEqualTo(FriendStatus.ACCEPTED);
        }
    }

    @Nested
    @DisplayName("탐험대 내 챌린지 조회 (challengesExpedition)")
    class ChallengesExpedition {
        @Test
        @DisplayName("성공: 챌린지 정보와 이미지, 해시태그, 좋아요 여부를 반환한다.")
        void success() {
            // given
            Long userId = 1L;
            Long expId = 100L;
            Long challengeId = 500L;
            User user = createMockUser(userId, "tester");
            Expedition exp = createMockExpedition(expId, "Exp", user, 10, 5);

            Challenge challenge = mock(Challenge.class);
            lenient().when(challenge.getId()).thenReturn(challengeId);
            lenient().when(challenge.getJourney()).thenReturn(mock(Journey.class));

            // 이미지 모킹
            ChallengeImage img = mock(ChallengeImage.class);
            lenient().when(img.getImageUrl()).thenReturn("img_url");
            lenient().when(challenge.getChallengeImages()).thenReturn(List.of(img));

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(expeditionRepository.findById(expId)).willReturn(Optional.of(exp));
            given(challengeRepository.findAllWithJourneyAndUserByExpeditionId(eq(expId), any(Pageable.class)))
                    .willReturn(new SliceImpl<>(List.of(challenge)));
            given(expeditionRepository.existsExpeditionUserByIdAndUserId(expId, userId)).willReturn(true);


            // 해시태그 모킹
            ChallengeHashtag ch = mock(ChallengeHashtag.class);
            Hashtag h = mock(Hashtag.class);
            lenient().when(h.getName()).thenReturn("hashtag1");
            lenient().when(ch.getHashtag()).thenReturn(h);
            given(challengeRepository.findAllChallengeHashtagWithHashtagByChallengeId(challengeId)).willReturn(List.of(ch));

            given(challengeRepository.existsChallengeLikeByIdAndUserId(challengeId, userId)).willReturn(true);

            // when
            Slice<ExpeditionChallengesResponse> result = expeditionQueryService.challengesExpedition(new ExpeditionChallengesCommand(expId, userId, PageRequest.of(0, 10)));

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().isLiked()).isTrue();
            assertThat(result.getContent().getFirst().hashtags()).contains("hashtag1");
        }

        @Test
        @DisplayName("탐험대 상세 조회 성공")
        void getExpedition_Success() {
            // given
            Long expeditionId = 1L;
            Long userId = 10L;
            ExpeditionGetCommand command = new ExpeditionGetCommand(expeditionId, userId);

            Expedition mockExpedition = mock(Expedition.class);
            User mockUser = mock(User.class);
            lenient().when(mockExpedition.getAdmin()).thenReturn(mockUser);

            // stubbing
            given(expeditionRepository.findById(expeditionId)).willReturn(Optional.of(mockExpedition));
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));


            // when
            ExpeditionGetResponse response = expeditionQueryService.getExpedition(command);

            // then
            assertNotNull(response);
            verify(expeditionRepository, times(1)).findById(expeditionId);
            verify(userRepository, times(1)).findById(userId);

        }

        @Test
        @DisplayName("사용자가 방장이 아닐 때 권한 예외 발생")
        void getExpedition_Forbidden_Not_Admin() {
            // given
            Long expeditionId = 1L;
            Long userId = 10L;
            Long adminId = 99L;

            ExpeditionGetCommand command = new ExpeditionGetCommand(expeditionId, userId);


            Expedition mockExpedition = mock(Expedition.class);

            User mockUser = mock(User.class);
            lenient().when(mockUser.getId()).thenReturn(userId);
            User mockAdmin = mock(User.class);
            lenient().when(mockAdmin.getId()).thenReturn(adminId);


            given(expeditionRepository.findById(expeditionId)).willReturn(Optional.of(mockExpedition));
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));


            given(mockExpedition.getAdmin()).willReturn(mockAdmin);


            BusinessException exception = assertThrows(BusinessException.class, () -> {
                expeditionQueryService.getExpedition(command);
            });


            assertEquals(ErrorCode.NOT_ADMIN_USER, exception.getErrorCode());
        }
    }
}