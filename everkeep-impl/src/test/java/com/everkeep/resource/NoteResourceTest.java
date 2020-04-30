package com.everkeep.resource;

import com.everkeep.IntegrationTest;
import com.everkeep.data.TestData;
import com.everkeep.dto.NoteDto;
import com.everkeep.model.Note;
import com.everkeep.repository.NoteRepository;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@IntegrationTest
public class NoteResourceTest {

    private final TestData data = new TestData();

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private MapperFacade mapper;

    private String getBasePath() {
        return "http://localhost:" + port + "/note/";
    }

    @AfterEach
    void tearDown() {
        noteRepository.deleteAll();
    }


    @Test
    public void getAll() {
        var expected = noteRepository.saveAll(data.getNotes());

        var actual = restTemplate.getForObject(getBasePath(), NoteDto[].class);

        Assertions.assertEquals(expected.size(), actual.length);
    }

    @Test
    public void get() {
        var expected = noteRepository.save(data.getNote());

        var actual = restTemplate.getForObject(getBasePath() + "/{id}", NoteDto.class, expected.getId());

        Assertions.assertEquals(actual.getId(), expected.getId());
    }

    @Test
    public void save() {
        var httpEntity = new HttpEntity<>(data.getNoteDto(), null);

        var expected = restTemplate.postForObject(getBasePath(), httpEntity, NoteDto.class);
        var expectedIdOptional = Optional.ofNullable(expected)
                .map(NoteDto::getId);

        Assertions.assertTrue(expectedIdOptional.isPresent());
    }

    @Test
    public void update() {
        var noteDto = mapper.map(noteRepository.save(data.getNote()), NoteDto.class);
        noteDto.setText("updated text");
        var httpEntity = new HttpEntity<>(noteDto, null);

        var actual = restTemplate.exchange(getBasePath() + "/{id}", HttpMethod.PUT, httpEntity, NoteDto.class, noteDto.getId());
        var actualText = Optional.ofNullable(actual)
                .map(HttpEntity::getBody)
                .map(NoteDto::getText)
                .orElse("");

        Assertions.assertEquals(actualText, noteDto.getText());
    }

    @Test
    public void delete() {
        var note = noteRepository.save(data.getNote());

        restTemplate.delete(getBasePath() + "/{id}", note.getId());

        Assertions.assertTrue(noteRepository.findById(note.getId())
                .isEmpty());
    }
}