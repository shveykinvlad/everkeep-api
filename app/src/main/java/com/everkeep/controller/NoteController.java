package com.everkeep.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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

import java.util.List;

import static com.everkeep.config.SwaggerConfig.BEARER;
import static com.everkeep.service.converter.NoteConverter.convert;

@RestController
@RequestMapping(NoteController.NOTES_URL)
@RequiredArgsConstructor
public class NoteController {

    public static final String NOTES_URL = "/api/notes";
    public static final String SEARCH_URL = "/search";
    public static final String ID_URL = "/{id}";
    public static final String TITLE_PARAM = "title";

    private final NoteService noteService;

    @GetMapping
    @Operation(summary = "Get all", security = @SecurityRequirement(name = BEARER))
    public List<NoteDto> getAll() {
        return convert(noteService.getAll());
    }

    @GetMapping(ID_URL)
    @Operation(summary = "Get by id", security = @SecurityRequirement(name = BEARER))
    public NoteDto get(@PathVariable Long id) {
        return convert(noteService.get(id));
    }

    @GetMapping(SEARCH_URL)
    @Operation(summary = "Get by title", security = @SecurityRequirement(name = BEARER))
    public List<NoteDto> search(@RequestParam(TITLE_PARAM) String title) {
        return convert(noteService.search(title));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create", security = @SecurityRequirement(name = BEARER))
    public NoteDto create(@RequestBody @Valid NoteDto noteDto) {
        var note = convert(noteDto);
        note = noteService.save(note);

        return convert(note);
    }

    @PutMapping(ID_URL)
    @Operation(summary = "Update", security = @SecurityRequirement(name = BEARER))
    public NoteDto update(@RequestBody @Valid NoteDto noteDto) {
        var note = convert(noteDto);
        note = noteService.update(note);

        return convert(note);
    }

    @DeleteMapping(ID_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete", security = @SecurityRequirement(name = BEARER))
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }
}
