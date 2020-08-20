package com.everkeep.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticationResponse {

    private String jwt;
}
