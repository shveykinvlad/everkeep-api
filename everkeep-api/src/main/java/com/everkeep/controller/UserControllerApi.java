package com.everkeep.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.everkeep.dto.AuthRequest;
import com.everkeep.dto.AuthResponse;
import com.everkeep.dto.RegistrationRequest;

public interface UserControllerApi {

    @PostMapping("/register")
    void register(@RequestBody @Valid RegistrationRequest registrationRequest);

    @GetMapping("/confirm")
    void confirm(@RequestParam("token") String tokenValue);

    @GetMapping("/confirm/resend")
    void resendToken(@RequestParam("email") String email);

    @GetMapping("/password/reset")
    void resetPassword(@RequestParam("email") String email);

    @PutMapping("/password/update")
    void updatePassword(@RequestParam("token") String tokenValue,
                        @RequestBody @Valid RegistrationRequest registrationRequest);

    @PostMapping("/authenticate")
    AuthResponse authenticate(@RequestBody @Valid AuthRequest authRequest);

    @PostMapping("/authenticate/refresh")
    AuthResponse refreshAccessToken(@RequestBody @Valid String refreshToken);
}
