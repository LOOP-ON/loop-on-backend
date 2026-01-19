package com.loopon.challenge.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record ChallengePostRequest(
        @Size(min = 1, max = 10, message = "사진은 최대 10개까지 가능합니다.")
        List<MultipartFile> imageList,

        @Size(max = 5, message = "해시태그는 최대 5개까지 가능합니다.")
        List<@NotBlank @Size(max = 10, message = "글자 수는 최대 10자입니다.") String> hashtagList,

        @NotBlank
        @Size(max = 500, message = "글자 수는 최대 500자입니다")
        String content,

        @NotNull
        Long journeyId,

        @NotNull
        Long expeditionId
){}
