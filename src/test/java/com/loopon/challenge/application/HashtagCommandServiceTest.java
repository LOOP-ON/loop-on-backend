package com.loopon.challenge.application;

import com.loopon.challenge.application.converter.HashtagConverter;
import com.loopon.challenge.application.dto.command.HashtagAddCommand;
import com.loopon.challenge.application.dto.response.HashtagAddResponse;
import com.loopon.challenge.application.service.ChallengeCommandService;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashtagCommandServiceTest {

    @InjectMocks
    private ChallengeCommandService challengeCommandService;

    @Mock
    private ChallengeRepository challengeRepository;

    HashtagAddCommand dto;

    @BeforeEach
    void setUp() {
        List<String> hashtagList = new ArrayList<>();
        hashtagList.add("해시태그");

        dto = HashtagAddCommand.builder()
                .hashtagList(hashtagList)
                .build();
    }

    @Test
    @DisplayName("해시태그 추가 등록 성공")
    void postHashtag_success() {

        // given
        given(challengeRepository.findAllHashtagByNameIn(dto.hashtagList()))
                .willReturn(new ArrayList<>());

        // when
        HashtagAddResponse response = challengeCommandService.addHashtags(dto);

        // then
        assertNotNull(response);
        verify(challengeRepository).saveAllHashtags(anyList()); // 추가 등록
    }



    @Test
    @DisplayName("해시태그 중복 등록 성공")
    void postHashtag_alreadyExists() {
        // given
        given(challengeRepository.findAllHashtagByNameIn(dto.hashtagList()))
                .willReturn(HashtagConverter.addHashtag(dto));

        // when
        HashtagAddResponse response = challengeCommandService.addHashtags(dto);

        // then
        assertNotNull(response);
    }
}
