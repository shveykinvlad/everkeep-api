package com.everkeep.config.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.everkeep.dto.NoteDto;
import com.everkeep.model.Note;

@Mapper
public interface NoteMapper {

    NoteDto map(Note note);

    Note map(NoteDto noteDto);

    List<NoteDto> map(List<Note> notes);
}
