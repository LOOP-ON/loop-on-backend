package com.loopon.routine.infrastructure;

import com.loopon.journey.domain.ProgressStatus;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import org.springframework.data.jpa.repository.JpaRepository;

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
    List<RoutineProgress> findAllByRoutineInAndProgressDateAndStatus(
            List<Routine> routines,
            LocalDate progressDate,
            ProgressStatus status
    );
}
