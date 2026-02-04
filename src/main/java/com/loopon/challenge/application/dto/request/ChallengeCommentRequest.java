package com.loopon.challenge.application.dto.request;

public record ChallengeCommentRequest(
        String content,
        Long parentId
) {}
