package com.everkeep.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.everkeep.AbstractTest;
import com.everkeep.config.TimeConfig;
import com.everkeep.config.properties.VerificationTokenProperties;
import com.everkeep.exception.VerificationTokenExpirationException;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.VerificationTokenRepository;

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

        verificationTokenService.create(user, VerificationToken.Action.CONFIRM_ACCOUNT);

        Mockito.verify(verificationTokenRepository).save(tokenCaptor.capture());
        var savedToken = tokenCaptor.getValue();
        assertAll("Should return created token",
                () -> assertEquals(VerificationToken.Action.CONFIRM_ACCOUNT, savedToken.getAction()),
                () -> assertEquals(user, savedToken.getUser()),
                () -> assertEquals(OffsetDateTime.now(clock).plusSeconds(duration.getSeconds()).truncatedTo(ChronoUnit.SECONDS),
                        savedToken.getExpiryTime().truncatedTo(ChronoUnit.SECONDS))
        );
    }

    @Test
    void get() {
        var value = UUID.randomUUID().toString();
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var savedToken = VerificationToken.builder()
                .value(value)
                .action(action)
                .build();
        when(verificationTokenRepository.findByValueAndAction(value, action)).thenReturn(Optional.of(savedToken));

        var receivedToken = verificationTokenService.get(value, VerificationToken.Action.CONFIRM_ACCOUNT);

        assertAll("Should return token",
                () -> assertEquals(value, receivedToken.getValue()),
                () -> assertTrue(receivedToken.isActive()),
                () -> assertEquals(action, receivedToken.getAction())
        );
    }

    @Test
    void getNotFound() {
        var value = UUID.randomUUID().toString();
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        when(verificationTokenRepository.findByValueAndAction(value, action)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> verificationTokenService.get(value, action),
                "Should throw an exception if token not found");
    }

    @Test
    void apply() {
        var value = UUID.randomUUID().toString();
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var expiryTime = OffsetDateTime.MAX;
        var savedToken = VerificationToken.builder()
                .value(value)
                .action(action)
                .expiryTime(expiryTime)
                .build();
        when(verificationTokenRepository.findByValueAndAction(value, action)).thenReturn(Optional.of(savedToken));

        verificationTokenService.apply(value, action);

        Mockito.verify(verificationTokenRepository).save(tokenCaptor.capture());
        var appliedToken = tokenCaptor.getValue();
        assertAll("Should return applied token",
                () -> assertEquals(value, appliedToken.getValue()),
                () -> assertEquals(action, appliedToken.getAction()),
                () -> assertFalse(appliedToken.isActive()),
                () -> assertEquals(expiryTime, appliedToken.getExpiryTime())
        );
    }

    @Test
    void applyExpired() {
        var value = UUID.randomUUID().toString();
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var expiryTime = OffsetDateTime.MIN;
        var token = VerificationToken.builder()
                .value(value)
                .action(action)
                .expiryTime(expiryTime)
                .build();
        when(verificationTokenRepository.findByValueAndAction(value, action)).thenReturn(Optional.of(token));

        assertThrows(VerificationTokenExpirationException.class,
                () -> verificationTokenService.apply(value, action),
                "Should throw an exception if the token is expired");
    }
}
