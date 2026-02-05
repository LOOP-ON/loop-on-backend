package com.loopon.journey.domain.repository;

import com.loopon.journey.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
