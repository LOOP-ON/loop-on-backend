package com.loopon.challenge.application;

import com.loopon.challenge.application.dto.command.*;
import com.loopon.challenge.application.dto.response.*;
import com.loopon.challenge.application.service.ChallengeQueryService;
import com.loopon.challenge.domain.*;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.domain.dto.SliceResponse;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.domain.Journey;
import com.loopon.user.domain.*;
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

import java.time.LocalDateTime;
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
class ChallengeQueryServiceTest {

    @InjectMocks
    private ChallengeQueryService challengeQueryService;

    @Mock private ChallengeRepository challengeRepository;
    @Mock private UserRepository userRepository;
    @Mock private FriendRepository friendRepository;


    private User createTestUser(Long id, String nickname, UserVisibility visibility) {
        User mockUser = mock(User.class);

        lenient().when(mockUser.getId()).thenReturn(id);
        lenient().when(mockUser.getNickname()).thenReturn(nickname);
        lenient().when(mockUser.getVisibility()).thenReturn(visibility);

        return mockUser;
    }

    private Challenge createTestChallenge(Long id) {
        Challenge mockChallenge = mock(Challenge.class);

        lenient().when(mockChallenge.getId()).thenReturn(id);
        lenient().when(mockChallenge.getContent()).thenReturn("Test Content");
        lenient().when(mockChallenge.getChallengeHashtags()).thenReturn(new ArrayList<>());
        lenient().when(mockChallenge.getChallengeImages()).thenReturn(new ArrayList<>());
        lenient().when(mockChallenge.getJourney()).thenReturn(mock(Journey.class));
        lenient().when(mockChallenge.getUser()).thenReturn(mock(User.class));


        return mockChallenge;
    }

    @Nested
    @DisplayName("챌린지 상세 조회 (getChallenge)")
    class GetChallengeTest {

        @Test
        @DisplayName("성공: 이미지와 해시태그가 순서대로 정렬되어 반환된다.")
        void success() {
            // given
            Long challengeId = 1L;

            // challenge가 Mock이라면 getId() 대답이 필요합니다.
            Challenge challenge = mock(Challenge.class);
            when(challenge.getId()).thenReturn(challengeId);
            when(challenge.getContent()).thenReturn("Test Content");

            ChallengeImage img1 = mock(ChallengeImage.class);
            when(img1.getImageUrl()).thenReturn("url1");
            when(img1.getDisplayOrder()).thenReturn(2);

            ChallengeImage img2 = mock(ChallengeImage.class);
            when(img2.getImageUrl()).thenReturn("url2"); // 수정 완료
            when(img2.getDisplayOrder()).thenReturn(1); // 수정 완료

            List<ChallengeImage> imgList = new ArrayList<>();
            imgList.add(img1);
            imgList.add(img2);

            given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
            given(challengeRepository.findAllImageByChallengeId(challengeId)).willReturn(imgList);
            given(challengeRepository.findAllChallengeHashtagWithHashtagByChallengeId(challengeId)).willReturn(new ArrayList<>());

            // when
            ChallengeGetResponse response = challengeQueryService.getChallenge(challengeId);

            // then
            assertThat(response.challengeId()).isEqualTo(challengeId);
            // displayOrder가 1인 img2의 "url2"가 첫 번째로 와야 함
            assertThat(response.imageList().getFirst()).isEqualTo("url2");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 챌린지 ID 조회 시 NOT_FOUND 예외 발생")
        void fail_not_found() {
            given(challengeRepository.findById(anyLong())).willReturn(Optional.empty());

            assertThatThrownBy(() -> challengeQueryService.getChallenge(99L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("댓글 조회 (getCommentChallenge)")
    class GetCommentChallengeTest {

        @Test
        @DisplayName("성공: 부모 댓글과 그에 달린 대댓글들이 맵핑되어 반환된다.")
        void success() {
            // given
            ChallengeGetCommentCommand command = new ChallengeGetCommentCommand(1L, 1L, PageRequest.of(0, 10));
            Challenge challenge = createTestChallenge(1L);

            User parentUser = createTestUser(1L, "parent", UserVisibility.PUBLIC);
            when(parentUser.getProfileImageUrl()).thenReturn("url1");

            Comment parent = mock(Comment.class);
            when(parent.getId()).thenReturn(10L);
            when(parent.getUser()).thenReturn(parentUser);

            Slice<Comment> parentSlice = new SliceImpl<>(List.of(parent));

            User childUser = createTestUser(2L, "child", UserVisibility.PUBLIC);
            when(childUser.getProfileImageUrl()).thenReturn("url2");

            Comment child = mock(Comment.class);
            when(child.getId()).thenReturn(11L);
            when(child.getParent()).thenReturn(parent);
            when(child.getUser()).thenReturn(childUser);

            given(challengeRepository.findById(1L)).willReturn(Optional.of(challenge));
            given(challengeRepository.findCommentsWithUserByChallengeId(eq(1L), any())).willReturn(parentSlice);
            given(challengeRepository.findAllCommentWithUserByParentIdIn(anyList())).willReturn(List.of(child));

            // when
            SliceResponse<ChallengeGetCommentResponse> result = challengeQueryService.getCommentChallenge(command);

            // then
            assertThat(result.content()).hasSize(1);
            verify(challengeRepository).findAllCommentWithUserByParentIdIn(List.of(10L));
        }
    }

    @Nested
    @DisplayName("타인 챌린지 조회 (othersChallenge)")
    class OthersChallengeTest {

        @Test
        @DisplayName("성공: 비공개 계정이라도 친구 상태가 ACCEPTED라면 조회가 가능하다.")
        void success_private_friend() {
            // 1. Given
            Long myId = 1L;
            Long targetUserId = 2L;

            // 유저 생성 시 ID 확실히 주입 (createTestUser 메서드가 mockUser.getId() -> id 를 리턴하게 되어있어야 함)
            User myself = createTestUser(myId, "me", UserVisibility.PUBLIC);
            User target = createTestUser(targetUserId, "target", UserVisibility.PRIVATE);

            ChallengeOthersCommand command = new ChallengeOthersCommand(myId, "target", PageRequest.of(0, 10));

            given(userRepository.findById(myId)).willReturn(Optional.of(myself));
            given(userRepository.findByNickname("target")).willReturn(Optional.of(target));

            given(friendRepository.existsFriendship(eq(targetUserId), eq(myId), eq(FriendStatus.ACCEPTED)))
                    .willReturn(true);

            given(challengeRepository.findViewByUserId(anyLong(), any(Pageable.class)))
                    .willReturn(new SliceImpl<>(List.of()));

            // 2. When
            challengeQueryService.othersChallenge(command);

            // 3. Then
            verify(challengeRepository).findViewByUserId(eq(targetUserId), any(Pageable.class));
        }

        @Test
        @DisplayName("실패: 비공개 계정인데 친구가 아니면 CHALLENGE_FORBIDDEN 예외 발생")
        void fail_private_not_friend() {
            // given
            User myself = createTestUser(1L, "me", UserVisibility.PUBLIC);
            User target = createTestUser(2L, "target", UserVisibility.PRIVATE);
            ChallengeOthersCommand command = new ChallengeOthersCommand(1L, "target", PageRequest.of(0, 10));

            given(userRepository.findById(1L)).willReturn(Optional.of(myself));
            given(userRepository.findByNickname("target")).willReturn(Optional.of(target));
            given(friendRepository.existsFriendship(2L, 1L, FriendStatus.ACCEPTED)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> challengeQueryService.othersChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHALLENGE_FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("메인 피드 조회 (viewChallenge)")
    class ViewChallengeTest {

        @Test
        @DisplayName("성공: 트렌딩 게시물과 친구 게시물을 합쳐서 반환한다.")
        void success_combined_view() {
            // given
            User user = createTestUser(1L, "me", UserVisibility.PUBLIC);
            User friend = createTestUser(2L, "friend", UserVisibility.PUBLIC);
            ChallengeViewCommand command = new ChallengeViewCommand(1L, PageRequest.of(0, 5), PageRequest.of(0, 5));

            Challenge trending = createTestChallenge(100L);
            Challenge friendCh = createTestChallenge(200L);

            when(trending.getId()).thenReturn(100L);
            when(friend.getId()).thenReturn(200L);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(challengeRepository.findTrendingChallenges(any(LocalDateTime.class), anyLong(), any(Pageable.class)))
                    .willReturn(new SliceImpl<>(List.of(trending)));

            // 친구 목록 mock
            Friend mockFriend = mock(Friend.class);
            when(mockFriend.getRequester()).thenReturn(user);
            when(mockFriend.getReceiver()).thenReturn(friend);
            given(friendRepository.findFriendsByUserIdAndStatus(1L, FriendStatus.ACCEPTED)).willReturn(List.of(mockFriend));

            given(challengeRepository.findFriendsChallenges(anyList(), anyList(), any(Pageable.class)))
                    .willReturn(new SliceImpl<>(List.of(friendCh)));

            given(challengeRepository.findLikedChallengeIds(anyLong(), anyList())).willReturn(Set.of(100L));

            // when
            ChallengeCombinedViewResponse result = challengeQueryService.viewChallenge(command);

            // then
            assertThat(result.trendingChallenges().content()).hasSize(1);
            assertThat(result.friendChallenges().content()).hasSize(1);
            assertThat(result.trendingChallenges().content().getFirst().isLiked()).isTrue();
        }

        @Test
        @DisplayName("실패: 유저가 존재하지 않으면 NOT_FOUND")
        void fail_user_not_found() {
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());
            ChallengeViewCommand command = new ChallengeViewCommand(1L, null, null);

            assertThatThrownBy(() -> challengeQueryService.viewChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("내 챌린지 조회 (myChallenge)")
    class MyChallengeTest {

        @Test
        @DisplayName("성공: 유저가 존재하면 해당 유저의 챌린지 목록을 Slice로 반환한다.")
        void success() {
            // given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            ChallengeMyCommand command = new ChallengeMyCommand(userId, pageable);

            User user = createTestUser(userId, "me", UserVisibility.PUBLIC);

            // 가짜 결과물(Slice) 생성
            ChallengePreviewResponse preview = new ChallengePreviewResponse(100L, "thumb.jpg"); // 필드 구성은 DTO에 맞게 조정
            Slice<ChallengePreviewResponse> expectedSlice = new SliceImpl<>(List.of(preview), pageable, false);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(challengeRepository.findViewByUserId(userId, pageable)).willReturn(expectedSlice);

            // when
            SliceResponse<ChallengePreviewResponse> result = challengeQueryService.myChallenge(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().getFirst().challengeId()).isEqualTo(100L);

            // 리포지토리가 정확한 userId로 조회했는지 검증
            verify(challengeRepository).findViewByUserId(eq(userId), eq(pageable));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 유저 ID로 요청 시 NOT_FOUND 예외가 발생한다.")
        void fail_user_not_found() {
            // given
            Long userId = 999L;
            ChallengeMyCommand command = new ChallengeMyCommand(userId, PageRequest.of(0, 10));

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> challengeQueryService.myChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND);
        }
    }

    @Test
    @DisplayName("챌린지 상세 목록 조회 성공 - 연관 엔티티 포함")
    void detailsChallenge_Success() {
        // given
        String nickname = "testUser";
        Pageable pageable = PageRequest.of(0, 10);
        ChallengeDetailCommand command = new ChallengeDetailCommand(nickname, pageable);

        User mockUser = mock(User.class);
        Challenge mockChallenge = mock(Challenge.class);
        Journey mockJourney = mock(Journey.class);
        User author = mock(User.class);


        List<Challenge> challengeList = List.of(mockChallenge);
        Slice<Challenge> mockSlice = new SliceImpl<>(challengeList, pageable, false);


        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(mockUser));
        given(mockUser.getId()).willReturn(1L);
        given(challengeRepository.findAllWithJourneyAndUserByUserId(1L, pageable)).willReturn(mockSlice);


        given(mockChallenge.getId()).willReturn(100L);
        given(mockChallenge.getContent()).willReturn("챌린지 내용");
        given(mockChallenge.getCreatedAt()).willReturn(LocalDateTime.now());
        given(mockChallenge.getLikeCount()).willReturn(5);


        given(mockChallenge.getJourney()).willReturn(mockJourney);
        given(mockJourney.getJourneyOrder()).willReturn(1);

        given(mockChallenge.getUser()).willReturn(author);
        given(author.getNickname()).willReturn("작성자닉네임");
        given(author.getProfileImageUrl()).willReturn("https://image.com/profile");

        // when
        SliceResponse<ChallengeDetailResponse> result = challengeQueryService.detailsChallenge(command);

        // then
        assertNotNull(result);
        ChallengeDetailResponse firstResponse = result.content().getFirst();

        assertEquals(100L, firstResponse.challengeId());
        assertEquals("작성자닉네임", firstResponse.nickname());
        assertEquals(1, firstResponse.journeySequence());

        verify(userRepository).findByNickname(nickname);
        verify(challengeRepository).findAllWithJourneyAndUserByUserId(1L, pageable);
    }

    @Test
    @DisplayName("존재하지 않는 닉네임 조회 시 예외 발생")
    void detailsChallenge_UserNotFound() {
        // given

        ChallengeDetailCommand command = new ChallengeDetailCommand("unknown", PageRequest.of(0, 10));
        given(userRepository.findByNickname(anyString())).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                challengeQueryService.detailsChallenge(command)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}
