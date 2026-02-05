package com.loopon.expedition.application.service;

import com.loopon.challenge.domain.repository.ChallengeRepository;
import com.loopon.expedition.application.converter.ExpeditionConverter;
import com.loopon.expedition.application.dto.command.*;
import com.loopon.expedition.application.dto.response.*;
import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.ExpeditionUser;
import com.loopon.expedition.domain.ExpeditionUserStatus;
import com.loopon.expedition.domain.ExpeditionVisibility;
import com.loopon.expedition.domain.repository.ExpeditionRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ExpeditionCommandService {

    private final ExpeditionRepository expeditionRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    @Transactional
    public ExpeditionPostResponse postExpedition(
            ExpeditionPostCommand commandDto
    ) {
        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkExpeditionLimit(user);

        Expedition expedition = ExpeditionConverter.postExpedition(commandDto, user);
        expeditionRepository.save(expedition);

        ExpeditionUser expeditionUser = ExpeditionUser.builder()
                .user(user)
                .expedition(expedition)
                .status(ExpeditionUserStatus.APPROVED)
                .build();
        expeditionRepository.saveExpeditionUser(expeditionUser);


        return ExpeditionConverter.postExpedition(expedition);
    }

    @Transactional
    public ExpeditionJoinResponse joinExpedition(
            ExpeditionJoinCommand commandDto
    ) {
        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkExpeditionLimit(user);

        Expedition expedition = expeditionRepository.findById(commandDto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));

        checkExpelled(expedition, user);

        checkUserLimit(expedition);

        if (commandDto.expeditionVisibility() == ExpeditionVisibility.PRIVATE) {
            checkExpeditionPassword(expedition, commandDto.password());
        }

        ExpeditionUser expeditionUser = ExpeditionUser.builder()
                .user(user)
                .expedition(expedition)
                .status(ExpeditionUserStatus.APPROVED)
                .build();

        expeditionRepository.saveExpeditionUser(expeditionUser);
        expedition.addToCurrentUsers(1);

        return ExpeditionConverter.joinExpedition(expeditionUser);
    }

    @Transactional
    public ExpeditionWithdrawResponse withdrawExpedition(
            ExpeditionWithdrawCommand commandDto
    ) {
         ExpeditionUser expeditionUser =
                 expeditionRepository.findExpeditionUserByUserIdAndId(commandDto.userId(), commandDto.expeditionId())
                         .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_USER_NOT_FOUND));

        expeditionRepository.deleteExpeditionUser(expeditionUser);
        expedition.addToCurrentUsers(-1);

        return ExpeditionConverter.withdrawExpedition(commandDto.expeditionId());
    }

    @Transactional
    public ExpeditionDeleteResponse deleteExpedition(ExpeditionDeleteCommand commandDto) {

        User user = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Expedition expedition = expeditionRepository.findById(commandDto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));

        checkAdmin(user, expedition);

        String deletedTitle = expedition.getTitle();

        expeditionRepository.deleteAllExpeditionUsersById(expedition.getId());
        challengeRepository.deleteAllByExpeditionId(expedition.getId());

        expeditionRepository.delete(expedition);

        return ExpeditionConverter.deleteExpedition(deletedTitle);
    }


    @Transactional
    public ExpeditionExpelResponse expelExpedition(
            ExpeditionExpelCommand commandDto
    ) {
        User user = userRepository.findById(commandDto.myUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Expedition expedition = expeditionRepository.findById(commandDto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));
        User expelledUser = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkAdmin(user, expedition);

        ExpeditionUser expeditionUser =
                expeditionRepository.findExpeditionUserByUserIdAndId(expelledUser.getId(), expedition.getId())
                        .orElseThrow(()  -> new BusinessException(ErrorCode.EXPEDITION_USER_NOT_FOUND));

        expeditionUser.expelUser();
        expedition.addToCurrentUsers(-1);

        return ExpeditionConverter.expelExpedition(expeditionUser.getId());
    }

    @Transactional
    public ExpeditionCancelExpelResponse cancelExpelExpedition(
            ExpeditionCancelExpelCommand commandDto
    ) {
        User myself = userRepository.findById(commandDto.myUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Expedition expedition = expeditionRepository.findById(commandDto.expeditionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPEDITION_NOT_FOUND));

        checkAdmin(myself, expedition);

        User expelledUser = userRepository.findById(commandDto.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        ExpeditionUser expeditionUser = expeditionRepository.findExpeditionUserByUserIdAndId(expelledUser.getId(), expedition.getId())
                .orElseThrow(()  -> new BusinessException(ErrorCode.EXPEDITION_USER_NOT_FOUND));

        expeditionRepository.deleteExpeditionUser(expeditionUser);

        return ExpeditionConverter.cancelExpelExpedition(expeditionUser.getId());
    }


    // -------------------------- helper methods-------------------------------


    // 사용자의 탐험대 개수 제한
    private void checkExpeditionLimit(User user){
        List<ExpeditionUser> expeditionUserList = expeditionRepository.findAllExpeditionUserByUserId(user.getId());

        int currentCount = 0;

        for (ExpeditionUser expeditionUser : expeditionUserList){
            if (expeditionUser.getStatus().equals(ExpeditionUserStatus.APPROVED)) {
                currentCount++;
            }
        }

        if (currentCount >= 5) {
            throw new BusinessException(ErrorCode.EXPEDITION_ABOVE_LIMIT);
        }
    }

    private void checkExpelled(Expedition expedition, User user) {
        List<ExpeditionUser> expeditionUserList = expeditionRepository.findAllExpeditionUserById(expedition.getId());

        for (ExpeditionUser expeditionUser : expeditionUserList){
            if (expeditionUser.getUser() == user && expeditionUser.getStatus().equals(ExpeditionUserStatus.EXPELLED)) {
                throw new BusinessException(ErrorCode.EXPEDITION_EXPELLED);
            }
        }
    }

    // 탐험대 내 사용자 수 제한
    private void checkUserLimit(Expedition expedition) {
        int userLimit = expedition.getUserLimit();

        int currentUsers = expeditionRepository.countExpeditionUserByExpeditionId(expedition.getId());

        if (currentUsers + 1 > userLimit) {
            throw new BusinessException(ErrorCode.EXPEDITION_USER_ABOVE_LIMIT);
        }
    }


    private void checkExpeditionPassword(Expedition expedition, String password) {
        String expeditionPassword = expedition.getPassword();

        if (!expeditionPassword.equals(password)) {
            throw new BusinessException(ErrorCode.EXPEDITION_PASSWORD_MISMATCH);
        }
    }

    private void checkAdmin(User user, Expedition expedition) {
        if (expedition.getAdmin() != user){
            throw new BusinessException(ErrorCode.NOT_ADMIN_USER);
        }
    }


}
