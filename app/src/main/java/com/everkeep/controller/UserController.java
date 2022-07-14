package com.everkeep.controller;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.controller.dto.AuthRequest;
import com.everkeep.controller.dto.AuthResponse;
import com.everkeep.controller.dto.RegistrationRequest;
import com.everkeep.service.UserService;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register user")
    public void register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        userService.register(registrationRequest.email(), registrationRequest.email());
    }

    @GetMapping("/confirm")
    @Operation(summary = "Confirm user registration")
    public void confirm(@RequestParam("token") String tokenValue) {
        userService.confirm(tokenValue);
    }

    @GetMapping("/confirm/resend")
    @Operation(summary = "Resend confirmation email")
    public void resendToken(@RequestParam("email") String email) {
        userService.resendToken(email);
    }

    @GetMapping("/password/reset")
    @Operation(summary = "Reset password")
    public void resetPassword(@RequestParam("email") String email) {
        userService.resetPassword(email);
    }

    @PutMapping("/password/update")
    @Operation(summary = "Update password")
    public void updatePassword(@RequestParam("token") String tokenValue,
                               @RequestBody @Valid RegistrationRequest registrationRequest) {
        userService.updatePassword(tokenValue, registrationRequest.password());
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate")
    public AuthResponse authenticate(@RequestBody @Valid AuthRequest authRequest) {
        return userService.authenticate(authRequest.email(), authRequest.password());
    }

    @PostMapping("/authenticate/refresh")
    @Operation(summary = "Refresh access token")
    public AuthResponse refreshAccessToken(@RequestBody @Valid String refreshToken) {
        return userService.refreshAccessToken(refreshToken);
    }
}
