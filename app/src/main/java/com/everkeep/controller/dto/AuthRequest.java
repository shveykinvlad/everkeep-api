package com.everkeep.controller.dto;

import javax.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank
        String email,

        @NotBlank
        String password
) { }
