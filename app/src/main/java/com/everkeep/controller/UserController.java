package com.everkeep.controller;

import static com.everkeep.controller.UserController.USERS_URL;
import static com.everkeep.utils.Headers.X_API_KEY;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.controller.dto.AuthenticationRequest;
import com.everkeep.controller.dto.AuthenticationResponse;
import com.everkeep.controller.dto.RegistrationRequest;
import com.everkeep.service.UserService;

@RestController
@RequestMapping(path = USERS_URL)
@RequiredArgsConstructor
public class UserController {

    public static final String USERS_URL = "/api/users";
    public static final String CONFIRMATION_URL = "/confirmation";
    public static final String PASSWORD_URL = "/password";
    public static final String AUTHENTICATION_URL = "/authentication";
    public static final String LOGOUT_URL = "/logout";
    public static final String ACCESS_URL = "/access";
    public static final String EMAIL_PARAM = "email";

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user")
    public void register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        userService.register(registrationRequest.email(), registrationRequest.password());
    }

    @GetMapping(value = CONFIRMATION_URL)
    @Operation(summary = "Resend confirmation email")
    public void resendConfirmation(@RequestParam(EMAIL_PARAM) String email) {
        userService.resendToken(email);
    }

    @PostMapping(value = CONFIRMATION_URL)
    @Operation(summary = "Confirm user registration")
    public void applyConfirmation(@RequestHeader(X_API_KEY) @NotBlank String token) {
        userService.confirm(token);
    }

    @DeleteMapping(value = PASSWORD_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reset password")
    public void resetPassword(@RequestParam(EMAIL_PARAM) String email) {
        userService.resetPassword(email);
    }

    @PostMapping(value = PASSWORD_URL)
    @Operation(summary = "Update password")
    public void updatePassword(@RequestBody @Valid RegistrationRequest registrationRequest,
                               @RequestHeader(X_API_KEY) @NotBlank String token) {
        userService.updatePassword(token, registrationRequest.password());
    }

    @PostMapping(AUTHENTICATION_URL)
    @Operation(summary = "Authentication")
    public AuthenticationResponse authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return userService.authenticate(authenticationRequest.email(), authenticationRequest.password());
    }

    @DeleteMapping(LOGOUT_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Logout")
    public void logout(@RequestHeader(X_API_KEY) @NotBlank String token) {
        userService.logout(token);
    }

    @PostMapping(ACCESS_URL)
    @Operation(summary = "Refresh access token")
    public AuthenticationResponse refreshAccess(@RequestHeader(X_API_KEY) @NotBlank String token) {
        return userService.refreshAccessToken(token);
    }
}
