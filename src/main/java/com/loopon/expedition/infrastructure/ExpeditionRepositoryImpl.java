package com.loopon.expedition.infrastructure;

import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.ExpeditionCategory;
import com.loopon.expedition.domain.ExpeditionUser;
import com.loopon.expedition.domain.repository.ExpeditionRepository;
import com.loopon.expedition.infrastructure.jpa.ExpeditionJpaRepository;
import com.loopon.expedition.infrastructure.jpa.ExpeditionUserJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ExpeditionRepositoryImpl implements ExpeditionRepository {

    private final ExpeditionUserJpaRepository expeditionUserJpaRepository;
    private final ExpeditionJpaRepository expeditionJpaRepository;

    @Override
    public List<Expedition> findApprovedExpeditionsByUserId(Long userId) {
        return expeditionJpaRepository.findApprovedExpeditionsByUserId(userId);
    }

    @Override
    public List<ExpeditionUser> findAllExpeditionUserById(Long expeditionId) {
        return expeditionUserJpaRepository.findAllByExpeditionId(expeditionId);
    }

    @Override
    public Long saveExpeditionUser(ExpeditionUser expeditionUser) {
        expeditionUserJpaRepository.save(expeditionUser);
        return expeditionUser.getId();
    }

    @Override
    public Long save(Expedition expedition) {
        expeditionJpaRepository.save(expedition);
        return expedition.getId();
    }

    @Override
    public List<ExpeditionUser> findAllExpeditionUserByUserId(Long userId) {
        return expeditionUserJpaRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<Expedition> findById(Long expeditionId) {
        return expeditionJpaRepository.findById(expeditionId);
    }

    @Override
    public int countExpeditionUserByExpeditionId(Long expeditionId) {
        return expeditionUserJpaRepository.countByExpeditionId(expeditionId);
    }

    @Override
    public Optional<ExpeditionUser> findExpeditionUserByUserIdAndId(Long userId, Long expeditionId) {
        return expeditionUserJpaRepository.findByUserIdAndExpeditionId(userId, expeditionId);
    }

    @Override
    public Slice<Expedition> findByTitleContainingAndCategoryIn(String keyword, List<ExpeditionCategory> categories, Pageable pageable) {
        return expeditionJpaRepository.findByTitleContainingAndCategoryIn(keyword, categories, pageable);
    }

    @Override
    public List<Long> findJoinedExpeditionIds(Long userId, List<Long> expeditionIds) {
        return expeditionUserJpaRepository.findJoinedExpeditionIds(userId, expeditionIds);
    }

    @Override
    public void deleteExpeditionUser(ExpeditionUser expeditionUser) {
        expeditionUserJpaRepository.delete(expeditionUser);
    }

    @Override
    public void delete(Expedition expedition) {
        expeditionJpaRepository.delete(expedition);
    }

    @Override
    public void deleteAllExpeditionUsersById(Long expeditionId) {
        expeditionUserJpaRepository.deleteAllByExpeditionId(expeditionId);
    }

    @Override
    public List<ExpeditionUser> findAllExpeditionUserWithUserById(Long expeditionId) {
        return expeditionUserJpaRepository.findAllWithUserByExpeditionId(expeditionId);
    }
}
