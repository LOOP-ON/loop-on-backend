package com.loopon.challenge.application;

import com.loopon.challenge.application.dto.command.ChallengePostCommand;
import com.loopon.challenge.application.dto.response.ChallengePostResponse;
import com.loopon.challenge.application.service.ChallengeCommandService;
import com.loopon.challenge.domain.Challenge;
import com.loopon.challenge.domain.ChallengeHashtag;
import com.loopon.challenge.domain.ChallengeImage;
import com.loopon.challenge.domain.Hashtag;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.infrastructure.ExpeditionJpaRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.infrastructure.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import com.loopon.challenge.application.service.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeCommandServiceTest {

    @InjectMocks
    private ChallengeCommandService challengeCommandService;

    @Mock private ChallengeRepository challengeRepository;
    @Mock private JourneyJpaRepository journeyJpaRepository;
    @Mock private UserJpaRepository userJpaRepository;
    @Mock private ExpeditionJpaRepository expeditionJpaRepository;
    @Mock private S3Service s3Service;

    ChallengePostCommand command;

    Long userId = 1L;
    Long journeyId = 2L;
    Long expeditionId = 3L;

    @BeforeEach
    void setUp() {
        command = ChallengePostCommand.builder()
                .userId(userId)
                .journeyId(journeyId)
                .expeditionId(expeditionId)
                .hashtagList(List.of("해시태그1"))
                .imageList(List.of(mock(MultipartFile.class)))
                .build();

    }
    @Test
    @DisplayName("챌린지 등록 성공 테스트")
    void postChallenge_Success() {

        // given
        given(challengeRepository.existsByJourneyId(journeyId)).willReturn(false);
        given(journeyJpaRepository.findById(journeyId)).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(expeditionJpaRepository.findById(expeditionId)).willReturn(Optional.of(mock(Expedition.class)));
        given(s3Service.uploadFiles(any())).willReturn(List.of("https://s3.url/image.jpg"));
        given(challengeRepository.findAllHashtagByNameIn(anyList())).willReturn(List.of(mock(Hashtag.class)));

        // when
        ChallengePostResponse response = challengeCommandService.postChallenge(command);

        // then
        assertNotNull(response);
        verify(s3Service, times(1)).uploadFiles(any());
    }


    @Test
    @DisplayName("빈 해시태그 리스트 성공 테스트")
    void postChallenge_Success_WhenHashtagListIsNull() {

        // given

        ChallengePostCommand nullCommand = ChallengePostCommand.builder()
                .userId(userId)
                .journeyId(journeyId)
                .expeditionId(expeditionId)
                .hashtagList(null)
                .imageList(List.of(mock(MultipartFile.class)))
                .build();

        given(challengeRepository.existsByJourneyId(journeyId)).willReturn(false);
        given(journeyJpaRepository.findById(journeyId)).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(expeditionJpaRepository.findById(expeditionId)).willReturn(Optional.of(mock(Expedition.class)));
        given(s3Service.uploadFiles(any())).willReturn(List.of("https://s3.url/image.jpg"));
        given(challengeRepository.findAllHashtagByNameIn(Collections.emptyList()))
                .willReturn(Collections.emptyList());

        // when
        ChallengePostResponse response = challengeCommandService.postChallenge(nullCommand);

        // then
        assertNotNull(response);

        // 해시태그 저장 관련 로직이 실행되지 않았거나 빈 리스트로 실행되었는지 검증
        verify(challengeRepository, never()).saveAllHashtags(anyList());
        verify(challengeRepository, never()).saveChallengeHashtag(any());

        verify(challengeRepository, times(1)).save(any(Challenge.class));

    }

    @Test
    @DisplayName("이미 챌린지가 존재하는 여정일 경우 예외 발생")
    void postChallenge_Fail_AlreadyExists() {
        // given
        ChallengePostCommand command = ChallengePostCommand.builder()
                .userId(1L)
                .journeyId(1L)
                .expeditionId(1L)
                .content("내용")
                .hashtagList(List.of("태그1"))
                .imageList(List.of(mock(MultipartFile.class)))
                .build();
        given(challengeRepository.existsByJourneyId(anyLong())).willReturn(true);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> challengeCommandService.postChallenge(command));

        assertEquals(ErrorCode.CHALLENGE_ALREADY_EXISTS, exception.getErrorCode());
    }


    @Test
    @DisplayName("존재하지 않는 사용자일 경우 예외 발생")
    void user_NotFound_Fail() {

        // given
        given(challengeRepository.existsByJourneyId(anyLong())).willReturn(false);
        given(journeyJpaRepository.findById(anyLong())).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> challengeCommandService.postChallenge(command));
    }

    @Test
    @DisplayName("존재하지 않는 여정일 경우 예외 발생")
    void journey_NotFound_Fail() {
        // given
        given(challengeRepository.existsByJourneyId(anyLong())).willReturn(false);
        given(journeyJpaRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> challengeCommandService.postChallenge(command));
    }

    @Test
    @DisplayName("존재하지 않는 탐험일 경우 예외 발생")
    void expedition_NotFound_Fail() {
        // given
        given(challengeRepository.existsByJourneyId(anyLong())).willReturn(false);
        given(journeyJpaRepository.findById(anyLong())).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
        given(expeditionJpaRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> challengeCommandService.postChallenge(command));
    }

    @Test
    @DisplayName("S3 업로드 메서드가 정상 호출되는지 확인")
    void s3_Upload_Verify() {
        // given
        given(challengeRepository.existsByJourneyId(journeyId)).willReturn(false);
        given(journeyJpaRepository.findById(journeyId)).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(expeditionJpaRepository.findById(expeditionId)).willReturn(Optional.of(mock(Expedition.class)));
        given(s3Service.uploadFiles(any())).willReturn(List.of("https://s3.url/image.jpg"));
        given(challengeRepository.findAllHashtagByNameIn(anyList())).willReturn(List.of(mock(Hashtag.class)));


        // when
        challengeCommandService.postChallenge(command);

        // then
        // s3Service.uploadFiles가 command에 담긴 이미지 리스트를 인자로 한 번 호출되었는지 검증
        verify(s3Service, times(1)).uploadFiles(command.imageList());
    }

    @Test
    @DisplayName("챌린지, 해시태그, 이미지가 각각 저장소에 저장되는지 확인")
    void entities_Save_Verify() {
        // given
        given(challengeRepository.existsByJourneyId(journeyId)).willReturn(false);
        given(journeyJpaRepository.findById(journeyId)).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(expeditionJpaRepository.findById(expeditionId)).willReturn(Optional.of(mock(Expedition.class)));
        given(s3Service.uploadFiles(any())).willReturn(List.of("https://s3.url/image.jpg"));
        given(challengeRepository.findAllHashtagByNameIn(anyList())).willReturn(List.of(mock(Hashtag.class)));

        // when
        challengeCommandService.postChallenge(command);

        // then
        verify(challengeRepository, times(1)).save(any(Challenge.class));
        verify(challengeRepository, atLeastOnce()).saveChallengeHashtag(any(ChallengeHashtag.class));
        verify(challengeRepository, atLeastOnce()).saveChallengeImage(any(ChallengeImage.class));
    }



}