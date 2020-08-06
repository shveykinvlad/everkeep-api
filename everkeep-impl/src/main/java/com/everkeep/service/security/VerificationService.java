package com.everkeep.service.security;

import java.time.OffsetDateTime;
import javax.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void create(User user, String token) {
        OffsetDateTime tokenExpiryTime = OffsetDateTime.now().plusDays(TOKEN_EXPIRY_DAYS);
        VerificationToken verificationToken = new VerificationToken()
                .setToken(token)
                .setUser(user)
                .setExpiryTime(tokenExpiryTime);

        verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken get(String verificationToken) {
        return verificationTokenRepository.findByToken(verificationToken)
                .orElseThrow(() -> new EntityNotFoundException("VerificationToken " + verificationToken + " not found"));
    }

    public void validateToken(VerificationToken verificationToken) {
        if (verificationToken == null) {
            throw new VerificationTokenInvalidException("Verification token is null");
        }
        if ((OffsetDateTime.now().isAfter(verificationToken.getExpiryTime()))) {
            throw new VerificationTokenExpirationException("Verification token is expired", verificationToken.getToken());
        }
    }
}
