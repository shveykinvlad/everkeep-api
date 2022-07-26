package com.everkeep.controller.dto;

import javax.validation.constraints.NotBlank;

import lombok.Builder;

@Builder
public record SessionResponse(
        @NotBlank
        String jwt,

        @NotBlank
        String refreshToken,

        @NotBlank
        String email
) { }
