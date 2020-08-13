package com.everkeep.repository.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.everkeep.enums.TokenAction;
import com.everkeep.model.security.User;
import com.everkeep.model.security.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByValueAndAction(String value, TokenAction tokenAction);

    Optional<VerificationToken> findByUser(User user);
}