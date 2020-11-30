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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import com.everkeep.AbstractTest;
import com.everkeep.config.security.JwtTokenProvider;
import com.everkeep.exception.UserAlreadyEnabledException;
import com.everkeep.exception.UserAlreadyExistsException;
import com.everkeep.model.Role;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;

@ContextConfiguration(classes = {UserService.class})
class UserServiceTest extends AbstractTest {

    private static final String ROLE_NAME = "role name";
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
    private VerificationService verificationService;
    @MockBean
    private MailSender mailSender;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Captor
    ArgumentCaptor<User> userCaptor;

    @AfterEach
    void tearDown() {
        Mockito.reset(userRepository, roleRepository, passwordEncoder, verificationService,
                mailSender, authenticationManager, jwtTokenProvider);
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
        var role = new Role()
                .setName(roleName);
        var user = new User()
                .setEmail(email);
        var token = new VerificationToken()
                .setValue(tokenValue)
                .setAction(action);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByName(roleName)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationService.create(user, action)).thenReturn(token);
        userService.register(email, password);

        Mockito.verify(mailSender).sendUserConfirmationMail(email, tokenValue);
    }

    @Test
    void registerIfUserExists() {
        var email = EMAIL;
        var password = PASSWORD;
        var user = new User()
                .setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.register(email, password));
    }

    @Test
    void confirm() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var token = new VerificationToken()
                .setValue(tokenValue)
                .setAction(action)
                .setUser(new User());

        when(verificationService.validateAndUtilize(tokenValue, action)).thenReturn(token);
        userService.confirm(tokenValue);

        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertAll(() -> {
            Assertions.assertTrue(userCaptor.getValue().isEnabled());
        });
    }

    @Test
    void resendToken() {
        var email = EMAIL;
        var user = new User()
                .setEmail(email);
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.CONFIRM_ACCOUNT;
        var token = new VerificationToken()
                .setUser(user)
                .setValue(tokenValue)
                .setAction(action);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationService.create(user, action)).thenReturn(token);
        userService.resendToken(email);

        Mockito.verify(mailSender).sendUserConfirmationMail(email, tokenValue);
    }

    @Test
    void resendTokenIfUserIsEnabled() {
        var email = EMAIL;
        var user = new User()
                .setEmail(email)
                .setEnabled(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Assertions.assertThrows(UserAlreadyEnabledException.class, () -> userService.resendToken(email));
    }

    @Test
    void resetPassword() {
        var tokenValue = TOKEN_VALUE;
        var email = EMAIL;
        var user = new User()
                .setEmail(email);
        var action = VerificationToken.Action.RESET_PASSWORD;
        var token = new VerificationToken()
                .setAction(action)
                .setValue(tokenValue);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationService.create(user, action)).thenReturn(token);
        userService.resetPassword(email);

        Mockito.verify(mailSender).sendResetPasswordMail(user.getEmail(), tokenValue);
    }

    @Test
    void updatePassword() {
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.RESET_PASSWORD;
        var password = PASSWORD;
        var encodedPassword = ENCODED_PASSWORD;
        var token = new VerificationToken()
                .setValue(tokenValue)
                .setAction(action)
                .setUser(new User());

        when(verificationService.validateAndUtilize(tokenValue, action)).thenReturn(token);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        userService.updatePassword(tokenValue, password);

        Mockito.verify(userRepository).save(userCaptor.capture());
        Assertions.assertAll(() -> {
            Assertions.assertEquals(encodedPassword, userCaptor.getValue().getPassword());
        });
    }

    @Test
    void authenticate() {
        var email = EMAIL;
        var password = PASSWORD;
        var jwt = "jwt";
        var user = new User()
                .setEmail(email)
                .setPassword(password);
        var tokenValue = TOKEN_VALUE;
        var action = VerificationToken.Action.REFRESH_ACCESS;
        var token = new VerificationToken()
                .setValue(tokenValue)
                .setAction(action);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)))
                .thenReturn(new TestingAuthenticationToken(user, password));
        when(jwtTokenProvider.generateToken(user)).thenReturn(jwt);
        when(verificationService.create(user, action)).thenReturn(token);
        var actual = userService.authenticate(email, password);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(jwt, actual.getJwt());
            Assertions.assertEquals(tokenValue, actual.getRefreshTokenValue());
            Assertions.assertEquals(email, actual.getUserEmail());
        });
    }

    @Test
    void refreshAccessToken() {
        var user = new User();
        var jwt = "jwt";
        var action = VerificationToken.Action.REFRESH_ACCESS;
        var oldToken = new VerificationToken()
                .setValue("old value")
                .setAction(VerificationToken.Action.REFRESH_ACCESS)
                .setUser(user);
        var newToken = new VerificationToken()
                .setValue("new value")
                .setAction(VerificationToken.Action.REFRESH_ACCESS)
                .setUser(user);

        when(verificationService.validateAndUtilize(oldToken.getValue(), action)).thenReturn(oldToken);
        when(jwtTokenProvider.generateToken(user)).thenReturn(jwt);
        when(verificationService.create(user, action)).thenReturn(newToken);
        var actual = userService.refreshAccessToken(oldToken.getValue());

        Assertions.assertAll(() -> {
            Assertions.assertEquals(jwt, actual.getJwt());
            Assertions.assertEquals(newToken.getValue(), actual.getRefreshTokenValue());
            Assertions.assertEquals(newToken.getUser().getEmail(), actual.getUserEmail());
        });
    }
}
