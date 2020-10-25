package com.everkeep.dto;

import lombok.Value;

@Value
public class AuthResponse {

    String jwt;
    String refreshTokenValue;
    String userEmail;
}
