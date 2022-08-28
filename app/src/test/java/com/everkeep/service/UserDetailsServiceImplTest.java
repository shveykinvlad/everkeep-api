package com.everkeep.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.everkeep.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootTest(classes = UserDetailsServiceImpl.class)
class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsService userDetailsService;
    @MockBean
    private UserService userService;

    @Test
    void loadUserByUsername() {
        var email = "apollo@localhost";
        var savedUser = User.builder()
                .email(email)
                .build();
        when(userService.get(email)).thenReturn(savedUser);

        var receivedUser = userDetailsService.loadUserByUsername(email);

        assertEquals(savedUser, receivedUser);
    }
}
