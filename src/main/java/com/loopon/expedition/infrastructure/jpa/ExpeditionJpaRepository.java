package com.loopon.expedition.infrastructure.jpa;

import com.loopon.expedition.domain.Expedition;
import com.loopon.expedition.domain.ExpeditionCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpeditionJpaRepository extends JpaRepository<Expedition, Long> {
    @Override
    Optional<Expedition> findById(Long expeditionId);

    @Query("SELECT eu.expedition FROM ExpeditionUser eu " +
            "JOIN eu.expedition " +
            "WHERE eu.user.id = :userId AND eu.status = 'APPROVED'")
    List<Expedition> findApprovedExpeditionsByUserId(@Param("userId") Long userId);

    Slice<Expedition> findByTitleContainingAndCategoryIn(
            String keyword,
            List<ExpeditionCategory> expeditionCategories,
            Pageable pageable
    );
}
