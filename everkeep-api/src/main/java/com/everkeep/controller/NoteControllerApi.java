package com.everkeep.controller;

import java.util.List;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.everkeep.dto.NoteDto;

public interface NoteControllerApi {

    @Operation(summary = "Get all notes", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping
    List<NoteDto> getAll();

    @Operation(summary = "Get note by id", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/{id}")
    NoteDto get(@PathVariable("id") Long id);

    @Operation(summary = "Get note by title", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/search")
    List<NoteDto> get(@RequestParam String title);

    @Operation(summary = "Save note", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    NoteDto save(@RequestBody @Valid NoteDto noteDto);

    @Operation(summary = "Update note", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}")
    NoteDto update(@RequestBody @Valid NoteDto noteDto);

    @Operation(summary = "Delete note", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") Long id);
}
