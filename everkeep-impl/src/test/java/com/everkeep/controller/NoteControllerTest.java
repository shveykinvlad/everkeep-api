package com.everkeep.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.everkeep.AbstractIntegrationTest;
import com.everkeep.config.mapper.NoteMapper;
import com.everkeep.dto.NoteDto;
import com.everkeep.enums.NotePriority;
import com.everkeep.model.Note;
import com.everkeep.repository.NoteRepository;

class NoteControllerTest extends AbstractIntegrationTest {

    private static final String USERNAME = "user@user.com";
    private static final String JWT = "eyJhbGciOiJIUzUxMiJ9."
            + "eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoidXNlckB1c2VyLmNvbSIsImlhdCI6MTYwNjU5MDYwMSwiZXhwIjo0NzYwMTkwNjAxfQ."
            + "6dhRZKmVBsxzpC0oEVcp_7dH0-kazdzKO8RSNd0s0ebwgQltxUoTvZKpjCCgsT-5gZKbQgHi_-U_F2BW7Q4Tkg";

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private NoteMapper noteMapper;

    @AfterEach
    void tearDown() {
        noteRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getAll() {
        var expected = noteRepository.saveAll(getNotes());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getPath(), HttpMethod.GET, httpEntity, NoteDto[].class);

        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(expected.size(), actual.getBody().length);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void get() {
        var expected = noteRepository.save(getNote());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getPath() + "/{id}", HttpMethod.GET, httpEntity, NoteDto.class, expected.getId());

        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(expected.getId(), actual.getBody().getId());
    }

    @Test
    @WithMockUser(username = "user@user.com")
    void getByTitle() {
        var expected = noteRepository.saveAll(getNotes());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getPath() + "/search/?title={title}", HttpMethod.GET, httpEntity, NoteDto[].class, expected.get(0).getTitle());

        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(expected.get(0).getTitle(), actual.getBody()[0].getTitle());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void save() {
        var httpEntity = new HttpEntity<>(getNoteDto(), getAuthorizationHeader());
        var expected = restTemplate.postForObject(getPath(), httpEntity, NoteDto.class);
        var expectedIdOptional = Optional.ofNullable(expected)
                .map(NoteDto::getId);

        Assertions.assertTrue(expectedIdOptional.isPresent());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void update() {
        var noteDto = noteMapper.map(noteRepository.save(getNote()));
        var updatedNoteDto = NoteDto.builder()
                .id(noteDto.getId())
                .title(noteDto.getTitle())
                .text("updated text")
                .priority(NotePriority.LOW)
                .build();

        var httpEntity = new HttpEntity<>(updatedNoteDto, getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getPath() + "/{id}", HttpMethod.PUT, httpEntity, NoteDto.class, updatedNoteDto.getId());
        var actualText = Optional.ofNullable(actual)
                .map(HttpEntity::getBody)
                .map(NoteDto::getText)
                .orElse("");

        Assertions.assertEquals(updatedNoteDto.getText(), actualText);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void delete() {
        var note = noteRepository.save(getNote());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        restTemplate.exchange(getPath() + "/{id}", HttpMethod.DELETE, httpEntity, NoteDto.class, note.getId());

        Assertions.assertTrue(noteRepository.findById(note.getId())
                .isEmpty());
    }

    private HttpHeaders getAuthorizationHeader() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + JWT);

        return headers;
    }

    @Override
    protected String getControllerPath() {
        return "/api/notes/";
    }

    private List<Note> getNotes() {
        return List.of(
                new Note()
                        .setText("text one")
                        .setTitle("title one")
                        .setPriority(NotePriority.LOW)
                        .setUsername(USERNAME),
                new Note()
                        .setText("text two")
                        .setTitle("title two")
                        .setPriority(NotePriority.MEDIUM)
                        .setUsername(USERNAME),
                new Note()
                        .setText("text three")
                        .setTitle("title three")
                        .setPriority(NotePriority.HIGH)
                        .setUsername(USERNAME));
    }

    public Note getNote() {
        return new Note()
                .setId(1L)
                .setText("text")
                .setTitle("title")
                .setPriority(NotePriority.NONE)
                .setUsername(USERNAME);
    }

    public NoteDto getNoteDto() {
        return NoteDto.builder()
                .text("text")
                .title("title")
                .priority(NotePriority.NONE)
                .build();
    }
}
