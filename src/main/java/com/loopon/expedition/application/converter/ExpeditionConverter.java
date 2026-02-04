package com.loopon.expedition.application.converter;

import com.loopon.challenge.domain.Challenge;
import com.loopon.expedition.application.dto.command.*;
import com.loopon.expedition.application.dto.request.ExpeditionCancelExpelRequest;
import com.loopon.expedition.application.dto.request.ExpeditionJoinRequest;
import com.loopon.expedition.application.dto.request.ExpeditionPostRequest;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.ExpeditionUser;
import com.loopon.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.loopon.expedition.application.dto.response.ExpeditionGetResponseList.*;

public class ExpeditionConverter {

    public static ExpeditionGetResponse getExpeditions(
            Expedition expedition,
            String adminName,
            Integer currentMembers
    ) {
        return ExpeditionGetResponse.builder()
                .expeditionId(expedition.getId())
                .title(expedition.getTitle())
                .category(expedition.getCategory())
                .capacity(expedition.getUserLimit())
                .visibility(expedition.getVisibility())
                .admin(adminName)
                .currentMembers(currentMembers)
                .build();
    }

    public static ExpeditionGetResponseList getExpeditionList(
            List<ExpeditionGetResponse> responseList
    ) {
        return ExpeditionGetResponseList.builder()
                .expeditionGetResponses(responseList)
                .build();
    }

    public static ExpeditionPostCommand postExpedition(
            ExpeditionPostRequest requestDto,
            Long userId
    ) {
        return ExpeditionPostCommand.builder()
                .title(requestDto.title())
                .category(requestDto.category())
                .capacity(requestDto.capacity())
                .visibility(requestDto.visibility())
                .userId(userId)
                .password(requestDto.password())
                .build();
    }

    public static Expedition postExpedition(
            ExpeditionPostCommand commandDto,
            User user
    ) {
        return Expedition.builder()
                .title(commandDto.title())
                .admin(user)
                .category(commandDto.category())
                .visibility(commandDto.visibility())
                .password(commandDto.password())
                .userLimit(commandDto.capacity())
                .build();
    }

    public static ExpeditionPostResponse postExpedition(
            Expedition expedition
    ) {
        return ExpeditionPostResponse.builder()
                .expeditionId(expedition.getId())
                .build();
    }


    public static ExpeditionJoinCommand joinExpedition(
            ExpeditionJoinRequest requestDto,
            Long userId
    ) {
        return ExpeditionJoinCommand.builder()
                .expeditionId(requestDto.expeditionId())
                .expeditionVisibility(requestDto.expeditionVisibility())
                .password(requestDto.password())
                .userId(userId)
                .build();
    }

    public static ExpeditionJoinResponse joinExpedition(
            ExpeditionUser expeditionUser
    ) {
        return ExpeditionJoinResponse.builder()
                .expeditionUserId(expeditionUser.getId())
                .build();
    }


    public static ExpeditionWithdrawCommand withdrawExpedition(
            Long expeditionId,
            Long userId
    ) {
        return ExpeditionWithdrawCommand.builder()
                .expeditionId(expeditionId)
                .userId(userId)
                .build();
    }

    public static ExpeditionWithdrawResponse withdrawExpedition(
            Long expeditionId
    ) {
        return ExpeditionWithdrawResponse.builder()
                .expeditionId(expeditionId)
                .build();
    }

    public static ExpeditionSearchCommand searchExpedition(
            String keyword,
            List<Boolean> categories,
            Pageable pageable,
            Long userId
    ) {

        return ExpeditionSearchCommand.builder()
                .keyword(keyword)
                .categories(categories)
                .pageable(pageable)
                .userId(userId)
                .build();
    }

    public static ExpeditionSearchResponse searchExpedition(
            Expedition expedition,

            boolean isJoined
    ) {
        return ExpeditionSearchResponse.builder()
                .expeditionId(expedition.getId())
                .admin(expedition.getAdmin().getNickname())
                .category(expedition.getCategory())
                .capacity(expedition.getUserLimit())
                .visibility(expedition.getVisibility())
                .isJoined(isJoined)
                .title(expedition.getTitle())

                .build();
    }

    public static ExpeditionDeleteCommand deleteExpedition(
            Long expeditionId,
            Long userId
    ) {
        return ExpeditionDeleteCommand.builder()
                .expeditionId(expeditionId)
                .userId(userId)
                .build();
    }

    public static ExpeditionDeleteResponse deleteExpedition(
            String title
    ) {
        return ExpeditionDeleteResponse.builder()
                .title(title)
                .build();
    }

    public static ExpeditionUsersCommand usersExpedition(
            Long expeditionId,
            Long userId
    ) {
        return ExpeditionUsersCommand.builder()
                .expeditionId(expeditionId)
                .userId(userId)
                .build();
    }

    public static ExpeditionUsersResponse usersExpedition(
            Boolean isHost, // 방장이 요청한 탐험대 명단 화면이 다르기에
            Integer currentMemberCount,
            Integer maxMemberCount,
            List<ExpeditionUsersResponse.UserInfo> userList
    ) {
        return ExpeditionUsersResponse.builder()
                .isHost(isHost)
                .currentMemberCount(currentMemberCount)
                .maxMemberCount(maxMemberCount)
                .userList(userList)
                .build();
    }

    public static ExpeditionExpelCommand expelExpedition(
            Long expeditionId,
            Long userId,
            Long myUserId
    ) {
        return ExpeditionExpelCommand.builder()
                .expeditionId(expeditionId)
                .userId(userId)
                .myUserId(myUserId)
                .build();
    }

    public static ExpeditionExpelResponse expelExpedition(
            Long expeditionUserId
    ) {
        return ExpeditionExpelResponse.builder()
                .expeditionUserId(expeditionUserId)
                .build();
    }

    public static ExpeditionChallengesCommand challengesExpedition(
            Long expeditionId,
            Long userId,
            Pageable pageable
    ) {
        return ExpeditionChallengesCommand.builder()
                .expeditionId(expeditionId)
                .userId(userId)
                .pageable(pageable)
                .build();
    }

    public static ExpeditionChallengesResponse challengesExpedition(
            Challenge challenge,
            List<String> imageUrls,
            List<String> hashtags,
            Boolean isLiked
    ) {
        return ExpeditionChallengesResponse.builder()
                .challengeId(challenge.getId())
                .journeyNumber(challenge.getJourney().getJourneyOrder())
                .imageUrls(imageUrls)
                .content(challenge.getContent())
                .hashtags(hashtags)
                .createdAt(challenge.getCreatedAt())
                .isLiked(isLiked)
                .build();
    }

    public static ExpeditionCancelExpelCommand cancelExpelExpedition(
            Long expeditionId,
            ExpeditionCancelExpelRequest requestDto,
            Long userId
    ) {
        return ExpeditionCancelExpelCommand.builder()
                .expeditionId(expeditionId)
                .userId(requestDto.userId())
                .myUserId(userId)
                .build();
    }

    public static ExpeditionCancelExpelResponse cancelExpelExpedition(
            Long userId
    ){
        return ExpeditionCancelExpelResponse.builder()
                .userId(userId)
                .build();
    }
}
