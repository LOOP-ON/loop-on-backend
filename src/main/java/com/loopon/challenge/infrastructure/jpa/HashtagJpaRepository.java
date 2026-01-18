package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashtagJpaRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByName(String hashtag);

    List<Hashtag> findAllByNameIn(List<String> nameList);
}
