package com.loopon.expedition.domain.repository;

import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.ExpeditionCategory;
import com.loopon.expedition.domain.ExpeditionUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface ExpeditionRepository {

    List<ExpeditionUser> findAllExpeditionUserById(Long expeditionId);

    Long save(Expedition expedition);

    Long saveExpeditionUser(ExpeditionUser expeditionUser);

    List<ExpeditionUser> findAllExpeditionUserByUserId(Long userId);

    Optional<Expedition> findById(Long expeditionId);

    List<Expedition> findApprovedExpeditionsByUserId(Long userId);

    int countExpeditionUserByExpeditionId(Long expeditionId);

    void deleteExpeditionUser(ExpeditionUser expeditionUser);

    Optional<ExpeditionUser> findExpeditionUserByUserIdAndId(Long userId, Long expeditionId);

    Slice<Expedition> findByTitleContainingAndCategoryIn(String keyword, List<ExpeditionCategory> expeditionCategories, Pageable pageable);

    List<Long> findJoinedExpeditionIds(Long userId, List<Long> expeditionIds);

    void delete(Expedition expedition);

    void deleteAllExpeditionUsersById(Long expeditionId);

    List<ExpeditionUser> findAllExpeditionUserWithUserById(Long expeditionId);

    Boolean existsExpeditionUserByIdAndUserId(Long expeditionId, Long userId);
}
