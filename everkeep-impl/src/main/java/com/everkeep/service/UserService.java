package com.everkeep.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.everkeep.config.security.JwtTokenProvider;
import com.everkeep.dto.AuthResponse;
import com.everkeep.exception.UserAlreadyEnabledException;
import com.everkeep.exception.UserAlreadyExistsException;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final MailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email = " + email + " not found"));
    }

    public void register(String email, String password) {
        if (userExists(email)) {
            throw new UserAlreadyExistsException("User already exists", email);
        }
        var user = createUser(email, password);
        var token = verificationService.create(user, VerificationToken.Action.CONFIRM_ACCOUNT);

        mailSender.sendUserConfirmationMail(user.getEmail(), token.getValue());
    }

    public void confirm(String tokenValue) {
        var verificationToken = verificationService.validateAndUtilize(tokenValue, VerificationToken.Action.CONFIRM_ACCOUNT);
        var user = verificationToken.getUser()
                .setEnabled(true);

        userRepository.save(user);
    }

    public void resendToken(String email) {
        var user = loadUserByUsername(email);
        if (user.isEnabled()) {
            throw new UserAlreadyEnabledException("User already enabled", user.getEmail());
        }
        var token = verificationService.create(user, VerificationToken.Action.CONFIRM_ACCOUNT);

        mailSender.sendUserConfirmationMail(user.getEmail(), token.getValue());
    }

    public void resetPassword(String email) {
        var user = loadUserByUsername(email);
        var token = verificationService.create(user, VerificationToken.Action.RESET_PASSWORD);

        mailSender.sendResetPasswordMail(user.getEmail(), token.getValue());
    }

    public void updatePassword(String tokenValue, String password) {
        var verificationToken = verificationService.validateAndUtilize(tokenValue, VerificationToken.Action.RESET_PASSWORD);
        var user = verificationToken.getUser()
                .setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public AuthResponse authenticate(String email, String password) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        var user = (User) authentication.getPrincipal();
        var jwtToken = jwtTokenProvider.generateToken(user);
        var refreshToken = verificationService.create(user, VerificationToken.Action.REFRESH_ACCESS);

        return new AuthResponse(jwtToken, refreshToken.getValue(), user.getEmail());
    }

    public AuthResponse refreshAccessToken(String oldTokenValue) {
        var oldToken = verificationService.validateAndUtilize(oldTokenValue, VerificationToken.Action.REFRESH_ACCESS);
        var user = oldToken.getUser();
        var jwt = jwtTokenProvider.generateToken(user);
        var newToken = verificationService.create(user, VerificationToken.Action.REFRESH_ACCESS);

        return new AuthResponse(jwt, newToken.getValue(), user.getEmail());
    }

    public String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private User createUser(String email, String password) {
        var user = new User()
                .setEmail(email)
                .setPassword(passwordEncoder.encode(password));
        user.getRoles().add(roleRepository.findByName("ROLE_USER"));

        return userRepository.save(user);
    }
}
