package com.everkeep.controller.dto;

import com.everkeep.model.NotePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record NoteDto(
        Long id,

        @NotBlank
        String title,

        @NotBlank
        String text,

        @NotNull
        NotePriority priority
) { }
