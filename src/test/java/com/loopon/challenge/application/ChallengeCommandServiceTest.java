package com.loopon.challenge.application;

import com.loopon.challenge.application.dto.command.*;
import com.loopon.challenge.application.dto.response.*;
import com.loopon.challenge.application.service.ChallengeCommandService;
import com.loopon.challenge.domain.*;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.repository.ExpeditionRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.s3.S3Service;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.notification.application.event.ChallengeLikeEvent;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeCommandServiceTest {

    @InjectMocks
    private ChallengeCommandService challengeCommandService;

    @Mock private ChallengeRepository challengeRepository;
    @Mock private JourneyJpaRepository journeyJpaRepository;
    @Mock private UserRepository userRepository;
    @Mock private ExpeditionRepository expeditionRepository;
    @Mock private S3Service s3Service;
    @Mock ApplicationEventPublisher applicationEventPublisher;

    // --- 테스트용 더미 데이터 생성 헬퍼 ---
    private User createUser(Long id) {
        User mockUser = mock(User.class);


        lenient().when(mockUser.getId()).thenReturn(id);
        lenient().when(mockUser.getNickname()).thenReturn("Tester");

        return mockUser;
    }

    private Challenge createChallenge(Long id, User user) {
        return Challenge.builder()
                .id(id)
                .user(user)
                .content("Test Content")
                .challengeImages(new ArrayList<>())
                .challengeHashtags(new ArrayList<>())
                .expedition(null)
                .build();
    }

    @Nested
    @DisplayName("챌린지 생성 (PostChallenge)")
    class PostChallengeTest {

        @Test
        @DisplayName("성공: 모든 정보가 유효하고 이미지가 있을 때 정상 저장된다.")
        void success() {
            // given
            Long userId = 1L;
            Long journeyId = 1L;
            MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "data".getBytes());

            ChallengePostCommand command = new ChallengePostCommand(
                    List.of(file),
                    List.of("tag1", "tag2"),
                    "Content",
                    journeyId,
                    null,
                    userId
            );

            User user = createUser(userId);
            Journey journey = Journey.builder().id(journeyId).build();

            given(challengeRepository.existsByJourneyId(journeyId)).willReturn(false);
            given(journeyJpaRepository.findById(journeyId)).willReturn(Optional.of(journey));
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(s3Service.uploadFile(any())).willReturn("http://s3.url/test.jpg");
            given(challengeRepository.saveHashtag(any())).willReturn(Hashtag.builder().name("tag1").build());

            // when
            ChallengePostResponse response = challengeCommandService.postChallenge(command);

            // then
            verify(challengeRepository).save(any(Challenge.class)); // 챌린지 저장 호출 확인
            verify(s3Service).uploadFile(any()); // 이미지 업로드 호출 확인
            verify(challengeRepository, atLeastOnce()).saveChallengeImage(any()); // 이미지 매핑 저장 확인
        }

        @Test
        @DisplayName("실패: 해당 여정에 이미 챌린지가 존재하면 예외가 발생한다.")
        void fail_duplicate_journey() {
            // given
            ChallengePostCommand command = new ChallengePostCommand(List.of(), List.of(), "Content", 1L,  null,1L);
            given(challengeRepository.existsByJourneyId(1L)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> challengeCommandService.postChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHALLENGE_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("실패: 해시태그가 5개를 초과하면 예외가 발생한다.")
        void fail_hashtag_limit() {
            // given
            List<String> manyTags = List.of("1", "2", "3", "4", "5", "6");
            ChallengePostCommand command = new ChallengePostCommand(List.of(), manyTags, "Content", 1L,  null,1L);

            User mockUser = mock(User.class);
            lenient().when(mockUser.getId()).thenReturn(1L);

            Journey mockJourney = mock(Journey.class);
            lenient().when(mockJourney.getId()).thenReturn(1L);

            given(challengeRepository.existsByJourneyId(1L)).willReturn(false);
            given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));
            given(journeyJpaRepository.findById(1L)).willReturn(Optional.of(mockJourney));

            // when & then
            assertThatThrownBy(() -> challengeCommandService.postChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHALLENGE_HASHTAG_LIMIT);
        }
    }

    @Nested
    @DisplayName("챌린지 수정 (ModifyChallenge)")
    class ModifyChallengeTest {

        @Test
        @DisplayName("성공: 기존 이미지를 삭제하고 새 이미지를 추가한다.")
        void success_image_update() {
            // given
            Long challengeId = 1L;
            MockMultipartFile newFile = new MockMultipartFile("new", "new.jpg", "image/jpeg", "data".getBytes());

            ChallengeModifyCommand command = new ChallengeModifyCommand(
                    challengeId,
                    List.of(newFile), List.of(0),
                    List.of(), List.of(),
                    List.of(),
                    "Updated",
                    2L
            );

            Challenge challenge = createChallenge(challengeId, createUser(1L));
            // 기존 이미지 세팅
            ChallengeImage oldImage = ChallengeImage.builder().imageUrl("old.jpg").challenge(challenge).build();
            challenge.getChallengeImages().add(oldImage);

            given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
            given(expeditionRepository.findById(2L)).willReturn(Optional.of(Expedition.builder().id(2L).build()));
            given(s3Service.uploadFile(any())).willReturn("new_s3_url");

            // when
            challengeCommandService.modifyChallenge(command);

            // then
            verify(s3Service).deleteFile("old.jpg"); // 기존 파일 삭제 확인
            verify(challengeRepository).deleteAllChallengeImageById(challengeId); // DB 매핑 삭제 확인
            verify(s3Service).uploadFile(newFile); // 새 파일 업로드 확인
        }

        @Test
        @DisplayName("실패: 수정 후 이미지 총 합이 10개를 초과하면 예외 발생")
        void fail_image_limit() {
            // given
            Long challengeId = 1L;
            List<String> remainUrls = Collections.nCopies(5, "exist.jpg");
            List<MultipartFile> newFiles = Collections.nCopies(6, new MockMultipartFile("f", "d".getBytes())); // 5+6=11개

            ChallengeModifyCommand command = new ChallengeModifyCommand(
                    challengeId, newFiles, List.of(1,2,3,4,5,6),
                    remainUrls, List.of(1,2,3,4,5),
                    List.of(),
                    "Content",
                    2L

            );

            Challenge challenge = createChallenge(challengeId, createUser(1L));
            given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
            given(expeditionRepository.findById(2L)).willReturn(Optional.of(Expedition.builder().build()));

            // when & then
            assertThatThrownBy(() -> challengeCommandService.modifyChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHALLENGE_IMAGE_LIMIT);
        }
    }

    @Nested
    @DisplayName("챌린지 좋아요 (LikeChallenge)")
    class LikeChallengeTest {

        @Test
        @DisplayName("성공: 좋아요를 누른 상태(isLiked=true)라면 좋아요가 취소된다.")
        void success_unlike() {
            // given
            User user = createUser(1L);
            Challenge challenge = createChallenge(10L, createUser(2L));
            ChallengeLike existingLike = ChallengeLike.builder().id(50L).user(user).challenge(challenge).build();

            // isLiked=true: 클라이언트가 "나 이거 좋아요 누른 상태야"라고 보냄 -> 토글 -> 취소
            ChallengeLikeCommand command = new ChallengeLikeCommand(10L, true, 1L);

            given(challengeRepository.findById(10L)).willReturn(Optional.of(challenge));
            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(challengeRepository.findChallengeLikeByUserIdAndId(1L, 10L)).willReturn(Optional.of(existingLike));

            // when
            challengeCommandService.likeChallenge(command);

            // then
            verify(challengeRepository).deleteChallengeLikeById(50L);
            verify(applicationEventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("성공: 좋아요를 안 누른 상태(isLiked=false)라면 좋아요가 추가된다.")
        void success_like() {
            // given
            User user = createUser(1L);
            Challenge challenge = createChallenge(10L, createUser(2L));
            ChallengeLikeCommand command = new ChallengeLikeCommand(10L, false,1L);

            given(challengeRepository.findById(10L)).willReturn(Optional.of(challenge));
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            challengeCommandService.likeChallenge(command);

            // then
            verify(challengeRepository).saveChallengeLike(any(ChallengeLike.class));

            ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
            verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

            Object event = eventCaptor.getValue();
            assertTrue(event instanceof ChallengeLikeEvent);

            ChallengeLikeEvent e = (ChallengeLikeEvent) event;
            assertEquals(10L, e.challengeId());
            assertEquals(2L, e.challengeOwnerId());
            assertEquals(1L, e.likedUserId());
        }
    }

    @Nested
    @DisplayName("댓글 작성 (CommentChallenge)")
    class CommentChallengeTest {

        @Test
        @DisplayName("성공: 대댓글 작성 시 부모 댓글이 존재해야 한다.")
        void success_reply() {
            // given
            ChallengeCommentCommand command = new ChallengeCommentCommand(100L, 1L, "Reply", 50L);

            Challenge challenge = createChallenge(100L, createUser(2L));
            User user = createUser(1L);
            Comment parent = Comment.builder().id(50L).build();

            given(challengeRepository.findById(100L)).willReturn(Optional.of(challenge));
            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(challengeRepository.findCommentByCommentId(50L)).willReturn(Optional.of(parent));

            // when
            challengeCommandService.commentChallenge(command);

            // then
            verify(challengeRepository).saveComment(argThat(comment ->
                    comment.getParent().getId().equals(50L) && comment.getContent().equals("Reply")
            ));
        }

        @Test
        @DisplayName("실패: 부모 댓글 ID가 있지만 DB에 없으면 예외 발생")
        void fail_parent_not_found() {
            // given
            ChallengeCommentCommand command = new ChallengeCommentCommand(100L, 1L, "Reply", 999L);
            User mockUser = mock(User.class);
            lenient().when(mockUser.getId()).thenReturn(1L);

            given(challengeRepository.findById(100L)).willReturn(Optional.of(createChallenge(100L, null)));
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mockUser));
            given(challengeRepository.findCommentByCommentId(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> challengeCommandService.commentChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("댓글 삭제 (DeleteComment)")
    class DeleteCommentTest {

        @Test
        @DisplayName("실패: 작성자가 아닌 유저가 삭제를 시도하면 예외 발생")
        void fail_not_writer() {
            // given
            Long commentId = 10L;
            Long maliciousUserId = 2L;
            Long originalWriterId = 1L;
            Long targetCommentId = 100L; // 기준 ID를 하나로 통일
            Long userId = 1L;

            // Command 생성 시 통일된 ID 사용
            ChallengeDeleteCommentCommand command = new ChallengeDeleteCommentCommand(1L, targetCommentId, userId);

            User maliciousUser = createUser(maliciousUserId);
            User originalWriter = createUser(originalWriterId);
            Comment comment = Comment.builder().id(commentId).user(originalWriter).build();
            Challenge challenge = createChallenge(100L, originalWriter);

            given(challengeRepository.findCommentByCommentId(anyLong())).willReturn(Optional.of(comment));
            given(challengeRepository.findById(anyLong())).willReturn(Optional.of(challenge));
            given(userRepository.findById(anyLong())).willReturn(Optional.of(maliciousUser));

            // 2. When & 3. Then
            assertThatThrownBy(() -> challengeCommandService.deleteCommentChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_COMMENT_WRITER);
        }

        @Test
        @DisplayName("성공: 작성자가 삭제 시도 시 정상 삭제")
        void success() {
            // 1. Given
            Long userId = 1L;
            Long commentId = 100L; // 실제 사용할 ID

            // Command 생성 시 commentId(100L) 확인
            ChallengeDeleteCommentCommand command = new ChallengeDeleteCommentCommand(1L, commentId, userId);

            User mockUser = mock(User.class);
            lenient().when(mockUser.getId()).thenReturn(userId);

            Comment mockComment = mock(Comment.class);
            lenient().when(mockComment.getId()).thenReturn(commentId);
            lenient().when(mockComment.getUser()).thenReturn(mockUser);
            org.springframework.test.util.ReflectionTestUtils.setField(mockComment, "user", mockUser);

            Challenge mockChallenge = mock(Challenge.class);
            org.springframework.test.util.ReflectionTestUtils.setField(mockChallenge, "id", 1L);

            // [수정 포인트] anyLong()을 사용하여 값 불일치 방지
            given(challengeRepository.findCommentByCommentId(anyLong())).willReturn(Optional.of(mockComment));
            given(challengeRepository.findById(anyLong())).willReturn(Optional.of(mockChallenge));
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mockUser));

            // 2. When
            challengeCommandService.deleteCommentChallenge(command);

            // 3. Then
            verify(challengeRepository).deleteComment(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("챌린지 삭제 (DeleteChallenge)")
    class DeleteChallengeTest {

        @Test
        @DisplayName("실패: 챌린지 작성자가 아니면 삭제할 수 없다")
        void fail_not_writer() {
            // given
            Long challengeId = 1L;
            Long otherUserId = 2L;

            ChallengeDeleteCommand command = new ChallengeDeleteCommand(challengeId, otherUserId);

            User owner = createUser(1L); // 주인
            User other = createUser(otherUserId); // 타인
            Challenge challenge = createChallenge(challengeId, owner);

            given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
            given(userRepository.findById(otherUserId)).willReturn(Optional.of(other));

            // when & then
            assertThatThrownBy(() -> challengeCommandService.deleteChallenge(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_CHALLENGE_WRITER);
        }
    }
}