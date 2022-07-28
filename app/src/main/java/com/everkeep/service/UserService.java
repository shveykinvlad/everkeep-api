package com.everkeep.service;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.everkeep.controller.dto.SessionResponse;
import com.everkeep.exception.UserAlreadyEnabledException;
import com.everkeep.exception.UserAlreadyExistsException;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;
import com.everkeep.service.security.JwtTokenProvider;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    public User get(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email = %s not found".formatted(email)));
    }

    public void register(String email, String password) {
        if (userExists(email)) {
            throw new UserAlreadyExistsException("User already exists", email);
        }
        var user = createUser(email, password);
        var token = verificationTokenService.create(user, VerificationToken.Action.ACCOUNT_CONFIRMATION);

        mailService.sendConfirmationMail(user.getEmail(), token.getValue());
    }

    public void confirm(String tokenValue) {
        var verificationToken = verificationTokenService.apply(tokenValue, VerificationToken.Action.ACCOUNT_CONFIRMATION);
        var user = verificationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);
    }

    public void resendToken(String email) {
        var user = get(email);
        if (user.isEnabled()) {
            throw new UserAlreadyEnabledException("User already enabled", user.getEmail());
        }
        var token = verificationTokenService.create(user, VerificationToken.Action.ACCOUNT_CONFIRMATION);

        mailService.sendConfirmationMail(user.getEmail(), token.getValue());
    }

    public void resetPassword(String email) {
        var user = get(email);
        var token = verificationTokenService.create(user, VerificationToken.Action.PASSWORD_RESET);

        mailService.sendResetPasswordMail(user.getEmail(), token.getValue());
    }

    public void updatePassword(String tokenValue, String password) {
        var verificationToken = verificationTokenService.apply(tokenValue, VerificationToken.Action.PASSWORD_RESET);
        var user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public SessionResponse createSession(String email, String password) {
        var authentication = authenticationService.authenticate(email, password);
        var user = (User) authentication.getPrincipal();
        var jwtToken = jwtTokenProvider.generateToken(user);
        var refreshToken = verificationTokenService.create(user, VerificationToken.Action.SESSION_REFRESH);

        return new SessionResponse(jwtToken, refreshToken.getValue(), user.getEmail());
    }

    public SessionResponse refreshSession(String oldTokenValue) {
        var oldToken = verificationTokenService.apply(oldTokenValue, VerificationToken.Action.SESSION_REFRESH);
        var user = oldToken.getUser();
        var jwt = jwtTokenProvider.generateToken(user);
        var newToken = verificationTokenService.create(user, VerificationToken.Action.SESSION_REFRESH);

        return SessionResponse.builder()
                .jwt(jwt)
                .refreshToken(newToken.getValue())
                .email(user.getEmail())
                .build();
    }

    public void deleteSession(String token) {
        verificationTokenService.apply(token, VerificationToken.Action.SESSION_REFRESH);
    }

    public String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private User createUser(String email, String password) {
        var user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(roleRepository.findByName("ROLE_USER")))
                .build();

        return userRepository.save(user);
    }

}
