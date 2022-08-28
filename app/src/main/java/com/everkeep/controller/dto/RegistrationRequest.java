package com.everkeep.controller.dto;

import com.everkeep.annotation.PasswordMatches;
import com.everkeep.annotation.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
