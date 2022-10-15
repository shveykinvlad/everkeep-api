package com.everkeep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.everkeep.controller.dto.SessionResponse;
import com.everkeep.model.User;
import com.everkeep.security.JwtProvider;

import static com.everkeep.model.VerificationToken.Action.SESSION_REFRESH;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionService {

    private final VerificationTokenService verificationTokenService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public SessionResponse create(String email, String password) {
        var authentication = authenticationManager.authenticate(getAuthentication(email, password));
        var user = (User) authentication.getPrincipal();
        var authToken = jwtProvider.generateToken(user);
        var refreshToken = verificationTokenService.create(user, SESSION_REFRESH);

        return new SessionResponse(authToken, refreshToken, user.getEmail());
    }

    public SessionResponse update(String refreshToken) {
        var oldRefreshToken = verificationTokenService.apply(refreshToken, SESSION_REFRESH);
        var user = oldRefreshToken.getUser();
        var authToken = jwtProvider.generateToken(user);
        var newRefreshToken = verificationTokenService.create(user, SESSION_REFRESH);

        return SessionResponse.builder()
                .authToken(authToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .build();
    }

    public void delete(String refreshToken) {
        verificationTokenService.apply(refreshToken, SESSION_REFRESH);
    }

    private Authentication getAuthentication(String email, String password) {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
