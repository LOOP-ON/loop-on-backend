package com.loopon.challenge.application.dto.request;


import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
<<<<<<<< HEAD:src/main/java/com/loopon/challenge/application/dto/request/ChallengePostRequest.java
public record ChallengePostRequest(
        @Size(min = 1, max = 10, message = "사진은 최대 10개까지 가능합니다.")
========
public record ChallengePostCommand (
>>>>>>>> a938c72 (refactor: commandDto, requestDto 분리):src/main/java/com/loopon/challenge/application/dto/command/ChallengePostCommand.java
        List<MultipartFile> imageList,

        List<String> hashtagList,

        String content,

        Long journeyId,

<<<<<<<< HEAD:src/main/java/com/loopon/challenge/application/dto/request/ChallengePostRequest.java
        @NotNull
        Long expeditionId
========
        Long expeditionId,

        Long userId
>>>>>>>> a938c72 (refactor: commandDto, requestDto 분리):src/main/java/com/loopon/challenge/application/dto/command/ChallengePostCommand.java
){}
