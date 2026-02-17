package com.loopon.term.infrastructure;

import com.loopon.term.domain.Term;
import com.loopon.term.domain.UserTermAgreement;
import com.loopon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserTermAgreementJpaRepository extends JpaRepository<UserTermAgreement, Long> {

    @Query("SELECT uta FROM UserTermAgreement uta WHERE uta.user = :user AND uta.term = :term")
    Optional<UserTermAgreement> findByUserAndTerm(User user, Term term);
}
