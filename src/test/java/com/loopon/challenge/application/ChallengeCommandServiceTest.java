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
import com.loopon.global.security.principal.PrincipalDetails;
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
    PrincipalDetails principalDetails;

    Long userId = 1L;
    Long journeyId = 2L;
    Long expeditionId = 3L;

    @BeforeEach
    void setUp() {
        command = ChallengePostCommand.builder()
                .journeyId(journeyId)
                .expeditionId(expeditionId)
                .hashtagList(List.of("해시태그1"))
                .imageList(List.of(mock(MultipartFile.class)))
                .build();

        principalDetails = mock(PrincipalDetails.class);
    }
    @Test
    @DisplayName("챌린지 등록 성공 테스트")
    void postChallenge_Success() {

        // given
        given(principalDetails.getUserId()).willReturn(userId);
        given(challengeRepository.existsByJourneyId(journeyId)).willReturn(false);
        given(journeyJpaRepository.findById(journeyId)).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(expeditionJpaRepository.findById(expeditionId)).willReturn(Optional.of(mock(Expedition.class)));
        given(s3Service.uploadFiles(any())).willReturn(List.of("https://s3.url/image.jpg"));
        given(challengeRepository.findHashtagByName("해시태그1")).willReturn(Optional.of(mock(Hashtag.class)));

        // when
        ChallengePostResponse response = challengeCommandService.postChallenge(command, principalDetails);

        // then
        assertNotNull(response);
        verify(s3Service, times(1)).uploadFiles(any());
    }

    @Test
    @DisplayName("이미 챌린지가 존재하는 여정일 경우 예외 발생")
    void postChallenge_Fail_AlreadyExists() {
        // given
        ChallengePostCommand command = ChallengePostCommand.builder()
                .journeyId(1L)
                .expeditionId(1L)
                .content("내용")
                .hashtagList(List.of("태그1"))
                .imageList(List.of(mock(MultipartFile.class)))
                .build();
        given(challengeRepository.existsByJourneyId(anyLong())).willReturn(true);

        // when & then
        BusinessException exception =
                assertThrows(BusinessException.class, () -> {
            challengeCommandService.postChallenge(command, principalDetails);
        });

        assertEquals(ErrorCode.CHALLENGE_ALREADY_EXISTS, exception.getErrorCode());
    }


    @Test
    @DisplayName("존재하지 않는 사용자일 경우 예외 발생")
    void user_NotFound_Fail() {
        // given
        given(principalDetails.getUserId()).willReturn(userId);
        given(challengeRepository.existsByJourneyId(anyLong())).willReturn(false);
        given(journeyJpaRepository.findById(anyLong())).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> challengeCommandService.postChallenge(command, principalDetails));
    }

    @Test
    @DisplayName("존재하지 않는 여정일 경우 예외 발생")
    void journey_NotFound_Fail() {
        // given
        given(challengeRepository.existsByJourneyId(anyLong())).willReturn(false);
        given(journeyJpaRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> challengeCommandService.postChallenge(command, principalDetails));
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
        assertThrows(BusinessException.class, () -> challengeCommandService.postChallenge(command, principalDetails));
    }

    @Test
    @DisplayName("S3 업로드 메서드가 정상 호출되는지 확인")
    void s3_Upload_Verify() {
        // given
        given(principalDetails.getUserId()).willReturn(userId);
        given(challengeRepository.existsByJourneyId(journeyId)).willReturn(false);
        given(journeyJpaRepository.findById(journeyId)).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(expeditionJpaRepository.findById(expeditionId)).willReturn(Optional.of(mock(Expedition.class)));
        given(s3Service.uploadFiles(any())).willReturn(List.of("https://s3.url/image.jpg"));
        given(challengeRepository.findHashtagByName("해시태그1")).willReturn(Optional.of(mock(Hashtag.class)));


        // when
        challengeCommandService.postChallenge(command, principalDetails);

        // then
        // s3Service.uploadFiles가 command에 담긴 이미지 리스트를 인자로 한 번 호출되었는지 검증
        verify(s3Service, times(1)).uploadFiles(command.imageList());
    }

    @Test
    @DisplayName("챌린지, 해시태그, 이미지가 각각 저장소에 저장되는지 확인")
    void entities_Save_Verify() {
        // given
        given(principalDetails.getUserId()).willReturn(userId);
        given(challengeRepository.existsByJourneyId(journeyId)).willReturn(false);
        given(journeyJpaRepository.findById(journeyId)).willReturn(Optional.of(mock(Journey.class)));
        given(userJpaRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(expeditionJpaRepository.findById(expeditionId)).willReturn(Optional.of(mock(Expedition.class)));
        given(s3Service.uploadFiles(any())).willReturn(List.of("https://s3.url/image.jpg"));
        given(challengeRepository.findHashtagByName(anyString())).willReturn(Optional.of(mock(Hashtag.class)));

        // when
        challengeCommandService.postChallenge(command, principalDetails);

        // then
        verify(challengeRepository, times(1)).save(any(Challenge.class));
        verify(challengeRepository, atLeastOnce()).saveChallengeHashtag(any(ChallengeHashtag.class));
        verify(challengeRepository, atLeastOnce()).saveChallengeImage(any(ChallengeImage.class));
    }



}