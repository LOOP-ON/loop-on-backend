package com.loopon.expedition.application.service;

import com.loopon.challenge.domain.Challenge;
import com.loopon.challenge.domain.ChallengeHashtag;
import com.loopon.challenge.domain.ChallengeImage;
import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.application.converter.ExpeditionConverter;
import com.loopon.expedition.application.dto.command.ExpeditionChallengesCommand;
import com.loopon.expedition.application.dto.command.ExpeditionGetCommand;
import com.loopon.expedition.application.dto.command.ExpeditionSearchCommand;
import com.loopon.expedition.application.dto.command.ExpeditionUsersCommand;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.ExpeditionCategory;
import com.loopon.expedition.domain.ExpeditionUser;
import com.loopon.expedition.domain.ExpeditionUserStatus;
import com.loopon.expedition.domain.repository.ExpeditionRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.FriendRepository;
import com.loopon.user.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ExpeditionQueryService {

    private final ExpeditionRepository expeditionRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final ChallengeRepository challengeRepository;

    public ExpeditionGetResponseList getExpeditionList(Long userId) {
        List<Expedition> expeditions = expeditionRepository.findApprovedExpeditionsByUserId(userId);

        List<ExpeditionGetResponseList.ExpeditionGetResponse> responseList = expeditions.stream()
                .map(expedition -> {
                    boolean isAdmin = expedition.getAdmin().getId().equals(userId);

                    return ExpeditionConverter.getExpeditions(expedition, isAdmin);
                })
                .toList();

        return new ExpeditionGetResponseList(responseList);
    }

    @Transactional(readOnly = true)
    public Slice<ExpeditionSearchResponse> searchExpedition(
            ExpeditionSearchCommand commandDto
    ) {

        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<ExpeditionCategory> expeditionCategories = getExpeditionCategories(commandDto);

        Slice<Expedition> expeditions =
                expeditionRepository.findByTitleContainingAndCategoryIn(
                        commandDto.keyword(),
                        expeditionCategories,
                        commandDto.pageable()
                );


        List<Long> joinedExpeditionIds = getJoinedExpeditionIds(expeditions, user);


        boolean notAboveExpeditionLimit = checkExpeditionLimit(user);

        return expeditions.map(expedition -> {

            boolean notJoined = !joinedExpeditionIds.contains(expedition.getId());
            boolean notAboveUserLimit = expedition.getCurrentUsers() < expedition.getUserLimit();
            boolean canJoin = notJoined && notAboveUserLimit && notAboveExpeditionLimit;

            return ExpeditionConverter.searchExpedition(expedition, canJoin);
        });

    }

    @Transactional(readOnly = true)
    public ExpeditionUsersResponse usersExpedition(
            ExpeditionUsersCommand commandDto
    ) {
        Expedition expedition = expeditionRepository.findById(commandDto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));
        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkJoinedToExpedition(expedition, user);

        // 해당 탐험대 명단(탈퇴 인원들 포함)
        List<ExpeditionUser> userList = expeditionRepository.findAllExpeditionUserWithUserById(expedition.getId());

        // 유저의 친구 목록
        Map<Long, FriendStatus> friendIds = getFriendsIds(user.getId());


        List<ExpeditionUsersResponse.UserInfo> userInfoList = new ArrayList<>();
        for (ExpeditionUser expeditionUser : userList) {
            User eUser = expeditionUser.getUser();

            Boolean isMe = eUser.getId().equals(user.getId());
            Boolean isHost = expedition.getAdmin().getId().equals(eUser.getId());
            FriendStatus status = getFriendStatus(friendIds, eUser.getId());

            userInfoList.add(ExpeditionUsersResponse.UserInfo.builder()
                    .userId(eUser.getId())
                    .nickname(eUser.getNickname())
                    .profileImageUrl(eUser.getProfileImageUrl())
                    .isMe(isMe)
                    .isHost(isHost)
                    .friendStatus(status)
                    .expeditionUserStatus(expeditionUser.getStatus())
                    .build());
        }

        Boolean isHost = expedition.getAdmin().getId().equals(user.getId());
        Integer currentUsers = expedition.getCurrentUsers();
        Integer maxUsers = expedition.getUserLimit();


        return ExpeditionConverter.usersExpedition(isHost, currentUsers, maxUsers, userInfoList);
    }


    @Transactional(readOnly = true)
    public Slice<ExpeditionChallengesResponse> challengesExpedition(
            ExpeditionChallengesCommand commandDto
    ) {

        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Expedition expedition = expeditionRepository.findById(commandDto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));

        // Challenge + Journey + User
        Slice<Challenge> challenges = challengeRepository.findAllWithJourneyAndUserByExpeditionId(expedition.getId(), commandDto.pageable());

        return challenges.map(challenge -> {

            List<String> imageUrls = getImageUrls(challenge);
            List<String> hashtags = getHashtags(challenge.getId());
            Boolean isLiked = getIsChallengeLikedByMe(challenge.getId(), user.getId());

            return ExpeditionConverter.challengesExpedition(challenge, imageUrls, hashtags, isLiked);
        });

    }


    @Transactional(readOnly = true)
    public ExpeditionGetResponse getExpedition(
            ExpeditionGetCommand commandDto
    ) {

        Expedition expedition = expeditionRepository.findById(commandDto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));
        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkAdmin(user, expedition);

        return ExpeditionConverter.getExpedition(expedition);
    }

    // ---------------------------- Helper Methods -------------------------------


    // 탐험대 카테고리 가져오기
    private static List<ExpeditionCategory> getExpeditionCategories(ExpeditionSearchCommand commandDto) {

        List<ExpeditionCategory> expeditionCategories = new ArrayList<>();
        ExpeditionCategory[] temp = ExpeditionCategory.values();

        for (int i=0; i<3; i++) {
            if (commandDto.categories().get(i) == true) {
                expeditionCategories.add(temp[i]);
            }
        }

        if (expeditionCategories.isEmpty()) {
            expeditionCategories.add(temp[0]);
            expeditionCategories.add(temp[1]);
            expeditionCategories.add(temp[2]);
        }

        return expeditionCategories;
    }

    // 가입한 탐험대의 id 가져오기
    private List<Long> getJoinedExpeditionIds(Slice<Expedition> expeditions, User user) {
        List<Long> expeditionIds = new ArrayList<>();
        for (Expedition expedition : expeditions) {
            expeditionIds.add(expedition.getId());
        }

        return expeditionRepository.findJoinedExpeditionIds(user.getId(), expeditionIds);
    }

    // 사용자의 탐험대 개수 제한
    private boolean checkExpeditionLimit(User user) {
        List<ExpeditionUser> expeditionUserList = expeditionRepository.findAllExpeditionUserByUserId(user.getId());

        int currentCount = 0;

        for (ExpeditionUser expeditionUser : expeditionUserList){
            if (expeditionUser.getStatus().equals(ExpeditionUserStatus.APPROVED)) {
                currentCount++;
            }
        }

        return currentCount < 5;
    }

    // 유저의 친구들 Map{userId, FriendStatus}
    private Map<Long, FriendStatus> getFriendsIds(Long userId){
        List<Friend> friendList = friendRepository.findAcceptedFriendsByUserId(userId, FriendStatus.ACCEPTED);
        friendList.addAll(friendRepository.findAcceptedFriendsByUserId(userId, FriendStatus.PENDING));

        Map<Long, FriendStatus> friendIds = new HashMap<>();
        for (Friend friend : friendList) {
            friendIds.put(friend.getId(), friend.getStatus());
        }

        return friendIds;
    }

    // 유저의 친구목록에 id가 존재하는지
    private FriendStatus getFriendStatus(Map<Long, FriendStatus> friendIds, Long friendId) {
        if (friendIds.containsKey(friendId)) {
            return friendIds.get(friendId);
        }

        return FriendStatus.NOT_FRIENDS;
    }


    private Boolean getIsChallengeLikedByMe(Long challengeId, Long userId) {
        return challengeRepository.existsChallengeLikeByIdAndUserId(challengeId, userId);
    }

    private List<String> getHashtags(Long challengeId) {
        List<ChallengeHashtag> challengeHashtagList
                = challengeRepository.findAllChallengeHashtagWithHashtagByChallengeId(challengeId);

        List<String> hashtags = new ArrayList<>();
        for (ChallengeHashtag challengeHashtag : challengeHashtagList) {
            hashtags.add(challengeHashtag.getHashtag().getName());
        }

        return hashtags;
    }

    private List<String> getImageUrls(Challenge challenge) {
        List<ChallengeImage> imageList = challenge.getChallengeImages();

        List<String> imageUrls = new ArrayList<>();
        for (ChallengeImage challengeImage : imageList) {
            imageUrls.add(challengeImage.getImageUrl());
        }

        return imageUrls;
    }

    private void checkJoinedToExpedition(Expedition expedition, User user) {
        if (!expeditionRepository.existsExpeditionUserByIdAndUserId(expedition.getId(), user.getId())){
            throw new BusinessException(ErrorCode.EXPEDITION_USER_NOT_FOUND);
        }
    }

    private void checkAdmin(User user, Expedition expedition) {
        if (expedition.getAdmin() != user){
            throw new BusinessException(ErrorCode.NOT_ADMIN_USER);
        }
    }
}
