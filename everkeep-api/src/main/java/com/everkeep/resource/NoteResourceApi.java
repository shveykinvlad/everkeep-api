package com.everkeep.resource;

import java.util.List;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
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

public interface NoteResourceApi {

    @ApiOperation(value = "Get all notes", authorizations = @Authorization(value = "Bearer"))
    @GetMapping
    List<NoteDto> getAll();

    @ApiOperation(value = "Get note by id", authorizations = @Authorization(value = "Bearer"))
    @GetMapping("/{id}")
    NoteDto get(@PathVariable("id") Long id);

    @ApiOperation(value = "Get note by title", authorizations = @Authorization(value = "Bearer"))
    @GetMapping("/search")
    List<NoteDto> get(@RequestParam String title);

    @ApiOperation(value = "Save note", authorizations = @Authorization(value = "Bearer"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    NoteDto save(@RequestBody @Valid NoteDto noteDto);

    @ApiOperation(value = "Update note", authorizations = @Authorization(value = "Bearer"))
    @PutMapping("/{id}")
    NoteDto update(@RequestBody @Valid NoteDto noteDto);

    @ApiOperation(value = "Delete note", authorizations = @Authorization(value = "Bearer"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") Long id);
}
