package com.everkeep.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;

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

    NotePriority priority;

    LocalDateTime endDate;
}
