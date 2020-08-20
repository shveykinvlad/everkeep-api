package com.everkeep.resource;

import java.util.Optional;

import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.everkeep.IntegrationTest;
import com.everkeep.data.TestData;
import com.everkeep.dto.AuthenticationResponse;
import com.everkeep.dto.NoteDto;
import com.everkeep.repository.NoteRepository;
import com.everkeep.repository.security.RoleRepository;
import com.everkeep.repository.security.UserRepository;

@IntegrationTest
class NoteResourceTest {

    private final TestData data = new TestData();

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MapperFacade mapper;

    @AfterEach
    void tearDown() {
        noteRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void getAll() {
        var expected = noteRepository.saveAll(data.getNotes());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getBasePath(), HttpMethod.GET, httpEntity, NoteDto[].class);

        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(expected.size(), actual.getBody().length);
    }

    @Test
    void get() {
        var expected = noteRepository.save(data.getNote());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getBasePath() + "/{id}", HttpMethod.GET, httpEntity, NoteDto.class, expected.getId());

        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(expected.getId(), actual.getBody().getId());
    }

    @Test
    void getByTitle() {
        var expected = noteRepository.saveAll(data.getNotes());

        var httpEntity = new HttpEntity<>(getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getBasePath() + "/find/?title={title}", HttpMethod.GET, httpEntity, NoteDto[].class, expected.get(0).getTitle());

        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(expected.get(0).getTitle(), actual.getBody()[0].getTitle());
    }

    @Test
    void save() {
        var httpEntity = new HttpEntity<>(data.getNoteDto(), getAuthorizationHeader());
        var expected = restTemplate.postForObject(getBasePath(), httpEntity, NoteDto.class);
        var expectedIdOptional = Optional.ofNullable(expected)
                .map(NoteDto::getId);

        Assertions.assertTrue(expectedIdOptional.isPresent());
    }

    @Test
    void update() {
        var noteDto = mapper.map(noteRepository.save(data.getNote()), NoteDto.class);
        noteDto.setText("updated text");

        var httpEntity = new HttpEntity<>(noteDto, getAuthorizationHeader());
        var actual = restTemplate.exchange(
                getBasePath() + "/{id}", HttpMethod.PUT, httpEntity, NoteDto.class, noteDto.getId());
        var actualText = Optional.ofNullable(actual)
                .map(HttpEntity::getBody)
                .map(NoteDto::getText)
                .orElse("");

        Assertions.assertEquals(noteDto.getText(), actualText);
    }

    @Test
    void delete() {
        var note = noteRepository.save(data.getNote());

        var httpEntity = new HttpEntity(getAuthorizationHeader());
        restTemplate.exchange(getBasePath() + "/{id}", HttpMethod.DELETE, httpEntity, NoteDto.class, note.getId());

        Assertions.assertTrue(noteRepository.findById(note.getId())
                .isEmpty());
    }

    private String getBasePath() {
        return "http://localhost:" + port + "/api/notes/";
    }

    private String getAuthenticationPath() {
        return "http://localhost:" + port + "/api/users/authenticate";
    }

    private void createUser() {
        var role = roleRepository.save(data.getUserRole());
        var user = data.getUser();
        user.getRoles().add(role);

        userRepository.save(user);
    }

    private String getJwtToken() {
        createUser();
        var authenticationEntity = new HttpEntity<>(data.getUserAuthenticationRequest(), null);
        var authentication = restTemplate.postForObject(
                getAuthenticationPath(), authenticationEntity, AuthenticationResponse.class);

        return authentication.getJwt();
    }

    private HttpHeaders getAuthorizationHeader() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + getJwtToken());

        return headers;
    }
}