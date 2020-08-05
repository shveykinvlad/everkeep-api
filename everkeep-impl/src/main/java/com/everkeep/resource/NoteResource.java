package com.everkeep.resource;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.dto.NoteDto;
import com.everkeep.model.Note;
import com.everkeep.service.NoteService;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/notes")
@RequiredArgsConstructor
public class NoteResource implements NoteResourceApi {

    private final NoteService noteService;
    private final MapperFacade mapper;

    @Override
    public List<NoteDto> getAll() {
        return mapper.mapAsList(noteService.getAll(), NoteDto.class);
    }

    @Override
    public NoteDto get(Long id) {
        return mapper.map(noteService.get(id), NoteDto.class);
    }

    @Override
    public List<NoteDto> get(String title) {
        return mapper.mapAsList(noteService.get(title), NoteDto.class);
    }

    @Override
    public NoteDto save(NoteDto noteDto) {
        var note = mapper.map(noteDto, Note.class);
        note = noteService.save(note);

        return mapper.map(note, NoteDto.class);
    }

    @Override
    public NoteDto update(NoteDto noteDto) {
        var note = mapper.map(noteDto, Note.class);
        note = noteService.update(note);

        return mapper.map(note, NoteDto.class);
    }

    @Override
    public void delete(Long id) {
        noteService.delete(id);
    }
}
