package com.everkeep.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.experimental.Accessors;

import com.everkeep.enums.NotePriority;

@Data
@Accessors(chain = true)
public class NoteDto {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String text;

    private NotePriority priority;

    private LocalDateTime endDate;
}
