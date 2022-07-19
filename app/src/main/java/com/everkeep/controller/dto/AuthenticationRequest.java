package com.everkeep.controller.dto;

import javax.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank
        String email,

        @NotBlank
        String password
) { }
