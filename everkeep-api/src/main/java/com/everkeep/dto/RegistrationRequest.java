package com.everkeep.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Value;

import com.everkeep.annotation.PasswordMatches;
import com.everkeep.annotation.ValidPassword;

@Value
@PasswordMatches
public class RegistrationRequest {

    @NotBlank
    @ValidPassword
    String password;

    @NotBlank
    @ValidPassword
    String matchingPassword;

    @Email
    @NotBlank
    String email;
}
