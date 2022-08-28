package com.everkeep.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.everkeep.AbstractTest;
import com.everkeep.exception.UserAlreadyEnabledException;
import com.everkeep.exception.UserAlreadyExistsException;
import com.everkeep.exception.VerificationTokenExpiredException;
import com.everkeep.model.Role;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static com.everkeep.utils.DigestUtils.sha256Hex;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserService.class)
class UserServiceTest extends AbstractTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private VerificationTokenService verificationTokenService;
    @MockBean
    private MailService mailService;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void loadUserByUsername() {
        var email = "patroklos@localhost";
        var savedUser = User.builder()
                .email(email)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(savedUser));

        var receivedUser = userService.get(email);

        assertEquals(savedUser, receivedUser);
    }

    @Test
    void loadUserByUsernameIfNotFound() {
        var email = "aegle@localhost";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.get(email));
    }

    @Test
    void register() {
        var tokenValue = UUID.randomUUID().toString();
        var password = "Th1rdP4$$";
        var email = "laios@localhost";
        var roleName = "ROLE_USER";
        var action = VerificationToken.Action.ACCOUNT_CONFIRMATION;
        var role = Role.builder()
                .name(roleName)
                .build();
        var user = User.builder()
                .email(email)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("");
        when(roleRepository.findByName(roleName)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationTokenService.create(user, action)).thenReturn(tokenValue);

        userService.register(email, password);

        verify(mailService).sendConfirmationMail(email, tokenValue);
    }

    @Test
    void registerIfUserExists() {
        var email = "pyrrhus@localhost";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.register(email, "F0urthP4$$"),
                "Should throw an exception if user already exists");
    }

    @Test
    void applyConfirmation() {
        var tokenValue = UUID.randomUUID().toString();
        var action = VerificationToken.Action.ACCOUNT_CONFIRMATION;
        var token = VerificationToken.builder()
                .hashValue(sha256Hex(tokenValue))
                .action(action)
                .user(new User())
                .build();
        when(verificationTokenService.apply(tokenValue, action)).thenReturn(token);

        userService.confirm(tokenValue);

        verify(userRepository).save(userCaptor.capture());
        assertAll("Should enable user",
                () -> assertTrue(userCaptor.getValue().isEnabled())
        );
    }

    @Test
    void applyConfirmationIfExpired() {
        var oldTokenValue = UUID.randomUUID().toString();
        var newTokenValue = UUID.randomUUID().toString();
        var username = "kore@localhost";
        var user = User.builder()
                .email(username)
                .build();
        var action = VerificationToken.Action.ACCOUNT_CONFIRMATION;
        when(verificationTokenService.apply(oldTokenValue, action))
                .thenThrow(new VerificationTokenExpiredException("Verification token is expired", oldTokenValue,
                        username));
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(verificationTokenService.create(user, VerificationToken.Action.ACCOUNT_CONFIRMATION)).thenReturn(
                newTokenValue);

        assertThrows(VerificationTokenExpiredException.class,
                () -> userService.confirm(oldTokenValue),
                "Should throw an exception if token is expired");

        verify(mailService).sendConfirmationMail(username, newTokenValue);
    }

    @Test
    void resendConfirmation() {
        var tokenValue = UUID.randomUUID().toString();
        var email = "philomele@localhost";
        var user = User.builder()
                .email(email)
                .build();
        var action = VerificationToken.Action.ACCOUNT_CONFIRMATION;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationTokenService.create(user, action)).thenReturn(tokenValue);

        userService.resendToken(email);

        verify(mailService).sendConfirmationMail(email, tokenValue);
    }

    @Test
    void resendConfirmationIfUserIsAlreadyEnabled() {
        var email = "orpheus@localhost";
        var user = User.builder()
                .email(email)
                .enabled(true)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyEnabledException.class, () -> userService.resendToken(email));
    }

    @Test
    void resetPassword() {
        var tokenValue = UUID.randomUUID().toString();
        var email = "elektra@localhost";
        var user = User.builder()
                .email(email)
                .build();
        var action = VerificationToken.Action.PASSWORD_RESET;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationTokenService.create(user, action)).thenReturn(tokenValue);

        userService.resetPassword(email);

        verify(mailService).sendResetPasswordMail(user.getEmail(), tokenValue);
    }

    @Test
    void updatePassword() {
        var tokenValue = UUID.randomUUID().toString();
        var action = VerificationToken.Action.PASSWORD_RESET;
        var password = "P4$$w0rd";
        var encodedPassword = "$2a$10$l13RhzScYa0XCo4AGvbxTe2/f7W8.0b5bLf5Plwq713G15rcxlpJe";
        var token = VerificationToken.builder()
                .hashValue(sha256Hex(tokenValue))
                .action(action)
                .user(new User())
                .build();
        when(verificationTokenService.apply(tokenValue, action)).thenReturn(token);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        userService.updatePassword(tokenValue, password);

        verify(userRepository).save(userCaptor.capture());
        assertAll("Should return updated password",
                () -> assertEquals(encodedPassword, userCaptor.getValue().getPassword())
        );
    }
}
