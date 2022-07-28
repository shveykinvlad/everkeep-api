package com.everkeep.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@SpringBootTest(classes = AuthenticationService.class)
class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void authenticate() {
        var email = "one@localhost";
        var password = "P4$$w0rd";
        var authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password))).thenReturn(authentication);

        var receivedAuthentication = authenticationService.authenticate(email, password);

        assertEquals(receivedAuthentication, authentication);
    }
}
