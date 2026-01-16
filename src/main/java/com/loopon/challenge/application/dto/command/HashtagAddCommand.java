package com.loopon.challenge.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record HashtagAddCommand(
        @Size(max = 5, message = "해시태그는 최대 5개까지 가능합니다.")
        List<@NotBlank @Size(max = 10, message = "글자 수는 최대 10자입니다.") String> hashtagList
){}


