package com.everkeep.controller.dto;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record ErrorDto(
        String message,

        OffsetDateTime timestamp
) { }
