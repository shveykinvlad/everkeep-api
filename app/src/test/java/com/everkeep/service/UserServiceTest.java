package com.everkeep.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.everkeep.AbstractTest;
import com.everkeep.service.security.JwtTokenProvider;
import com.everkeep.exception.UserAlreadyEnabledException;
import com.everkeep.exception.UserAlreadyExistsException;
import com.everkeep.model.Role;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;

@SpringBootTest(classes = UserService.class)
class UserServiceTest extends AbstractTest {

    private static final String ROLE_NAME = "ROLE_USER";
    private static final String EMAIL = "email@example.com";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded password";
    private static final String TOKEN_VALUE = "token value";

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
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @AfterEach
    void tearDown() {
        Mockito.reset(userRepository, roleRepository, passwordEncoder, verificationTokenService,
                mailService, authenticationManager, jwtTokenProvider);
    }

    @Test
    void loadUserByUsername() {
        var email = EMAIL;
        var expected = new User();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expected));
        var actual = userService.loadUserByUsername(email);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void loadUserByUsernameIfNotFound() {
        var email = EMAIL;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void register() {
        var password = PASSWORD;
        var email = EMAIL;
        var tokenValue = TOKEN_VALUE;
        var roleName = ROLE_NAME;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var role = Role.builder()
                .name(roleName)
                .build();
        var user = User.builder()
                .email(email)
                .build();
        var token = VerificationToken.builder()
                .value(tokenValue)
                .action(action)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByName(roleName)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationTokenService.create(user, action)).thenReturn(token);
        userService.register(email, password);

        Mockito.verify(mailService).sendUserConfirmationMail(email, tokenValue);
    }

    @Test
    void registerIfUserExists() {
        var email = EMAIL;

        when(userRepository.existsByEmail(email)).thenReturn(true);

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.register(email, PASSWORD));
    }

    @Test
    void confirm() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var token = VerificationToken.builder()
                .value(tokenValue)
                .action(action)
                .user(new User())
                .build();

        when(verificationTokenService.apply(tokenValue, action)).thenReturn(token);
        userService.confirm(tokenValue);

        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertAll(() -> Assertions.assertTrue(userCaptor.getValue().isEnabled()));
    }

    @Test
    void resendToken() {
        var email = EMAIL;
        var user = User.builder()
                .email(email)
                .build();
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var token = VerificationToken.builder()
                .user(user)
                .value(tokenValue)
                .action(action)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationTokenService.create(user, action)).thenReturn(token);
        userService.resendToken(email);

        Mockito.verify(mailService).sendUserConfirmationMail(email, tokenValue);
    }

    @Test
    void resendTokenIfUserIsEnabled() {
        var email = EMAIL;
        var user = User.builder()
                .email(email)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Assertions.assertThrows(UserAlreadyEnabledException.class, () -> userService.resendToken(email));
    }

    @Test
    void resetPassword() {
        var tokenValue = TOKEN_VALUE;
        var email = EMAIL;
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

        Mockito.verify(mailService).sendResetPasswordMail(user.getEmail(), tokenValue);
    }

    @Test
    void updatePassword() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.RESET_PASSWORD;
        var password = PASSWORD;
        var encodedPassword = ENCODED_PASSWORD;
        var token = VerificationToken.builder()
                .value(tokenValue)
                .action(action)
                .user(new User())
                .build();

        when(verificationTokenService.apply(tokenValue, action)).thenReturn(token);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        userService.updatePassword(tokenValue, password);

        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertAll(() -> Assertions.assertEquals(encodedPassword, userCaptor.getValue().getPassword()));
    }

    @Test
    void authenticate() {
        var email = EMAIL;
        var password = PASSWORD;
        var jwt = "jwt";
        var user = User.builder()
                .email(email)
                .password(password)
                .build();
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.REFRESH_ACCESS;
        var token = VerificationToken.builder()
                .value(tokenValue)
                .action(action)
                .build();

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)))
                .thenReturn(new TestingAuthenticationToken(user, password));
        when(jwtTokenProvider.generateToken(user)).thenReturn(jwt);
        when(verificationTokenService.create(user, action)).thenReturn(token);
        var actual = userService.authenticate(email, password);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(jwt, actual.jwt());
            Assertions.assertEquals(tokenValue, actual.refreshTokenValue());
            Assertions.assertEquals(email, actual.userEmail());
        });
    }

    @Test
    void refreshAccessToken() {
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
        var actual = userService.refreshAccessToken(oldToken.getValue());

        Assertions.assertAll(() -> {
            Assertions.assertEquals(jwt, actual.jwt());
            Assertions.assertEquals(newToken.getValue(), actual.refreshTokenValue());
            Assertions.assertEquals(newToken.getUser().getEmail(), actual.userEmail());
        });
    }
}
