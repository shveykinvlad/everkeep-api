package com.everkeep.service;

import static com.everkeep.model.VerificationToken.Action.ACCOUNT_CONFIRMATION;
import static com.everkeep.model.VerificationToken.Action.PASSWORD_RESET;

import com.everkeep.exception.UserAlreadyEnabledException;
import com.everkeep.exception.UserAlreadyExistsException;
import com.everkeep.exception.VerificationTokenExpiredException;
import com.everkeep.model.User;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenService verificationTokenService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public User get(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email = %s not found".formatted(email)));
    }

    public void register(String email, String password) {
        if (userExists(email)) {
            throw new UserAlreadyExistsException("User already exists", email);
        }
        var user = create(email, password);
        var tokenValue = verificationTokenService.create(user, ACCOUNT_CONFIRMATION);

        mailService.sendConfirmationMail(user.getEmail(), tokenValue);
    }

    public void confirm(String tokenValue) {
        try {
            var verificationToken = verificationTokenService.apply(tokenValue, ACCOUNT_CONFIRMATION);
            var user = verificationToken.getUser();
            user.setEnabled(true);

            userRepository.save(user);
        } catch (VerificationTokenExpiredException e) {
            resendToken(e.getEmail());
            throw e;
        }
    }

    public void resendToken(String email) {
        var user = get(email);
        if (user.isEnabled()) {
            throw new UserAlreadyEnabledException("User already enabled", user.getEmail());
        }
        var tokenValue = verificationTokenService.create(user, ACCOUNT_CONFIRMATION);

        mailService.sendConfirmationMail(user.getEmail(), tokenValue);
    }

    public void resetPassword(String email) {
        var user = get(email);
        var token = verificationTokenService.create(user, PASSWORD_RESET);

        mailService.sendResetPasswordMail(user.getEmail(), token);
    }

    public void updatePassword(String tokenValue, String password) {
        var verificationToken = verificationTokenService.apply(tokenValue, PASSWORD_RESET);
        var user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private User create(String email, String password) {
        var user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(roleRepository.findByName("ROLE_USER")))
                .build();

        return userRepository.save(user);
    }
}
