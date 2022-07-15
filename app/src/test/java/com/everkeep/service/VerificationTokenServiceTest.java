package com.everkeep.service;

import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

    private static final String TOKEN_VALUE = "tokenValue";

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

    @AfterEach
    void tearDown() {
        Mockito.reset(verificationTokenRepository);
    }

    @Test
    void create() {
        var user = new User();
        var tokenDuration = Duration.of(30, ChronoUnit.SECONDS);

        when(verificationTokenProperties.expiryDuration()).thenReturn(tokenDuration);
        verificationTokenService.create(user, VerificationToken.Action.CONFIRM_ACCOUNT);

        Mockito.verify(verificationTokenRepository).save(tokenCaptor.capture());
        Assertions.assertAll(() -> {
            var actual = tokenCaptor.getValue();
            Assertions.assertEquals(VerificationToken.Action.CONFIRM_ACCOUNT, actual.getAction());
            Assertions.assertEquals(user, actual.getUser());
            Assertions.assertEquals(
                    OffsetDateTime.now().plusSeconds(tokenDuration.getSeconds()).truncatedTo(ChronoUnit.SECONDS),
                    actual.getExpiryTime().truncatedTo(ChronoUnit.SECONDS));
        });
    }

    @Test
    void get() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var expected = VerificationToken.builder()
                .value(tokenValue)
                .action(action)
                .build();

        when(verificationTokenRepository.findByValueAndAction(tokenValue, action)).thenReturn(Optional.of(expected));
        var actual = verificationTokenService.get(tokenValue, VerificationToken.Action.CONFIRM_ACCOUNT);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getIfNotFound() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;

        when(verificationTokenRepository.findByValueAndAction(tokenValue, action)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> verificationTokenService.get(tokenValue, action));
    }

    @Test
    void validateAndUtilize() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var expiryTime = OffsetDateTime.MAX;
        var token = VerificationToken.builder()
                .value(tokenValue)
                .action(action)
                .expiryTime(expiryTime)
                .build();

        when(verificationTokenRepository.findByValueAndAction(tokenValue, action)).thenReturn(Optional.of(token));
        verificationTokenService.apply(tokenValue, action);

        Mockito.verify(verificationTokenRepository).save(tokenCaptor.capture());
        Assertions.assertAll(() -> {
            var actual = tokenCaptor.getValue();
            Assertions.assertFalse(actual.isActive());
            Assertions.assertEquals(action, actual.getAction());
            Assertions.assertEquals(tokenValue, actual.getValue());
            Assertions.assertEquals(expiryTime, actual.getExpiryTime());
        });
    }

    @Test
    void validateAndUtilizeIfExpired() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var expiryTime = OffsetDateTime.MIN;
        var token = VerificationToken.builder()
                .value(tokenValue)
                .action(action)
                .expiryTime(expiryTime)
                .build();

        when(verificationTokenRepository.findByValueAndAction(tokenValue, action)).thenReturn(Optional.of(token));
        Assertions.assertThrows(VerificationTokenExpirationException.class,
                () -> verificationTokenService.apply(tokenValue, action));
    }
}
