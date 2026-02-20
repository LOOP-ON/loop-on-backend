package com.loopon.routine.infrastructure;

import com.loopon.routine.domain.RoutineReport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface RoutineReportJpaRepository extends JpaRepository<RoutineReport, Long> {
    @Query("""
    SELECT rr
        FROM RoutineReport rr
        WHERE rr.user.id = :userId
        AND rr.journey.id = :journeyId
        AND DATE(rr.createdAt) = :date
        ORDER BY rr.id ASC
""")
    List<RoutineReport> findFirstByUserAndJourneyAndDate(
            @Param("userId") Long userId,
            @Param("journeyId") Long journeyId,
            @Param("date") LocalDate date,
            Pageable pageable
    );
}
