package com.everkeep.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.everkeep.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByHashValueAndAction(String hashValue, VerificationToken.Action tokenAction);
}
