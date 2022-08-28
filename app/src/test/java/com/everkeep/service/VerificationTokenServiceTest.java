package com.everkeep.service;

import static com.everkeep.utils.DigestUtils.sha256Hex;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import com.everkeep.AbstractTest;
import com.everkeep.config.TimeConfig;
import com.everkeep.config.properties.VerificationTokenProperties;
import com.everkeep.exception.VerificationTokenExpiredException;
import com.everkeep.exception.VerificationTokenNotFoundException;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {
        VerificationTokenService.class,
        TimeConfig.class
})
class VerificationTokenServiceTest extends AbstractTest {

    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private Clock clock;
    @MockBean
    private VerificationTokenProperties verificationTokenProperties;
    @MockBean
    private VerificationTokenRepository verificationTokenRepository;
    @Captor
    private ArgumentCaptor<VerificationToken> tokenCaptor;

    @Test
    void create() {
        var user = new User();
        var duration = Duration.of(30, ChronoUnit.SECONDS);
        when(verificationTokenProperties.expiryDuration()).thenReturn(duration);

        verificationTokenService.create(user, VerificationToken.Action.ACCOUNT_CONFIRMATION);

        Mockito.verify(verificationTokenRepository).save(tokenCaptor.capture());
        var savedToken = tokenCaptor.getValue();
        assertAll("Should save token and return the value",
                () -> assertEquals(VerificationToken.Action.ACCOUNT_CONFIRMATION, savedToken.getAction()),
                () -> assertEquals(user, savedToken.getUser()),
                () -> assertEquals(OffsetDateTime.now(clock).plusSeconds(duration.getSeconds()).truncatedTo(ChronoUnit.SECONDS),
                        savedToken.getExpiryTime().truncatedTo(ChronoUnit.SECONDS))
        );
    }

    @Test
    void get() {
        var value = UUID.randomUUID().toString();
        var action = VerificationToken.Action.ACCOUNT_CONFIRMATION;
        var savedToken = VerificationToken.builder()
                .hashValue(sha256Hex(value))
                .action(action)
                .build();
        when(verificationTokenRepository.findByHashValueAndAction(sha256Hex(value), action))
                .thenReturn(Optional.of(savedToken));

        var receivedToken = verificationTokenService.get(value, VerificationToken.Action.ACCOUNT_CONFIRMATION);

        assertAll("Should return token",
                () -> assertEquals(sha256Hex(value), receivedToken.getHashValue()),
                () -> assertTrue(receivedToken.isActive()),
                () -> assertEquals(action, receivedToken.getAction())
        );
    }

    @Test
    void getNotFound() {
        var value = UUID.randomUUID().toString();
        var action = VerificationToken.Action.ACCOUNT_CONFIRMATION;
        when(verificationTokenRepository.findByHashValueAndAction(sha256Hex(value), action))
                .thenReturn(Optional.empty());

        assertThrows(VerificationTokenNotFoundException.class,
                () -> verificationTokenService.get(value, action),
                "Should throw an exception if token not found");
    }

    @Test
    void apply() {
        var value = UUID.randomUUID().toString();
        var action = VerificationToken.Action.ACCOUNT_CONFIRMATION;
        var expiryTime = OffsetDateTime.MAX;
        var savedToken = VerificationToken.builder()
                .hashValue(sha256Hex(value))
                .action(action)
                .expiryTime(expiryTime)
                .build();
        when(verificationTokenRepository.findByHashValueAndAction(sha256Hex(value), action))
                .thenReturn(Optional.of(savedToken));

        verificationTokenService.apply(value, action);

        Mockito.verify(verificationTokenRepository).save(tokenCaptor.capture());
        var appliedToken = tokenCaptor.getValue();
        assertAll("Should return applied token",
                () -> assertEquals(sha256Hex(value), appliedToken.getHashValue()),
                () -> assertEquals(action, appliedToken.getAction()),
                () -> assertFalse(appliedToken.isActive()),
                () -> assertEquals(expiryTime, appliedToken.getExpiryTime())
        );
    }

    @Test
    void applyExpired() {
        var value = UUID.randomUUID().toString();
        var action = VerificationToken.Action.ACCOUNT_CONFIRMATION;
        var expiryTime = OffsetDateTime.MIN;
        var token = VerificationToken.builder()
                .hashValue(sha256Hex(value))
                .action(action)
                .expiryTime(expiryTime)
                .user(User.builder()
                        .email("hyacinthus@localhost")
                        .build())
                .build();
        when(verificationTokenRepository.findByHashValueAndAction(sha256Hex(value), action))
                .thenReturn(Optional.of(token));

        assertThrows(VerificationTokenExpiredException.class,
                () -> verificationTokenService.apply(value, action),
                "Should throw an exception if the token is expired");
    }
}
