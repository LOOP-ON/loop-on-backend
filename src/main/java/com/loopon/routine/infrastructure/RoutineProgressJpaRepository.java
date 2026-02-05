package com.loopon.routine.infrastructure;

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
}
