package com.everkeep.service;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.everkeep.exception.VerificationTokenExpirationException;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.VerificationTokenRepository;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${verificationToken.expirationTimeSec}")
    private long expirationTimeSec;

    public VerificationToken create(User user, VerificationToken.Action tokenAction) {
        var value = UUID.randomUUID().toString();
        var expiryTime = OffsetDateTime.now().plusSeconds(expirationTimeSec);
        var token = VerificationToken.builder()
                .value(value)
                .user(user)
                .expiryTime(expiryTime)
                .action(tokenAction)
                .build();

        return verificationTokenRepository.save(token);
    }

    public VerificationToken get(String value, VerificationToken.Action action) {
        return verificationTokenRepository.findByValueAndAction(value, action)
                .orElseThrow(() -> new EntityNotFoundException("VerificationToken %s for action %s not found"
                        .formatted(value, action)));
    }

    public VerificationToken apply(String value, VerificationToken.Action active) {
        var token = get(value, active);
        validate(token);

        return utilize(token);
    }

    private void validate(VerificationToken token) {
        if (OffsetDateTime.now().isAfter(token.getExpiryTime())) {
            throw new VerificationTokenExpirationException("Verification token is expired", token.getValue());
        }
    }

    private VerificationToken utilize(VerificationToken token) {
        token.setActive(false);
        return verificationTokenRepository.save(token);
    }
}
