package com.everkeep.config.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.everkeep.dto.NoteDto;
import com.everkeep.model.Note;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {

    NoteDto map(Note note);

    Note map(NoteDto noteDto);

    List<NoteDto> map(List<Note> notes);
}
