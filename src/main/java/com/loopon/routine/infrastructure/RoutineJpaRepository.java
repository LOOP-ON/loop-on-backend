package com.loopon.routine.infrastructure;

import com.loopon.journey.domain.Journey;
import com.loopon.routine.domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineJpaRepository extends JpaRepository<Routine, Long> {

    List<Routine> findAllByJourney(Journey journey);

    List<Routine> findByJourney_Id(Long journeyId);
}
