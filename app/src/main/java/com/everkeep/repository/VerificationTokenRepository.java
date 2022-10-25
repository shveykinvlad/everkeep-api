package com.everkeep.repository;

import com.everkeep.model.VerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByHashValueAndAction(String hashValue, VerificationToken.Action tokenAction);
}
