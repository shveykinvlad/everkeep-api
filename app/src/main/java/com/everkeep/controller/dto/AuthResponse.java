package com.everkeep.controller.dto;

import javax.validation.constraints.NotBlank;

import lombok.Builder;

@Builder
public record AuthResponse(
        @NotBlank
        String jwt,

        @NotBlank
        String refreshTokenValue,

        @NotBlank
        String userEmail
) { }
