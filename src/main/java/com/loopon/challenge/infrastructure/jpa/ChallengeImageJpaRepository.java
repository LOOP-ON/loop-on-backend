package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.ChallengeImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeImageJpaRepository extends JpaRepository<ChallengeImage, Long> {
}
