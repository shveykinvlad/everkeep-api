package com.everkeep.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

import com.everkeep.annotation.PasswordMatches;

@Data
@PasswordMatches
public class UserDto {

    @NotBlank
    private String password;

    @NotBlank
    private String matchingPassword;

    @Email
    @NotBlank
    private String email;
}
