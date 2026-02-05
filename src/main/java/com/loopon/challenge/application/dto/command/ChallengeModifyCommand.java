package com.loopon.challenge.application.dto.command;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record ChallengeModifyCommand(
        Long challengeId,

        List<MultipartFile> newImages,
        List<Integer> newSequence,

        List<String> remainImages,
        List<Integer> remainSequence,

        List<String> hashtags,
        String content,
        Long expeditionId
) {
}
