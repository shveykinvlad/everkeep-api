package com.everkeep.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.everkeep.AbstractIntegrationTest;
import com.everkeep.config.properties.VerificationTokenProperties;
import com.everkeep.controller.dto.SessionRequest;
import com.everkeep.model.User;
import com.everkeep.model.VerificationToken;
import com.everkeep.repository.RoleRepository;
import com.everkeep.repository.UserRepository;
import com.everkeep.repository.VerificationTokenRepository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static com.everkeep.controller.SessionController.REFRESH_TOKEN_HEADER;
import static com.everkeep.controller.SessionController.SESSIONS_URL;
import static com.everkeep.utils.DigestUtils.sha256Hex;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerTest extends AbstractIntegrationTest {

    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
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
    void create() throws Exception {
        var email = "andromeda@localhost";
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

        mockMvc.perform(
                        MockMvcRequestBuilders.post(SESSIONS_URL)
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.authToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void update() throws Exception {
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email("hekabe@localhost")
                        .password("$2a$10$l13RhzScYa0XCo4AGvbxTe2/f7W8.0b5bLf5Plwq713G15rcxlpJe")
                        .enabled(true)
                        .roles(Set.of(role))
                        .build()
        );
        var tokenValue = UUID.randomUUID().toString();
        verificationTokenRepository.save(
                VerificationToken.builder()
                        .hashValue(sha256Hex(tokenValue))
                        .action(VerificationToken.Action.SESSION_REFRESH)
                        .active(true)
                        .user(user)
                        .expiryTime(OffsetDateTime.now(clock)
                                .plusSeconds(verificationTokenProperties.expiryDuration().getSeconds()))
                        .build()
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.put(SESSIONS_URL)
                                .header(REFRESH_TOKEN_HEADER, tokenValue)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.authToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void delete() throws Exception {
        var role = roleRepository.findByName(ROLE_USER);
        var user = userRepository.save(
                User.builder()
                        .email("chloris@localhost")
                        .password("$2a$10$m3PkFi86uXTS3LXjxK09DOmT2eVN0IqAW73M7henHdDbc3QxLs7e.")
                        .enabled(true)
                        .roles(Set.of(role))
                        .build()
        );
        var tokenValue = UUID.randomUUID().toString();
        var token = verificationTokenRepository.save(
                VerificationToken.builder()
                        .hashValue(sha256Hex(tokenValue))
                        .action(VerificationToken.Action.SESSION_REFRESH)
                        .active(true)
                        .user(user)
                        .expiryTime(
                                OffsetDateTime.now(clock)
                                        .plusSeconds(verificationTokenProperties.expiryDuration().getSeconds())
                        )
                        .build()
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(SESSIONS_URL)
                                .header(REFRESH_TOKEN_HEADER, tokenValue)
                )
                .andExpect(status().isNoContent());

        assertAll(
                "Should utilize token",
                () -> assertFalse(
                        verificationTokenRepository.findByHashValueAndAction(token.getHashValue(), token.getAction())
                                .orElseThrow()
                                .isActive()
                )
        );
    }

}
