package com.everkeep.controller.dto;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Builder
public record SessionResponse(
        @NotBlank
        String jwt,

        @NotBlank
        String refreshToken,

        @NotBlank
        String email
) { }
