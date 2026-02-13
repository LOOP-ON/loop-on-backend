package com.loopon.friend.application;

import com.loopon.global.domain.dto.PageResponse;
import com.loopon.global.domain.dto.SliceResponse;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.application.dto.response.FriendResponse;
import com.loopon.user.application.service.FriendServiceImpl;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.repository.FriendRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
        Pageable pageable = PageRequest.of(0, 10);

        Slice<Friend> emptySlice =
                new SliceImpl<>(List.of(), pageable, false);

        given(friendRepository.getFriendsByUserIdAndStatus(
                me, FriendStatus.ACCEPTED, pageable))
                .willReturn(emptySlice);

        // when
        SliceResponse<FriendResponse> result =
                friendService.getMyFriends(me, pageable);

        // then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertFalse(result.hasNext());

        then(friendRepository).should(times(1))
                .getFriendsByUserIdAndStatus(me, FriendStatus.ACCEPTED, pageable);
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