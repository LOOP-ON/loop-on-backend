package com.loopon.expedition.infrastructure.jpa;

import com.loopon.expedition.domain.ExpeditionUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ExpeditionUserJpaRepository extends JpaRepository<ExpeditionUser, Long> {

    @EntityGraph(attributePaths = {"expedition"})
    List<ExpeditionUser> findAllByUserId(Long userId);

    List<ExpeditionUser> findAllByExpeditionId(Long expeditionId);

    int countByExpeditionId(Long expeditionId);

    Optional<ExpeditionUser> findByUserIdAndExpeditionId(Long userId, Long expeditionId);

    @Query("SELECT eu.expedition.id FROM ExpeditionUser eu " +
            "WHERE eu.user.id = :userId AND eu.expedition.id IN :expeditionIds")
    List<Long> findJoinedExpeditionIds(@Param("userId") Long userId, @Param("expeditionIds") List<Long> expeditionIds);

    void deleteAllByExpeditionId(Long expeditionId);

    @Query("SELECT eu FROM ExpeditionUser eu " +
            "JOIN FETCH eu.user " +
            "WHERE eu.expedition.id = :expeditionId AND eu.status = com.loopon.expedition.domain.ExpeditionUserStatus.APPROVED")
    List<ExpeditionUser> findAllWithUserByExpeditionId(@Param("expeditionId") Long expeditionId);
}
