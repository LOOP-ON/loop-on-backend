package com.loopon.friend.application;

import com.loopon.global.exception.BusinessException;
import com.loopon.user.application.dto.response.FriendResponse;
import com.loopon.user.application.service.FriendServiceImpl;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.repository.FriendRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private FriendServiceImpl friendService;

    @Test
    @DisplayName("내 친구 목록 조회: repository 결과가 비어있으면 빈 리스트를 반환한다")
    void getMyFriends_returnsEmptyList_whenNoFriends() {
        // given
        Long me = 1L;
        given(friendRepository.findAcceptedFriendsByUserId(me, FriendStatus.ACCEPTED))
                .willReturn(List.of());

        // when
        List<FriendResponse> result = friendService.getMyFriends(me);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        then(friendRepository).should(times(1))
                .findAcceptedFriendsByUserId(me, FriendStatus.ACCEPTED);
    }

    @Test
    @DisplayName("친구 삭제: 내 친구 관계가 맞으면 deleteByIdAndParticipant를 호출한다")
    void deleteFriend_deletes_whenRelationExists() {
        Long me = 1L;
        Long friendRelationId = 10L;

        given(friendRepository.deleteByIdAndParticipant(friendRelationId, me)).willReturn(1);

        assertDoesNotThrow(() -> friendService.deleteFriend(me, friendRelationId));

        then(friendRepository).should(times(1)).deleteByIdAndParticipant(friendRelationId, me);
        then(friendRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("친구 삭제: 친구 관계가 없거나 권한이 없으면 예외")
    void deleteFriend_throws_whenRelationNotExists() {
        Long me = 1L;
        Long friendRelationId = 10L;

        given(friendRepository.deleteByIdAndParticipant(friendRelationId, me)).willReturn(0);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> friendService.deleteFriend(me, friendRelationId)
        );

        assertTrue(ex.getMessage().contains("삭제"));
        then(friendRepository).should(times(1)).deleteByIdAndParticipant(friendRelationId, me);
        then(friendRepository).shouldHaveNoMoreInteractions();
    }
}