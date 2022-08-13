package com.everkeep.controller;

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

import com.everkeep.controller.dto.RegistrationRequest;
import com.everkeep.service.UserService;

@RestController
@RequestMapping(UserController.USERS_URL)
@RequiredArgsConstructor
public class UserController {

    public static final String USERS_URL = "/api/users";
    public static final String CONFIRMATION_URL = "/confirmation";
    public static final String PASSWORD_URL = "/password";
    public static final String EMAIL_PARAM = "email";

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user")
    public void register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        userService.register(registrationRequest.email(), registrationRequest.password());
    }

    @GetMapping(CONFIRMATION_URL)
    @Operation(summary = "Resend confirmation email")
    public void resendConfirmation(@RequestParam(EMAIL_PARAM) String email) {
        userService.resendToken(email);
    }

    @PostMapping(CONFIRMATION_URL)
    @Operation(summary = "Confirm user registration")
    public void confirm(@RequestHeader(X_API_KEY) @NotBlank String token) {
        userService.confirm(token);
    }

    @DeleteMapping(PASSWORD_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reset password")
    public void resetPassword(@RequestParam(EMAIL_PARAM) String email) {
        userService.resetPassword(email);
    }

    @PostMapping(PASSWORD_URL)
    @Operation(summary = "Update password")
    public void updatePassword(@RequestBody @Valid RegistrationRequest registrationRequest,
                               @RequestHeader(X_API_KEY) @NotBlank String token) {
        userService.updatePassword(token, registrationRequest.password());
    }
}
