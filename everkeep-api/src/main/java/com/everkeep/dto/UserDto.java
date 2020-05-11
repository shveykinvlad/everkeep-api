package com.everkeep.dto;

import com.everkeep.annotation.PasswordMatches;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
