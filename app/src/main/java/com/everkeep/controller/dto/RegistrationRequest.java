package com.everkeep.controller.dto;

import com.everkeep.annotation.PasswordMatches;
import com.everkeep.annotation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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
