package com.everkeep.dto;

import javax.validation.constraints.NotBlank;

import lombok.Value;

@Value
public class AuthRequest {

    @NotBlank
    String email;

    @NotBlank
    String password;
}
