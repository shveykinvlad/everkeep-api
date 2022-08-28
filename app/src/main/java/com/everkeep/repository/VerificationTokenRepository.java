package com.everkeep.repository;

import java.util.Optional;

import com.everkeep.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByHashValueAndAction(String hashValue, VerificationToken.Action tokenAction);
}
