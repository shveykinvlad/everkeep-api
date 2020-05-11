package com.everkeep.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class NoteDto {

    private Long id;

    @NotBlank
    private String title;
    @NotBlank
    private String text;
}
