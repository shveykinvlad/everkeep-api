package com.everkeep.controller.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.everkeep.annotation.PasswordMatches;
import com.everkeep.annotation.ValidPassword;

@PasswordMatches
public record RegistrationRequest(
        @NotBlank
        @ValidPassword
        String password,

        @NotBlank
        @ValidPassword
        String matchingPassword,

        @Email
        @NotBlank
        String email
) { }
