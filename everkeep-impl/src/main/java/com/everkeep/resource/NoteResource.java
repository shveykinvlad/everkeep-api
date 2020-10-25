package com.everkeep.resource;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.config.mapper.NoteMapper;
import com.everkeep.dto.NoteDto;
import com.everkeep.service.NoteService;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/notes")
@RequiredArgsConstructor
public class NoteResource implements NoteResourceApi {

    private final NoteService noteService;
    private final NoteMapper mapper;

    @Override
    public List<NoteDto> getAll() {
        return mapper.map(noteService.getAll());
    }

    @Override
    public NoteDto get(Long id) {
        return mapper.map(noteService.get(id));
    }

    @Override
    public List<NoteDto> get(String title) {
        return mapper.map(noteService.get(title));
    }

    @Override
    public NoteDto save(NoteDto noteDto) {
        var note = mapper.map(noteDto);
        note = noteService.save(note);

        return mapper.map(note);
    }

    @Override
    public NoteDto update(NoteDto noteDto) {
        var note = mapper.map(noteDto);
        note = noteService.update(note);

        return mapper.map(note);
    }

    @Override
    public void delete(Long id) {
        noteService.delete(id);
    }
}
