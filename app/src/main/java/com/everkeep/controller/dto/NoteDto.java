package com.everkeep.controller.dto;

import lombok.Builder;

import com.everkeep.model.NotePriority;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
