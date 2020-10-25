package com.everkeep.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Value;

@Value
public class AuthRequest {

    @NotEmpty
    String email;

    @NotEmpty
    String password;
}
