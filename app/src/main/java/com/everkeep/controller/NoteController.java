package com.everkeep.controller;

import static com.everkeep.config.SwaggerConfig.SECURITY_SCHEME;

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
@RequestMapping(path = NoteController.NOTES_URL)
@RequiredArgsConstructor
public class NoteController {

    public static final String NOTES_URL = "/api/notes";
    public static final String SEARCH_URL = "/search";
    public static final String ID_URL = "/{id}";
    public static final String VALUE_PARAM = "value";

    private final NoteService noteService;

    @GetMapping
    @Operation(summary = "Get all notes", security = @SecurityRequirement(name = SECURITY_SCHEME))
    public List<NoteDto> getAll() {
        return NoteConverter.convert(noteService.getAll());
    }

    @GetMapping(ID_URL)
    @Operation(summary = "Get note by id", security = @SecurityRequirement(name = SECURITY_SCHEME))
    public NoteDto get(@PathVariable Long id) {
        return NoteConverter.convert(noteService.get(id));
    }

    @GetMapping(SEARCH_URL)
    @Operation(summary = "Get note by title", security = @SecurityRequirement(name = SECURITY_SCHEME))
    public List<NoteDto> search(@RequestParam(VALUE_PARAM) String value) {
        return NoteConverter.convert(noteService.search(value));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save note", security = @SecurityRequirement(name = SECURITY_SCHEME))
    public NoteDto save(@RequestBody @Valid NoteDto noteDto) {
        var note = NoteConverter.convert(noteDto);
        note = noteService.save(note);

        return NoteConverter.convert(note);
    }

    @PutMapping(ID_URL)
    @Operation(summary = "Update note", security = @SecurityRequirement(name = SECURITY_SCHEME))
    public NoteDto update(@RequestBody @Valid NoteDto noteDto) {
        var note = NoteConverter.convert(noteDto);
        note = noteService.update(note);

        return NoteConverter.convert(note);
    }

    @DeleteMapping(ID_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete note", security = @SecurityRequirement(name = SECURITY_SCHEME))
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }
}
