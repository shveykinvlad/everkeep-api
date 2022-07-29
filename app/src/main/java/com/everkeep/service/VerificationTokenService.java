package com.everkeep.service;

import static com.everkeep.utils.DigestUtils.sha256Hex;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.everkeep.config.properties.VerificationTokenProperties;
import com.everkeep.exception.VerificationTokenExpiredException;
import com.everkeep.exception.VerificationTokenNotFoundException;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.VerificationTokenRepository;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationTokenProperties verificationTokenProperties;
    private final Clock clock;

    public String create(User user, VerificationToken.Action tokenAction) {
        var value = UUID.randomUUID().toString();
        var expiryTime = OffsetDateTime.now(clock)
                .plusSeconds(verificationTokenProperties.expiryDuration().getSeconds());
        var token = VerificationToken.builder()
                .hashValue(sha256Hex(value))
                .user(user)
                .expiryTime(expiryTime)
                .action(tokenAction)
                .build();
        verificationTokenRepository.save(token);

        return value;
    }

    public VerificationToken get(String value, VerificationToken.Action action) {
        return verificationTokenRepository.findByHashValueAndAction(sha256Hex(value), action)
                .orElseThrow(() -> new VerificationTokenNotFoundException("VerificationToken %s for action %s not found"
                        .formatted(value, action)));
    }

    public VerificationToken apply(String value, VerificationToken.Action active) {
        var token = get(value, active);
        validate(token);

        return utilize(token);
    }

    private void validate(VerificationToken token) {
        if (OffsetDateTime.now().isAfter(token.getExpiryTime())) {
            throw new VerificationTokenExpiredException("Verification token is expired", token.getHashValue(), token.getUser().getEmail());
        }
    }

    private VerificationToken utilize(VerificationToken token) {
        token.setActive(false);
        return verificationTokenRepository.save(token);
    }
}
