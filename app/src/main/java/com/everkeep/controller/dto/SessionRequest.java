package com.everkeep.controller.dto;

import javax.validation.constraints.NotBlank;

public record SessionRequest(
        @NotBlank
        String email,

        @NotBlank
        String password
) { }
