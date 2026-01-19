package com.loopon.friend.application;

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
    @DisplayName("친구 삭제: 내 친구 관계가 맞으면 deleteById를 호출한다")
    void deleteFriend_deletes_whenRelationExists() {
        // given
        Long me = 1L;
        Long friendRelationId = 10L; // Friend 엔티티의 id (친구 관계 row id)

        given(friendRepository.existsByIdAndUserId(friendRelationId, me)).willReturn(true);

        // when
        assertDoesNotThrow(() -> friendService.deleteFriend(me, friendRelationId));

        // then
        then(friendRepository).should(times(1)).existsByIdAndUserId(friendRelationId, me);
        then(friendRepository).should(times(1)).deleteById(friendRelationId);
    }

    @Test
    @DisplayName("친구 삭제: 친구 관계가 없거나 권한이 없으면 예외를 던지고 deleteById를 호출하지 않는다")
    void deleteFriend_throws_whenRelationNotExists() {
        // given
        Long me = 1L;
        Long friendRelationId = 10L;

        given(friendRepository.existsByIdAndUserId(friendRelationId, me)).willReturn(false);

        // when
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> friendService.deleteFriend(me, friendRelationId)
        );

        // then
        assertTrue(ex.getMessage().contains("삭제"));
        then(friendRepository).should(times(1)).existsByIdAndUserId(friendRelationId, me);
        then(friendRepository).should(never()).deleteById(anyLong());
    }
}
