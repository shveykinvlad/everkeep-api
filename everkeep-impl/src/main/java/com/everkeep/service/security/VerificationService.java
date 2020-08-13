package com.everkeep.service.security;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
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

    private static final long TOKEN_EXPIRY_DAYS = 1;

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken create(User user, TokenAction tokenAction) {
        var tokenValue = UUID.randomUUID().toString();
        var tokenExpiryTime = OffsetDateTime.now().plusDays(TOKEN_EXPIRY_DAYS);
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

    public void validateToken(VerificationToken verificationToken) {
        if (verificationToken == null) {
            throw new VerificationTokenInvalidException("Verification token is null");
        }
        if ((OffsetDateTime.now().isAfter(verificationToken.getExpiryTime()))) {
            throw new VerificationTokenExpirationException("Verification token is expired", verificationToken.getValue());
        }
    }
}
