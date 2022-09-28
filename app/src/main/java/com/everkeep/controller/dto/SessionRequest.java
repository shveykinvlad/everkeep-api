package com.everkeep.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record SessionRequest(
        @NotBlank
        String email,

        @NotBlank
        String password
) { }
