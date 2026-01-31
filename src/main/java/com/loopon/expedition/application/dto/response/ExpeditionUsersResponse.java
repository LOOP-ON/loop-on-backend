package com.loopon.expedition.application.dto.response;

import com.loopon.user.domain.FriendStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record ExpeditionUsersResponse(
        Boolean isHost, // 방장이 요청한 탐험대 명단 화면이 다르기에
        Integer currentMemberCount,
        Integer maxMemberCount,
        List<UserInfo> userList
) {
    @Builder
    public record UserInfo(
            Long userId,
            String nickname,
            String profileImageUrl,
            Boolean isMe,
            Boolean isHost,
            FriendStatus friendStatus
    ) {}
}

