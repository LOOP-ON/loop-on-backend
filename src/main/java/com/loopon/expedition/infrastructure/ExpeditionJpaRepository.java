package com.loopon.expedition.infrastructure;

import com.loopon.expedition.domain.Expedition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpeditionJpaRepository extends JpaRepository<Expedition, Long> {
    @Override
    Optional<Expedition> findById(Long expeditionId);
}
