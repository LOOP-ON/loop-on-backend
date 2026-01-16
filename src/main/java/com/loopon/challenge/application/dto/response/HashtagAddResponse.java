package com.loopon.challenge.application.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record HashtagAddResponse(
        List<Long> hashtagIdList
) {}
