package com.everkeep.controller;

import javax.validation.Valid;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.controller.dto.NoteDto;
import com.everkeep.service.NoteService;
import com.everkeep.service.converter.NoteConverter;

@RestController
@RequestMapping(path = "/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "Get all notes", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping
    public List<NoteDto> getAll() {
        return NoteConverter.convert(noteService.getAll());
    }

    @Operation(summary = "Get note by id", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/{id}")
    public NoteDto get(@PathVariable("id") Long id) {
        return NoteConverter.convert(noteService.get(id));
    }

    @Operation(summary = "Get note by title", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/search")
    public List<NoteDto> get(@RequestParam String title) {
        return NoteConverter.convert(noteService.get(title));
    }

    @Operation(summary = "Save note", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteDto save(@RequestBody @Valid NoteDto noteDto) {
        var note = NoteConverter.convert(noteDto);
        note = noteService.save(note);

        return NoteConverter.convert(note);
    }

    @Operation(summary = "Update note", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}")
    public NoteDto update(@RequestBody @Valid NoteDto noteDto) {
        var note = NoteConverter.convert(noteDto);
        note = noteService.update(note);

        return NoteConverter.convert(note);
    }

    @Operation(summary = "Delete note", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        noteService.delete(id);
    }
}
