package com.loopon.routine.infrastructure;

import com.loopon.journey.domain.ProgressStatus;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineProgressJpaRepository extends JpaRepository<RoutineProgress, Long> {
    Optional<RoutineProgress> findByRoutineAndProgressDate(
            Routine routine,
            LocalDate progressDate
    );
    List<RoutineProgress> findAllByRoutineInAndProgressDate(
            List<Routine> routines,
            LocalDate progressDate
    );
    Optional<RoutineProgress> findFirstByRoutineInAndProgressDateBeforeAndStatusOrderByProgressDateAsc(
            List<Routine> routines,
            LocalDate date,
            ProgressStatus status
    );
    List<RoutineProgress> findAllByIdInAndStatus(
            List<Long> ids,
            ProgressStatus status
    );
    @Query("""
    select count(rp) > 0
    from RoutineProgress rp
    where rp.routine.journey.id = :journeyId
      and rp.status = :status
    """)
    boolean existsInProgress(@Param("journeyId") Long journeyId,
                             @Param("status") ProgressStatus status);

    List<RoutineProgress> findByRoutine_Journey_Id(Long journeyId);

    @Query("""
    SELECT rp.progressDate, COUNT(rp)
    FROM RoutineProgress rp
    WHERE rp.status = com.loopon.journey.domain.ProgressStatus.COMPLETED
      AND rp.routine.journey.user.id = :userId
      AND rp.progressDate BETWEEN :startDate AND :endDate
    GROUP BY rp.progressDate
    ORDER BY rp.progressDate
""")
    List<Object[]> findCompletedCountByUserAndMonth(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
    select rp
    from RoutineProgress rp
    join fetch rp.routine r
    where r.journey.id = :journeyId
    and rp.progressDate = :date
""")
    List<RoutineProgress> findByJourneyAndDate(
            @Param("journeyId") Long journeyId,
            @Param("date") LocalDate date
    );
}
