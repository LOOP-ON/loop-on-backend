package com.loopon.routine.infrastructure;

import com.loopon.routine.domain.RoutineReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface RoutineReportJpaRepository extends JpaRepository<RoutineReport, Long> {
    @Query("""
    select rr
    from RoutineReport rr
    where rr.user.id = :userId
    and rr.journey.id = :journeyId
    and DATE(rr.createdAt) = :date
""")
    Optional<RoutineReport> findByUserAndJourneyAndDate(
            @Param("userId") Long userId,
            @Param("journeyId") Long journeyId,
            @Param("date") LocalDate date
    );
}
