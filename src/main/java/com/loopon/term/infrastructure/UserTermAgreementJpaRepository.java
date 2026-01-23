package com.loopon.term.infrastructure;

import com.loopon.term.domain.UserTermAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermAgreementJpaRepository extends JpaRepository<UserTermAgreement, Long> {
}
