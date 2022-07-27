package com.everkeep.controller;

import static com.everkeep.controller.UserController.CONFIRMATION_URL;
import static com.everkeep.controller.UserController.EMAIL_PARAM;
import static com.everkeep.controller.UserController.PASSWORD_URL;
import static com.everkeep.controller.UserController.SESSIONS_URL;
import static com.everkeep.controller.UserController.USERS_URL;
import static com.everkeep.utils.Headers.X_API_KEY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import com.icegreen.greenmail.util.GreenMailUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.everkeep.AbstractIntegrationTest;
import com.everkeep.config.properties.VerificationTokenProperties;
import com.everkeep.controller.dto.RegistrationRequest;
import com.everkeep.controller.dto.SessionRequest;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;
import com.everkeep.repository.VerificationTokenRepository;

class UserControllerTest extends AbstractIntegrationTest {

    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private VerificationTokenProperties verificationTokenProperties;
    @Autowired
    private Clock clock;

    @AfterEach
    void tearDown() {
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register() throws Exception {
        var email = "one@localhost";
        var request = new RegistrationRequest(
                "F1r$tP4$$",
                "F1r$tP4$$",
                email
        );

        mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL)
                        .content(mapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        var token = verificationTokenRepository.findAll().get(0);
        var receivedMessages = GREEN_MAIL.getReceivedMessages();
        assertAll("Should receive confirmation email",
                () -> assertEquals(1, receivedMessages.length),
                () -> assertEquals(email, receivedMessages[0].getAllRecipients()[0].toString()),
                () -> assertEquals("Email confirmation", receivedMessages[0].getSubject()),
                () -> assertEquals("http://localhost:4200/users/confirmation?token=%s".formatted(token.getValue()),
                        GreenMailUtil.getBody(receivedMessages[0])));
    }

    @Test
    void applyConfirmation() throws Exception {
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email("two@localhost")
                        .password("$2a$10$jn05mcrBgUrCL4TbbSSY0el8Kcoz5jzU9NkrCVmZrqSQA.TUA5xcS")
                        .enabled(false)
                        .roles(Set.of(role))
                        .build()
        );
        var token = verificationTokenRepository.save(
                VerificationToken.builder()
                        .value(UUID.randomUUID().toString())
                        .action(VerificationToken.Action.ACCOUNT_CONFIRMATION)
                        .active(true)
                        .user(user)
                        .expiryTime(OffsetDateTime.now(clock).plusSeconds(verificationTokenProperties.expiryDuration().getSeconds()))
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL + CONFIRMATION_URL)
                        .header(X_API_KEY, token.getValue()))
                .andExpect(status().isOk());

        assertAll("Should enable user and utilize token",
                () -> assertTrue(userRepository.findByEmail(user.getEmail()).orElseThrow().isEnabled()),
                () -> assertFalse(verificationTokenRepository.findByValueAndAction(token.getValue(), token.getAction()).orElseThrow()
                        .isActive())
        );
    }

    @Test
    void resendConfirmation() throws Exception {
        var email = "three@localhost";
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email(email)
                        .password("$2a$10$PFV9jWQK93Sawi3rq.hR7u511u48.AyoeLzxSGjwNWeQLRUDL8ity")
                        .enabled(false)
                        .roles(Set.of(role))
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.get(USERS_URL + CONFIRMATION_URL)
                        .param(EMAIL_PARAM, user.getEmail()))
                .andExpect(status().isOk());

        var token = verificationTokenRepository.findAll().get(0);
        var receivedMessages = GREEN_MAIL.getReceivedMessages();
        assertAll("Should receive confirmation email",
                () -> assertEquals(1, receivedMessages.length),
                () -> assertEquals(email, receivedMessages[0].getAllRecipients()[0].toString()),
                () -> assertEquals("Email confirmation", receivedMessages[0].getSubject()),
                () -> assertEquals("http://localhost:4200/users/confirmation?token=%s".formatted(token.getValue()),
                        GreenMailUtil.getBody(receivedMessages[0])));
    }

    @Test
    void resetPassword() throws Exception {
        var email = "four@localhost";
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email(email)
                        .password("$2a$10$AA0UY.EijdTzItSWdlvXseYRyiph1PtWUnLQRDajO0.LKU1TlC0XG")
                        .enabled(true)
                        .roles(Set.of(role))
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + PASSWORD_URL)
                        .param(EMAIL_PARAM, user.getEmail()))
                .andExpect(status().isNoContent());

        var token = verificationTokenRepository.findAll().get(0);
        var receivedMessages = GREEN_MAIL.getReceivedMessages();
        assertAll("Should receive password reset email",
                () -> assertEquals(1, receivedMessages.length),
                () -> assertEquals(email, receivedMessages[0].getAllRecipients()[0].toString()),
                () -> assertEquals("Password reset", receivedMessages[0].getSubject()),
                () -> assertEquals("http://localhost:4200/users/password/update?email=%s&token=%s"
                        .formatted(user.getEmail(), token.getValue()), GreenMailUtil.getBody(receivedMessages[0])));
    }

    @Test
    void updatePassword() throws Exception {
        var role = roleRepository.findByName(ROLE_USER);
        var oldEncodedPassword = "$2a$10$q0HqwhbSRe3ouGMne3M2OO1jldmjB3fGGfLrvCtDoQ1./iV6vgGKq";
        var user = userRepository.save(
                User.builder()
                        .email("five@localhost")
                        .password(oldEncodedPassword)
                        .enabled(false)
                        .roles(Set.of(role))
                        .build()
        );
        var token = verificationTokenRepository.save(
                VerificationToken.builder()
                        .value(UUID.randomUUID().toString())
                        .action(VerificationToken.Action.PASSWORD_RESET)
                        .active(true)
                        .user(user)
                        .expiryTime(OffsetDateTime.now(clock).plusSeconds(verificationTokenProperties.expiryDuration().getSeconds()))
                        .build()
        );
        var request = new RegistrationRequest(
                "N3WP4$$w0rd",
                "N3WP4$$w0rd",
                user.getEmail()
        );
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL + PASSWORD_URL)
                        .header(X_API_KEY, token.getValue())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var newEncodedPassword = userRepository.findByEmail(user.getEmail()).orElseThrow().getPassword();
        var isActiveToken = verificationTokenRepository.findByValueAndAction(token.getValue(), token.getAction()).orElseThrow().isActive();
        assertAll("Should update password and utilize token",
                () -> assertNotEquals(oldEncodedPassword, newEncodedPassword),
                () -> assertFalse(isActiveToken)

        );
    }

    @Test
    void createSession() throws Exception {
        var email = "six@localhost";
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email(email)
                        .password("$2a$10$1QkElnmngJkPdyFyb6dVk.4Kp2tlDwcUzkD7cJlAJEaERpzqOGByS")
                        .enabled(true)
                        .roles(Set.of(role))
                        .build()
        );
        var request = new SessionRequest(email, "P4$$w0rd");
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_URL + SESSIONS_URL)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.jwt").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void refreshSession() throws Exception {
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email("seven@localhost")
                        .password("$2a$10$l13RhzScYa0XCo4AGvbxTe2/f7W8.0b5bLf5Plwq713G15rcxlpJe")
                        .enabled(true)
                        .roles(Set.of(role))
                        .build()
        );
        var token = verificationTokenRepository.save(
                VerificationToken.builder()
                        .value(UUID.randomUUID().toString())
                        .action(VerificationToken.Action.SESSION_REFRESH)
                        .active(true)
                        .user(user)
                        .expiryTime(OffsetDateTime.now(clock).plusSeconds(verificationTokenProperties.expiryDuration().getSeconds()))
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.put(USERS_URL + SESSIONS_URL)
                        .header(X_API_KEY, token.getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.jwt").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void deleteSession() throws Exception {
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email("eight@localhost")
                        .password("$2a$10$m3PkFi86uXTS3LXjxK09DOmT2eVN0IqAW73M7henHdDbc3QxLs7e.")
                        .enabled(true)
                        .roles(Set.of(role))
                        .build()
        );
        var token = verificationTokenRepository.save(
                VerificationToken.builder()
                        .value(UUID.randomUUID().toString())
                        .action(VerificationToken.Action.SESSION_REFRESH)
                        .active(true)
                        .user(user)
                        .expiryTime(OffsetDateTime.now(clock).plusSeconds(verificationTokenProperties.expiryDuration().getSeconds()))
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.delete(USERS_URL + SESSIONS_URL)
                        .header(X_API_KEY, token.getValue()))
                .andExpect(status().isNoContent());

        assertAll("Should utilize token",
                () -> assertFalse(verificationTokenRepository.findByValueAndAction(token.getValue(), token.getAction()).orElseThrow()
                        .isActive())
        );
    }
}
