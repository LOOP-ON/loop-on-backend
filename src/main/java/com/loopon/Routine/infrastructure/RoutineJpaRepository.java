package com.loopon.Routine.infrastructure;

import com.loopon.Routine.domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineJpaRepository extends JpaRepository<Routine, Long> {
}
