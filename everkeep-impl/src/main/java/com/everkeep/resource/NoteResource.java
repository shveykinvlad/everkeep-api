package com.everkeep.resource;

import com.everkeep.dto.NoteDto;
import com.everkeep.model.Note;
import com.everkeep.service.NoteService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/note")
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
