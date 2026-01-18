package com.loopon.challenge.application.dto.command;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record ChallengePostCommand(
        List<MultipartFile> imageList,

        List<String> hashtagList,

        String content,

        Long journeyId,

        Long expeditionId,

        Long userId
){}