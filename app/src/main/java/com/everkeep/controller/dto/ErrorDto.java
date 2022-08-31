package com.everkeep.controller.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ErrorDto(
        String message,

        OffsetDateTime timestamp
) { }
