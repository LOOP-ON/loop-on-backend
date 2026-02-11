package com.loopon.expedition.application;

import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.application.dto.command.*;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.expedition.application.service.ExpeditionCommandService;
import com.loopon.expedition.domain.*;
import com.loopon.expedition.domain.repository.ExpeditionRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpeditionCommandServiceTest {

    @InjectMocks
    private ExpeditionCommandService expeditionCommandService;

    @Mock
    private ExpeditionRepository expeditionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    // -------------------------- Helper Methods (Mocking) --------------------------

    private User createMockUser(Long id, String nickname) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(id);
        lenient().when(user.getNickname()).thenReturn(nickname);
        return user;
    }

    private Expedition createMockExpedition(Long id, String title, User admin) {
        Expedition expedition = mock(Expedition.class);
        lenient().when(expedition.getId()).thenReturn(id);
        lenient().when(expedition.getTitle()).thenReturn(title);
        lenient().when(expedition.getAdmin()).thenReturn(admin);
        lenient().when(expedition.getUserLimit()).thenReturn(10);
        lenient().when(expedition.getPassword()).thenReturn("1234");
        return expedition;
    }

    private ExpeditionUser createMockExpeditionUser(Long id, User user, ExpeditionUserStatus status) {
        ExpeditionUser eu = mock(ExpeditionUser.class);
        lenient().when(eu.getId()).thenReturn(id);
        lenient().when(eu.getUser()).thenReturn(user);
        lenient().when(eu.getStatus()).thenReturn(status);
        return eu;
    }

    // -------------------------- Test Cases --------------------------

    @Nested
    @DisplayName("탐험대 생성 (postExpedition)")
    class PostExpeditionTest {
        @Test
        @DisplayName("성공: 탐험대 개수 제한(5개)을 넘지 않으면 생성이 가능하다.")
        void success() {
            // given
            Long userId = 1L;
            User user = createMockUser(userId, "tester");
            ExpeditionPostCommand command = new ExpeditionPostCommand("Title", 10, ExpeditionVisibility.PUBLIC, ExpeditionCategory.GROWTH, "pw", userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(expeditionRepository.findAllExpeditionUserByUserId(userId)).willReturn(new ArrayList<>());

            // when
            expeditionCommandService.postExpedition(command);

            // then
            verify(expeditionRepository).save(any(Expedition.class));
            verify(expeditionRepository).saveExpeditionUser(any(ExpeditionUser.class));
        }

        @Test
        @DisplayName("실패: 이미 5개의 탐험대에 참여 중이면 생성할 수 없다.")
        void fail_expedition_limit() {
            // given
            Long userId = 1L;
            User user = createMockUser(userId, "tester");
            List<ExpeditionUser> fullList = Collections.nCopies(5, createMockExpeditionUser(100L, user, ExpeditionUserStatus.APPROVED));

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(expeditionRepository.findAllExpeditionUserByUserId(userId)).willReturn(fullList);

            // when & then
            assertThatThrownBy(() -> expeditionCommandService.postExpedition(new ExpeditionPostCommand("Title", 10, ExpeditionVisibility.PUBLIC, ExpeditionCategory.GROWTH, "pw", userId)))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPEDITION_ABOVE_LIMIT);
        }
    }

    @Nested
    @DisplayName("탐험대 가입 (joinExpedition)")
    class JoinExpeditionTest {
        @Test
        @DisplayName("성공: 모든 조건을 만족하면 가입에 성공한다.")
        void success() {
            // given
            Long userId = 1L;
            Long expId = 100L;
            User user = createMockUser(userId, "tester");
            Expedition expedition = createMockExpedition(expId, "Exp", null);
            ExpeditionJoinCommand command = new ExpeditionJoinCommand(expId, userId, ExpeditionVisibility.PUBLIC, null);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(expeditionRepository.findById(expId)).willReturn(Optional.of(expedition));
            given(expeditionRepository.countExpeditionUserByExpeditionId(expId)).willReturn(5); // 현재 5명

            // when
            expeditionCommandService.joinExpedition(command);

            // then
            verify(expeditionRepository).saveExpeditionUser(any(ExpeditionUser.class));
        }

        @Test
        @DisplayName("실패: 강퇴당한 유저는 다시 가입할 수 없다.")
        void fail_expelled_user() {
            // given
            Long userId = 1L;
            Long expId = 100L;
            User user = createMockUser(userId, "tester");
            Expedition expedition = createMockExpedition(expId, "Exp", null);
            ExpeditionUser expelledRecord = createMockExpeditionUser(500L, user, ExpeditionUserStatus.EXPELLED);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(expeditionRepository.findById(expId)).willReturn(Optional.of(expedition));
            given(expeditionRepository.findAllExpeditionUserById(expId)).willReturn(List.of(expelledRecord));

            // when & then
            assertThatThrownBy(() -> expeditionCommandService.joinExpedition(new ExpeditionJoinCommand(expId, userId, null, null)))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPEDITION_EXPELLED);
        }

        @Test
        @DisplayName("실패: 비밀번호가 틀리면 가입할 수 없다.")
        void fail_password_mismatch() {
            // given
            Long userId = 1L;
            Long expId = 100L;
            User user = createMockUser(userId, "tester");

            Expedition expedition = Expedition.builder()
                    .id(expId)
                    .title("Private Exp")
                    .admin(user)
                    .userLimit(10)
                    .currentUsers(1)
                    .visibility(ExpeditionVisibility.PRIVATE)
                    .password("real_pw")
                    .build();

            // Repository Stubbing
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(expeditionRepository.findById(expId)).willReturn(Optional.of(expedition));

            given(expeditionRepository.findAllExpeditionUserByUserId(userId))
                    .willReturn(List.of());

            ExpeditionJoinCommand command = new ExpeditionJoinCommand(
                    expId,
                    userId,
                    ExpeditionVisibility.PRIVATE,
                    "wrong_pw"
            );

            // when & then
            assertThatThrownBy(() -> expeditionCommandService.joinExpedition(command))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPEDITION_PASSWORD_MISMATCH);
        }
    }

    @Nested
    @DisplayName("탐험대 삭제 (deleteExpedition)")
    class DeleteExpeditionTest {
        @Test
        @DisplayName("성공: 방장이 삭제를 요청하면 연관된 모든 데이터가 삭제된다.")
        void success() {
            // given
            Long userId = 1L;
            Long expId = 100L;
            User admin = createMockUser(userId, "admin");
            Expedition expedition = createMockExpedition(expId, "Title", admin);

            given(userRepository.findById(userId)).willReturn(Optional.of(admin));
            given(expeditionRepository.findById(expId)).willReturn(Optional.of(expedition));

            // when
            expeditionCommandService.deleteExpedition(new ExpeditionDeleteCommand(expId, userId));

            // then
            verify(expeditionRepository).deleteAllExpeditionUsersById(expId);
            verify(challengeRepository).deleteAllByExpeditionId(expId);
            verify(expeditionRepository).delete(expedition);
        }

        @Test
        @DisplayName("실패: 방장이 아닌 유저가 삭제를 요청하면 권한 에러가 발생한다.")
        void fail_not_admin() {
            // given
            User admin = createMockUser(1L, "admin");
            User normalUser = createMockUser(2L, "normal");
            Expedition expedition = createMockExpedition(100L, "Title", admin);

            given(userRepository.findById(2L)).willReturn(Optional.of(normalUser));
            given(expeditionRepository.findById(100L)).willReturn(Optional.of(expedition));

            // when & then
            assertThatThrownBy(() -> expeditionCommandService.deleteExpedition(new ExpeditionDeleteCommand(100L, 2L)))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_ADMIN_USER);
        }
    }

    @Nested
    @DisplayName("유저 강퇴 (expelExpedition)")
    class ExpelExpeditionTest {
        @Test
        @DisplayName("성공: 방장이 유저를 강퇴시키면 상태가 EXPELLED로 변경된다.")
        void success() {
            // given
            Long myId = 1L;
            Long targetId = 2L;
            Long expId = 100L;
            User admin = createMockUser(myId, "admin");
            User target = createMockUser(targetId, "target");
            Expedition expedition = createMockExpedition(expId, "Title", admin);
            ExpeditionUser targetEU = mock(ExpeditionUser.class);

            given(userRepository.findById(myId)).willReturn(Optional.of(admin));
            given(userRepository.findById(targetId)).willReturn(Optional.of(target));
            given(expeditionRepository.findById(expId)).willReturn(Optional.of(expedition));
            given(expeditionRepository.findExpeditionUserByUserIdAndId(targetId, expId)).willReturn(Optional.of(targetEU));

            // when
            expeditionCommandService.expelExpedition(new ExpeditionExpelCommand(expId, targetId, myId));

            // then
            verify(targetEU).expelUser();
        }
    }

    @Nested
    @DisplayName("강퇴 취소 (cancelExpelExpedition)")
    class CancelExpelTest {
        @Test
        @DisplayName("성공: 강퇴 취소 시 해당 ExpeditionUser 데이터를 삭제한다.")
        void success() {
            // given
            Long myId = 1L;
            Long targetId = 2L;
            Long expId = 100L;
            User admin = createMockUser(myId, "admin");
            User target = createMockUser(targetId, "target");
            Expedition expedition = createMockExpedition(expId, "Title", admin);
            ExpeditionUser targetEU = createMockExpeditionUser(500L, target, ExpeditionUserStatus.EXPELLED);

            given(userRepository.findById(myId)).willReturn(Optional.of(admin));
            given(userRepository.findById(targetId)).willReturn(Optional.of(target));
            given(expeditionRepository.findById(expId)).willReturn(Optional.of(expedition));
            given(expeditionRepository.findExpeditionUserByUserIdAndId(targetId, expId)).willReturn(Optional.of(targetEU));

            // when
            expeditionCommandService.cancelExpelExpedition(new ExpeditionCancelExpelCommand(expId, targetId, myId));

            // then
            verify(expeditionRepository).deleteExpeditionUser(targetEU);
        }
    }

    @Test
    @DisplayName("탐험대 수정 성공 - 모든 조건 충족 시")
    void modifyExpedition_Success() {
        // given
        ExpeditionModifyCommand command = new ExpeditionModifyCommand(1L, "새 제목", ExpeditionVisibility.PUBLIC, "1234", 10,10L);

        Expedition mockExpedition = mock(Expedition.class);
        User mockUser = mock(User.class);
        lenient().when(mockExpedition.getAdmin()).thenReturn(mockUser);

        given(expeditionRepository.findById(command.expeditionId())).willReturn(Optional.of(mockExpedition));
        given(userRepository.findById(command.userId())).willReturn(Optional.of(mockUser));
        given(mockExpedition.getId()).willReturn(1L);


        // when
        ExpeditionModifyResponse response = expeditionCommandService.modifyExpedition(command);

        // then
        assertNotNull(response);
        assertEquals(1L, response.expeditionId());

        verify(mockExpedition).modify(command.title(), command.visibility(), command.password(), command.userLimit());
    }

    @Test
    @DisplayName("수정 실패 - 방장 권한이 없는 경우")
    void modifyExpedition_Forbidden_Not_Admin() {
        // given
        ExpeditionModifyCommand command = new ExpeditionModifyCommand(1L, "제목", ExpeditionVisibility.PUBLIC, null, 10, 10L);

        Expedition mockExpedition = mock(Expedition.class);
        User mockUser = mock(User.class);
        User mockAdmin = mock(User.class);

        given(expeditionRepository.findById(anyLong())).willReturn(Optional.of(mockExpedition));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(mockUser));


        given(mockExpedition.getAdmin()).willReturn(mockAdmin);


        // when & then
        assertThrows(BusinessException.class, () -> expeditionCommandService.modifyExpedition(command));

        
    }

    
}


