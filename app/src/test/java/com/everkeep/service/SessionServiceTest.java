package com.everkeep.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.security.JwtProvider;

import java.util.UUID;

import static com.everkeep.utils.DigestUtils.sha256Hex;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SessionService.class)
class SessionServiceTest {

    @Autowired
    private SessionService sessionService;
    @MockBean
    private VerificationTokenService verificationTokenService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void create() {
        var email = "erebus@localhost";
        var password = "E1ghthP4$$";
        var jwt = "jwt";
        var user = User.builder()
                .email(email)
                .password(password)
                .build();
        var tokenValue = UUID.randomUUID().toString();
        var action = VerificationToken.Action.SESSION_REFRESH;
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)))
                .thenReturn(new TestingAuthenticationToken(user, password));
        when(jwtProvider.generateToken(user)).thenReturn(jwt);
        when(verificationTokenService.create(user, action)).thenReturn(tokenValue);

        var sessionResponse = sessionService.create(email, password);

        assertAll("Should return access token",
                () -> assertEquals(jwt, sessionResponse.jwt()),
                () -> assertEquals(tokenValue, sessionResponse.refreshToken()),
                () -> assertEquals(email, sessionResponse.email())
        );
    }

    @Test
    void update() {
        var user = new User();
        var jwt = "jwt";
        var action = VerificationToken.Action.SESSION_REFRESH;
        var oldTokenValue = UUID.randomUUID().toString();
        var oldToken = VerificationToken.builder()
                .hashValue(sha256Hex(oldTokenValue))
                .action(VerificationToken.Action.SESSION_REFRESH)
                .user(user)
                .build();
        var newTokenValue = UUID.randomUUID().toString();
        var newToken = VerificationToken.builder()
                .hashValue(sha256Hex(newTokenValue))
                .action(VerificationToken.Action.SESSION_REFRESH)
                .user(user)
                .build();
        when(verificationTokenService.apply(oldTokenValue, action)).thenReturn(oldToken);
        when(jwtProvider.generateToken(user)).thenReturn(jwt);
        when(verificationTokenService.create(user, action)).thenReturn(newTokenValue);

        var sessionResponse = sessionService.update(oldTokenValue);

        assertAll("Should refresh access token",
                () -> assertEquals(jwt, sessionResponse.jwt()),
                () -> assertEquals(newTokenValue, sessionResponse.refreshToken()),
                () -> assertEquals(newToken.getUser().getEmail(), sessionResponse.email()));
    }

    @Test
    void delete() {
        var tokenValue = UUID.randomUUID().toString();

        sessionService.delete(tokenValue);

        verify(verificationTokenService).apply(tokenValue, VerificationToken.Action.SESSION_REFRESH);
    }
}
