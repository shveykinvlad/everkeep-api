package com.everkeep.dto;

import javax.validation.constraints.NotBlank;

import lombok.Value;

@Value
public class AuthResponse {

    @NotBlank
    String jwt;

    @NotBlank
    String refreshTokenValue;

    @NotBlank
    String userEmail;
}
