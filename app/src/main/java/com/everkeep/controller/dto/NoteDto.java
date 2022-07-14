package com.everkeep.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;

import com.everkeep.model.NotePriority;

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
