package com.everkeep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.everkeep.controller.dto.SessionResponse;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.security.JwtProvider;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionService {

    private final VerificationTokenService verificationTokenService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public SessionResponse create(String email, String password) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        var user = (User) authentication.getPrincipal();
        var jwt = jwtProvider.generateToken(user);
        var refreshToken = verificationTokenService.create(user, VerificationToken.Action.SESSION_REFRESH);

        return new SessionResponse(jwt, refreshToken, user.getEmail());
    }

    public SessionResponse update(String tokenValue) {
        var oldToken = verificationTokenService.apply(tokenValue, VerificationToken.Action.SESSION_REFRESH);
        var user = oldToken.getUser();
        var jwt = jwtProvider.generateToken(user);
        var newToken = verificationTokenService.create(user, VerificationToken.Action.SESSION_REFRESH);

        return SessionResponse.builder()
                .jwt(jwt)
                .refreshToken(newToken)
                .email(user.getEmail())
                .build();
    }

    public void delete(String token) {
        verificationTokenService.apply(token, VerificationToken.Action.SESSION_REFRESH);
    }
}
