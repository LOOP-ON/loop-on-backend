package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagJpaRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByName(String hashtag);
}
