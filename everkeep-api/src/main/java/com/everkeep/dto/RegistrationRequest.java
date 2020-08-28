package com.everkeep.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

import com.everkeep.annotation.PasswordMatches;
import com.everkeep.annotation.ValidPassword;

@Data
@PasswordMatches
public class RegistrationRequest {

    @NotBlank
    @ValidPassword
    private String password;

    @NotBlank
    @ValidPassword
    private String matchingPassword;

    @Email
    @NotBlank
    private String email;
}
