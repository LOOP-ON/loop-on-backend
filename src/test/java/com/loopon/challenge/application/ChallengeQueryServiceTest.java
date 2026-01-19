package com.loopon.challenge.application;

import com.loopon.challenge.application.dto.response.ChallengeGetResponse;
import com.loopon.challenge.application.service.ChallengeQueryService;
import com.loopon.challenge.domain.Challenge;
import com.loopon.challenge.domain.ChallengeImage;
import com.loopon.challenge.domain.Hashtag;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.domain.Expedition;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChallengeQueryServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeQueryService challengeQueryService;

    @Test
    @DisplayName("챌린지 상세 조회 성공 테스트")
    void getChallenge_Success() {
        // given
        Long challengeId = 1L;

        Expedition mockExpedition = mock(Expedition.class);
        when(mockExpedition.getId()).thenReturn(100L);

        Challenge mockChallenge = mock(Challenge.class);
        when(mockChallenge.getId()).thenReturn(challengeId);
        when(mockChallenge.getContent()).thenReturn("챌린지 내용입니다.");
        when(mockChallenge.getExpedition()).thenReturn(mockExpedition);

        ChallengeImage image = mock(ChallengeImage.class);
        when(image.getImageUrl()).thenReturn("https://s3.url/image.jpg");

        Hashtag hashtag = mock(Hashtag.class);
        when(hashtag.getName()).thenReturn("갓생");

        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(mockChallenge));
        when(challengeRepository.findAllImageByChallengeId(challengeId)).thenReturn(List.of(image));
        when(challengeRepository.findAllHashtagByChallengeId(challengeId)).thenReturn(List.of(hashtag));

        // when
        ChallengeGetResponse response = challengeQueryService.getChallenge(challengeId);

        // then
        assertNotNull(response);
        assertEquals(1L, response.challengeId());
        assertEquals("챌린지 내용입니다.", response.content());
        assertEquals(1, response.imageList().size());
        assertEquals("https://s3.url/image.jpg", response.imageList().getFirst());
        assertEquals(100L, response.expeditionId());

        verify(challengeRepository, times(1)).findById(challengeId);
    }

    @Test
    @DisplayName("챌린지가 존재하지 않을 때 BusinessException 발생 테스트")
    void getChallenge_NotFound() {
        // given
        Long challengeId = 999L;
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> challengeQueryService.getChallenge(challengeId));

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }
}
