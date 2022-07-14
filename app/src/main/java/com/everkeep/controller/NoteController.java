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

    @GetMapping
    @Operation(summary = "Get all notes", security = @SecurityRequirement(name = "Bearer"))
    public List<NoteDto> getAll() {
        return NoteConverter.convert(noteService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note by id", security = @SecurityRequirement(name = "Bearer"))
    public NoteDto get(@PathVariable("id") Long id) {
        return NoteConverter.convert(noteService.get(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Get note by title", security = @SecurityRequirement(name = "Bearer"))
    public List<NoteDto> get(@RequestParam String title) {
        return NoteConverter.convert(noteService.get(title));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save note", security = @SecurityRequirement(name = "Bearer"))
    public NoteDto save(@RequestBody @Valid NoteDto noteDto) {
        var note = NoteConverter.convert(noteDto);
        note = noteService.save(note);

        return NoteConverter.convert(note);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update note", security = @SecurityRequirement(name = "Bearer"))
    public NoteDto update(@RequestBody @Valid NoteDto noteDto) {
        var note = NoteConverter.convert(noteDto);
        note = noteService.update(note);

        return NoteConverter.convert(note);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete note", security = @SecurityRequirement(name = "Bearer"))
    public void delete(@PathVariable("id") Long id) {
        noteService.delete(id);
    }
}
