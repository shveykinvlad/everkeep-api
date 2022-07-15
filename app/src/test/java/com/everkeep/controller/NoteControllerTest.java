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
import com.everkeep.controller.dto.NoteDto;
import com.everkeep.model.Note;
import com.everkeep.model.NotePriority;
import com.everkeep.model.User;
import com.everkeep.repository.NoteRepository;
import com.everkeep.repository.UserRepository;
import com.everkeep.service.converter.NoteConverter;

class NoteControllerTest extends AbstractIntegrationTest {

    private static final String USERNAME = "user@user.com";
    private static final String JWT = "eyJhbGciOiJIUzUxMiJ9."
            + "eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoidXNlckB1c2VyLmNvbSIsImlhdCI6MTYwNjU5MDYwMSwiZXhwIjo0NzYwMTkwNjAxfQ."
            + "6dhRZKmVBsxzpC0oEVcp_7dH0-kazdzKO8RSNd0s0ebwgQltxUoTvZKpjCCgsT-5gZKbQgHi_-U_F2BW7Q4Tkg";

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        noteRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getAll() {
        userRepository.save(getUser());
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
        userRepository.save(getUser());
        var expected = noteRepository.save(getNote());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getPath() + "/{id}", HttpMethod.GET, httpEntity, NoteDto.class, expected.getId());

        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(expected.getId(), actual.getBody().id());
    }

    @Test
    @WithMockUser(username = "user@user.com")
    void search() {
        userRepository.save(getUser());
        var expected = noteRepository.saveAll(getNotes());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getPath() + "/search/?value={value}", HttpMethod.GET, httpEntity, NoteDto[].class, expected.get(0).getTitle());

        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(expected.get(0).getTitle(), actual.getBody()[0].title());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void save() {
        userRepository.save(getUser());
        var httpEntity = new HttpEntity<>(getNoteDto(), getAuthorizationHeader());
        var expected = restTemplate.postForObject(getPath(), httpEntity, NoteDto.class);
        var expectedIdOptional = Optional.ofNullable(expected)
                .map(NoteDto::id);

        Assertions.assertTrue(expectedIdOptional.isPresent());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void update() {
        userRepository.save(getUser());
        var noteDto = NoteConverter.convert(noteRepository.save(getNote()));
        var updatedNoteDto = NoteDto.builder()
                .id(noteDto.id())
                .title(noteDto.title())
                .text("updated text")
                .priority(NotePriority.LOW)
                .build();

        var httpEntity = new HttpEntity<>(updatedNoteDto, getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getPath() + "/{id}", HttpMethod.PUT, httpEntity, NoteDto.class, updatedNoteDto.id());
        var actualText = Optional.ofNullable(actual)
                .map(HttpEntity::getBody)
                .map(NoteDto::text)
                .orElse("");

        Assertions.assertEquals(updatedNoteDto.text(), actualText);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void delete() {
        userRepository.save(getUser());
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
                Note.builder()
                        .text("text one")
                        .title("title one")
                        .priority(NotePriority.LOW)
                        .username(USERNAME)
                        .build(),
                Note.builder()
                        .text("text two")
                        .title("title two")
                        .priority(NotePriority.MEDIUM)
                        .username(USERNAME)
                        .build(),
                Note.builder()
                        .text("text three")
                        .title("title three")
                        .priority(NotePriority.HIGH)
                        .username(USERNAME)
                        .build());
    }

    public Note getNote() {
        return Note.builder()
                .id(1L)
                .text("text")
                .title("title")
                .priority(NotePriority.NONE)
                .username(USERNAME)
                .build();
    }

    public User getUser() {
        return User.builder()
                .id(1L)
                .password("password")
                .email(USERNAME)
                .enabled(true)
                .build();
    }

    public NoteDto getNoteDto() {
        return NoteDto.builder()
                .text("text")
                .title("title")
                .priority(NotePriority.NONE)
                .build();
    }
}
