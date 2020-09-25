package com.everkeep.resource;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.dto.AuthRequest;
import com.everkeep.dto.AuthResponse;
import com.everkeep.dto.RegistrationRequest;
import com.everkeep.service.security.UserService;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserResource implements UserResourceApi {

    private final UserService userService;

    @Override
    public void register(RegistrationRequest registrationRequest, HttpServletRequest request) {
        userService.register(registrationRequest, request.getContextPath());
    }

    @Override
    public void confirm(String tokenValue) {
        userService.confirm(tokenValue);
    }

    @Override
    public void resendToken(String email, HttpServletRequest request) {
        userService.resendToken(email, request.getContextPath());
    }

    @Override
    public void resetPassword(String email, HttpServletRequest request) {
        userService.resetPassword(email, request.getContextPath());
    }

    @Override
    public void updatePassword(String tokenValue, @Valid RegistrationRequest registrationRequest) {
        userService.updatePassword(tokenValue, registrationRequest.getPassword());
    }

    @Override
    public AuthResponse authenticate(@Valid AuthRequest authRequest) {
        return userService.authenticate(authRequest.getEmail(), authRequest.getPassword());
    }

    @Override
    public AuthResponse refreshAccessToken(@Valid String refreshToken) {
        return userService.refreshAccessToken(refreshToken);
    }
}
