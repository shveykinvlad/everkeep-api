package com.everkeep.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Value;

import com.everkeep.enums.NotePriority;

@Value
@Builder
public class NoteDto {

    Long id;

    @NotBlank
    String title;

    @NotBlank
    String text;

    @NotNull
    NotePriority priority;
}
