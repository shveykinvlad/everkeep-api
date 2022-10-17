package com.everkeep.controller;

import com.icegreen.greenmail.util.GreenMailUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.everkeep.AbstractIntegrationTest;
import com.everkeep.config.properties.VerificationTokenProperties;
import com.everkeep.controller.dto.RegistrationRequest;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;
import com.everkeep.repository.VerificationTokenRepository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static com.everkeep.controller.UserController.CONFIRMATION_URL;
import static com.everkeep.controller.UserController.EMAIL_PARAM;
import static com.everkeep.controller.UserController.PASSWORD_URL;
import static com.everkeep.controller.UserController.USERS_URL;
import static com.everkeep.utils.DigestUtils.sha256Hex;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractIntegrationTest {

    private static final String CONFIRMATION_MAIL_MESSAGE_REGEX
            = "http://localhost:4200/users/confirmation\\?token=.*";
    private static final String PASSWORD_RESET_MAIL_MESSAGE_REGEX
            = "http://localhost:4200/users/password/update\\?email=.*&token=.*";

    private static final String ROLE_USER = "ROLE_USER";
    private static final String TOKEN_PARAM = "token";

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
        var email = "glaukos@localhost";
        var request = new RegistrationRequest(
                "F1r$tP4$$",
                "F1r$tP4$$",
                email
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post(USERS_URL)
                                .content(mapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated());

        var receivedMessage = GREEN_MAIL.getReceivedMessages()[0];
        assertAll(
                "Should receive confirmation email",
                () -> assertEquals(1, GREEN_MAIL.getReceivedMessages().length),
                () -> assertEquals(email, receivedMessage.getAllRecipients()[0].toString()),
                () -> assertEquals("Email confirmation", receivedMessage.getSubject()),
                () -> assertTrue(GreenMailUtil.getBody(receivedMessage).matches(CONFIRMATION_MAIL_MESSAGE_REGEX))
        );
    }

    @Test
    void confirm() throws Exception {
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email("clytemnestra@localhost")
                        .password("$2a$10$jn05mcrBgUrCL4TbbSSY0el8Kcoz5jzU9NkrCVmZrqSQA.TUA5xcS")
                        .enabled(false)
                        .roles(Set.of(role))
                        .build()
        );
        var tokenValue = UUID.randomUUID().toString();
        var token = verificationTokenRepository.save(
                VerificationToken.builder()
                        .hashValue(sha256Hex(tokenValue))
                        .action(VerificationToken.Action.ACCOUNT_CONFIRMATION)
                        .active(true)
                        .user(user)
                        .expiryTime(
                                OffsetDateTime.now(clock)
                                        .plusSeconds(verificationTokenProperties.expiryDuration().getSeconds())
                        )
                        .build()
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post(USERS_URL + CONFIRMATION_URL)
                                .param(TOKEN_PARAM, tokenValue)
                )
                .andExpect(status().isOk());

        assertAll(
                "Should enable user and utilize token",
                () -> assertTrue(
                        userRepository.findByEmail(user.getEmail())
                                .orElseThrow()
                                .isEnabled()
                ),
                () -> assertFalse(
                        verificationTokenRepository.findByHashValueAndAction(token.getHashValue(), token.getAction())
                                .orElseThrow()
                                .isActive()
                )
        );
    }

    @Test
    void resendConfirmation() throws Exception {
        var email = "tychon@localhost";
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email(email)
                        .password("$2a$10$PFV9jWQK93Sawi3rq.hR7u511u48.AyoeLzxSGjwNWeQLRUDL8ity")
                        .enabled(false)
                        .roles(Set.of(role))
                        .build()
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.get(USERS_URL + CONFIRMATION_URL)
                                .param(EMAIL_PARAM, user.getEmail())
                )
                .andExpect(status().isOk());

        var receivedMessage = GREEN_MAIL.getReceivedMessages()[0];
        assertAll(
                "Should receive confirmation email",
                () -> assertEquals(1, GREEN_MAIL.getReceivedMessages().length),
                () -> assertEquals(email, receivedMessage.getAllRecipients()[0].toString()),
                () -> assertEquals("Email confirmation", receivedMessage.getSubject()),
                () -> assertTrue(GreenMailUtil.getBody(receivedMessage).matches(CONFIRMATION_MAIL_MESSAGE_REGEX))
        );
    }

    @Test
    void resetPassword() throws Exception {
        var email = "europe@localhost";
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email(email)
                        .password("$2a$10$AA0UY.EijdTzItSWdlvXseYRyiph1PtWUnLQRDajO0.LKU1TlC0XG")
                        .enabled(true)
                        .roles(Set.of(role))
                        .build()
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(USERS_URL + PASSWORD_URL)
                                .param(EMAIL_PARAM, user.getEmail())
                )
                .andExpect(status().isNoContent());

        var receivedMessage = GREEN_MAIL.getReceivedMessages()[0];
        assertAll(
                "Should receive password reset email",
                () -> assertEquals(1, GREEN_MAIL.getReceivedMessages().length),
                () -> assertEquals(email, receivedMessage.getAllRecipients()[0].toString()),
                () -> assertEquals("Password reset", receivedMessage.getSubject()),
                () -> assertTrue(GreenMailUtil.getBody(receivedMessage).matches(PASSWORD_RESET_MAIL_MESSAGE_REGEX))
        );
    }

    @Test
    void updatePassword() throws Exception {
        var role = roleRepository.findByName(ROLE_USER);
        var oldEncodedPassword = "$2a$10$q0HqwhbSRe3ouGMne3M2OO1jldmjB3fGGfLrvCtDoQ1./iV6vgGKq";
        var user = userRepository.save(
                User.builder()
                        .email("chloris@localhost")
                        .password(oldEncodedPassword)
                        .enabled(false)
                        .roles(Set.of(role))
                        .build()
        );
        var tokenValue = UUID.randomUUID().toString();
        var token = verificationTokenRepository.save(
                VerificationToken.builder()
                        .hashValue(sha256Hex(tokenValue))
                        .action(VerificationToken.Action.PASSWORD_RESET)
                        .active(true)
                        .user(user)
                        .expiryTime(OffsetDateTime.now(clock)
                                .plusSeconds(verificationTokenProperties.expiryDuration().getSeconds()))
                        .build()
        );
        var request = new RegistrationRequest(
                "N3WP4$$w0rd",
                "N3WP4$$w0rd",
                user.getEmail()
        );
        mockMvc.perform(
                        MockMvcRequestBuilders.put(USERS_URL + PASSWORD_URL)
                                .param(TOKEN_PARAM, tokenValue)
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        assertAll(
                "Should update password and utilize token",
                () -> assertNotEquals(
                        oldEncodedPassword, userRepository.findByEmail(user.getEmail())
                                .orElseThrow()
                                .getPassword()
                ),
                () -> assertFalse(
                        verificationTokenRepository.findByHashValueAndAction(token.getHashValue(), token.getAction())
                                .orElseThrow()
                                .isActive()
                )
        );
    }
}
