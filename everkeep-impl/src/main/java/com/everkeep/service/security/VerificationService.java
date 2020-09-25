package com.everkeep.service.security;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.everkeep.enums.TokenAction;
import com.everkeep.exception.security.VerificationTokenExpirationException;
import com.everkeep.exception.security.VerificationTokenInvalidException;
import com.everkeep.model.security.User;
import com.everkeep.model.security.VerificationToken;
import com.everkeep.repository.security.VerificationTokenRepository;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${verificationToken.expiration}")
    private long expirationSeconds;

    public VerificationToken create(User user, TokenAction tokenAction) {
        var tokenValue = UUID.randomUUID().toString();
        var tokenExpiryTime = OffsetDateTime.now().plusSeconds(expirationSeconds);
        var verificationToken = new VerificationToken()
                .setValue(tokenValue)
                .setUser(user)
                .setExpiryTime(tokenExpiryTime)
                .setAction(tokenAction);

        return verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken get(String value, TokenAction action) {
        return verificationTokenRepository.findByValueAndAction(value, action)
                .orElseThrow(() -> new EntityNotFoundException("VerificationToken " + value + " for action " + action + " not found"));
    }

    public VerificationToken utilize(VerificationToken verificationToken) {
        verificationToken.setActive(false);
        return verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken validateAndUtilize(String value, TokenAction active) {
        var verificationToken = get(value, active);
        validateToken(verificationToken);

        return utilize(verificationToken);
    }

    public void validateToken(VerificationToken verificationToken) {
        if (verificationToken == null) {
            throw new VerificationTokenInvalidException("Verification token is null");
        }
        if ((OffsetDateTime.now().isAfter(verificationToken.getExpiryTime()))) {
            throw new VerificationTokenExpirationException("Verification token is expired", verificationToken.getValue());
        }
    }
}
