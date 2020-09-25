package com.everkeep.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthResponse {

    private String jwt;
    private String refreshTokenValue;
    private String userEmail;
}
