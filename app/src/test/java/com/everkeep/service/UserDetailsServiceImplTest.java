package com.everkeep.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.everkeep.model.User;
import com.everkeep.repository.UserRepository;

@SpringBootTest(classes = UserDetailsServiceImpl.class)
class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsService userDetailsService;
    @MockBean
    private UserRepository userRepository;

    @Test
    void loadUserByUsername() {
        var email = "apollo@localhost";
        var savedUser = User.builder()
                .email(email)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(savedUser));

        var receivedUser = userDetailsService.loadUserByUsername(email);

        assertEquals(savedUser, receivedUser);
    }

    @Test
    void loadUserByUsernameIfNotFound() {
        var email = "hermione@localhost";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }
}
