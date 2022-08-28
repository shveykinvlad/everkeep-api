package com.everkeep.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.everkeep.model.NotePriority;
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
