package com.everkeep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.dto.AuthRequest;
import com.everkeep.dto.AuthResponse;
import com.everkeep.dto.RegistrationRequest;
import com.everkeep.service.UserService;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final UserService userService;

    @Override
    public void register(RegistrationRequest registrationRequest) {
        userService.register(registrationRequest.getEmail(), registrationRequest.getPassword());
    }

    @Override
    public void confirm(String tokenValue) {
        userService.confirm(tokenValue);
    }

    @Override
    public void resendToken(String email) {
        userService.resendToken(email);
    }

    @Override
    public void resetPassword(String email) {
        userService.resetPassword(email);
    }

    @Override
    public void updatePassword(String tokenValue, RegistrationRequest registrationRequest) {
        userService.updatePassword(tokenValue, registrationRequest.getPassword());
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        return userService.authenticate(authRequest.getEmail(), authRequest.getPassword());
    }

    @Override
    public AuthResponse refreshAccessToken(String refreshToken) {
        return userService.refreshAccessToken(refreshToken);
    }
}
