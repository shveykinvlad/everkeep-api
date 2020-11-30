package com.everkeep.service;

import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.everkeep.AbstractTest;
import com.everkeep.exception.VerificationTokenExpirationException;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.VerificationTokenRepository;

@ContextConfiguration(classes = VerificationService.class)
@TestPropertySource(properties = {"verificationToken.expirationTimeSec = 60"})
class VerificationServiceTest extends AbstractTest {

    private static final String TOKEN_VALUE = "tokenValue";

    @Autowired
    private VerificationService verificationService;
    @Value("${verificationToken.expirationTimeSec}")
    private long expirationTimeSec;
    @MockBean
    private VerificationTokenRepository verificationTokenRepository;
    @Captor
    ArgumentCaptor<VerificationToken> tokenCaptor;

    @AfterEach
    void tearDown() {
        Mockito.reset(verificationTokenRepository);
    }

    @Test
    void create() {
        var user = new User();

        verificationService.create(user, VerificationToken.Action.CONFIRM_ACCOUNT);

        Mockito.verify(verificationTokenRepository).save(tokenCaptor.capture());
        Assertions.assertAll(() -> {
            var actual = tokenCaptor.getValue();
            Assertions.assertEquals(VerificationToken.Action.CONFIRM_ACCOUNT, actual.getAction());
            Assertions.assertEquals(user, actual.getUser());
            Assertions.assertEquals(
                    OffsetDateTime.now().plusSeconds(expirationTimeSec).truncatedTo(ChronoUnit.SECONDS),
                    actual.getExpiryTime().truncatedTo(ChronoUnit.SECONDS));
        });
    }

    @Test
    void get() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var expected = new VerificationToken()
                .setValue(tokenValue)
                .setAction(action);

        when(verificationTokenRepository.findByValueAndAction(tokenValue, action)).thenReturn(Optional.of(expected));
        var actual = verificationService.get(tokenValue, VerificationToken.Action.CONFIRM_ACCOUNT);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getIfNotFound() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;

        when(verificationTokenRepository.findByValueAndAction(tokenValue, action)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> verificationService.get(tokenValue, action));
    }

    @Test
    void validateAndUtilize() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var expiryTime = OffsetDateTime.MAX;
        var token = new VerificationToken()
                .setValue(tokenValue)
                .setAction(action)
                .setExpiryTime(expiryTime);

        when(verificationTokenRepository.findByValueAndAction(tokenValue, action)).thenReturn(Optional.of(token));
        verificationService.validateAndUtilize(tokenValue, action);

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
        var token = new VerificationToken()
                .setValue(tokenValue)
                .setAction(action)
                .setExpiryTime(expiryTime);

        when(verificationTokenRepository.findByValueAndAction(tokenValue, action)).thenReturn(Optional.of(token));
        Assertions.assertThrows(VerificationTokenExpirationException.class,
                () -> verificationService.validateAndUtilize(tokenValue, action));
    }
}
