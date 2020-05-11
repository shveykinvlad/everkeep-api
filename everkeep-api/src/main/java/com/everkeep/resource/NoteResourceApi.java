package com.everkeep.resource;

import com.everkeep.dto.NoteDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public interface NoteResourceApi {

    @GetMapping
    List<NoteDto> getAll();

    @GetMapping("/{id}")
    NoteDto get(@PathVariable("id") Long id);

    @GetMapping("/find")
    List<NoteDto> get(@RequestParam String title);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    NoteDto save(@RequestBody NoteDto noteDto);

    @PutMapping("/{id}")
    NoteDto update(@RequestBody NoteDto noteDto);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") Long id);
}
