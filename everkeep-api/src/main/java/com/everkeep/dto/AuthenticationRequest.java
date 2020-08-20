package com.everkeep.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticationRequest {

    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
