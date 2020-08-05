package com.everkeep.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NoteDto {

    private Long id;

    @NotBlank
    private String title;
    @NotBlank
    private String text;
}
