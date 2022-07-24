package com.everkeep.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.everkeep.AbstractTest;
import com.everkeep.exception.UserAlreadyEnabledException;
import com.everkeep.exception.UserAlreadyExistsException;
import com.everkeep.model.Role;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;
import com.everkeep.service.security.JwtTokenProvider;

@SpringBootTest(classes = UserService.class)
class UserServiceTest extends AbstractTest {

    @Autowired
    private UserService userService;
    @MockBean
    private AuthenticationManager authenticationManager;
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
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void loadUserByUsername() {
        var email = "one@localhost";
        var savedUser = User.builder()
                .email(email)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(savedUser));

        var receivedUser = userService.loadUserByUsername(email);

        assertEquals(savedUser, receivedUser);
    }

    @Test
    void loadUserByUsernameIfNotFound() {
        var email = "two@localhost";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void register() {
        var password = "Th1rdP4$$";
        var email = "three@localhost";
        var roleName = "ROLE_USER";
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var role = Role.builder()
                .name(roleName)
                .build();
        var user = User.builder()
                .email(email)
                .build();
        var token = VerificationToken.builder()
                .value(UUID.randomUUID().toString())
                .action(action)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("");
        when(roleRepository.findByName(roleName)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationTokenService.create(user, action)).thenReturn(token);

        userService.register(email, password);

        verify(mailService).sendConfirmationMail(email, token.getValue());
    }

    @Test
    void registerIfUserExists() {
        var email = "four@localhost";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(email, "F0urthP4$$"));
    }

    @Test
    void applyConfirmation() {
        var tokenValue = UUID.randomUUID().toString();
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var token = VerificationToken.builder()
                .value(tokenValue)
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
    void resendConfirmation() {
        var email = "five@localhost";
        var user = User.builder()
                .email(email)
                .build();
        var tokenValue = UUID.randomUUID().toString();
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var token = VerificationToken.builder()
                .user(user)
                .value(tokenValue)
                .action(action)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationTokenService.create(user, action)).thenReturn(token);

        userService.resendToken(email);

        verify(mailService).sendConfirmationMail(email, tokenValue);
    }

    @Test
    void resendConfirmationIfUserIsAlreadyEnabled() {
        var email = "six@localhost";
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
        var email = "seven@localhost";
        var user = User.builder()
                .email(email)
                .build();
        var action = VerificationToken.Action.RESET_PASSWORD;
        var token = VerificationToken.builder()
                .action(action)
                .value(tokenValue)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationTokenService.create(user, action)).thenReturn(token);

        userService.resetPassword(email);

        verify(mailService).sendResetPasswordMail(user.getEmail(), tokenValue);
    }

    @Test
    void updatePassword() {
        var tokenValue = UUID.randomUUID().toString();
        var action = VerificationToken.Action.RESET_PASSWORD;
        var password = "P4$$w0rd";
        var encodedPassword = "$2a$10$l13RhzScYa0XCo4AGvbxTe2/f7W8.0b5bLf5Plwq713G15rcxlpJe";
        var token = VerificationToken.builder()
                .value(tokenValue)
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

    @Test
    void authenticate() {
        var email = "eight@localhost";
        var password = "E1ghthP4$$";
        var jwt = "jwt";
        var user = User.builder()
                .email(email)
                .password(password)
                .build();
        var tokenValue = UUID.randomUUID().toString();
        var action = VerificationToken.Action.REFRESH_ACCESS;
        var token = VerificationToken.builder()
                .value(tokenValue)
                .action(action)
                .build();
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)))
                .thenReturn(new TestingAuthenticationToken(user, password));
        when(jwtTokenProvider.generateToken(user)).thenReturn(jwt);
        when(verificationTokenService.create(user, action)).thenReturn(token);

        var authenticationResponse = userService.authenticate(email, password);

        assertAll("Should return access token",
                () -> assertEquals(jwt, authenticationResponse.jwt()),
                () -> assertEquals(tokenValue, authenticationResponse.refreshToken()),
                () -> assertEquals(email, authenticationResponse.email())
        );
    }

    @Test
    void access() {
        var user = new User();
        var jwt = "jwt";
        var action = VerificationToken.Action.REFRESH_ACCESS;
        var oldToken = VerificationToken.builder()
                .value("old value")
                .action(VerificationToken.Action.REFRESH_ACCESS)
                .user(user)
                .build();
        var newToken = VerificationToken.builder()
                .value("new value")
                .action(VerificationToken.Action.REFRESH_ACCESS)
                .user(user)
                .build();
        when(verificationTokenService.apply(oldToken.getValue(), action)).thenReturn(oldToken);
        when(jwtTokenProvider.generateToken(user)).thenReturn(jwt);
        when(verificationTokenService.create(user, action)).thenReturn(newToken);

        var authenticationResponse = userService.refreshAccessToken(oldToken.getValue());

        assertAll("Should refresh access token",
                () -> assertEquals(jwt, authenticationResponse.jwt()),
                () -> assertEquals(newToken.getValue(), authenticationResponse.refreshToken()),
                () -> assertEquals(newToken.getUser().getEmail(), authenticationResponse.email()));
    }

    @Test
    void logout() {
        var tokenValue = UUID.randomUUID().toString();

        userService.logout(tokenValue);

        verify(verificationTokenService).apply(tokenValue, VerificationToken.Action.REFRESH_ACCESS);
    }
}
