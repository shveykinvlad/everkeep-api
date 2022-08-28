package com.everkeep.service.converter;

import lombok.experimental.UtilityClass;

import com.everkeep.controller.dto.NoteDto;
import com.everkeep.model.Note;

import java.util.List;

@UtilityClass
public class NoteConverter {

    public static NoteDto convert(Note note) {
        return NoteDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .text(note.getText())
                .priority(note.getPriority())
                .build();
    }

    public static Note convert(NoteDto noteDto) {
        return Note.builder()
                .id(noteDto.id())
                .title(noteDto.title())
                .text(noteDto.text())
                .priority(noteDto.priority())
                .build();
    }

    public static List<NoteDto> convert(List<Note> notes) {
        return notes.stream()
                .map(NoteConverter::convert)
                .toList();
    }
}
