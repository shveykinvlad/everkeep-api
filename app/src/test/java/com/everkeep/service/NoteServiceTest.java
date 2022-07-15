package com.everkeep.service;

import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import com.everkeep.AbstractTest;
import com.everkeep.model.Note;
import com.everkeep.repository.NoteRepository;

@SpringBootTest(classes = NoteService.class)
class NoteServiceTest extends AbstractTest {

    private static final Long ID = 1L;
    private static final String USERNAME = "username";
    private static final String TITLE = "title";

    @Autowired
    private NoteService noteService;
    @MockBean
    private UserService userService;
    @MockBean
    private NoteRepository noteRepository;

    @AfterEach
    void tearDown() {
        Mockito.reset(userService, noteRepository);
    }

    @Test
    void getAll() {
        var username = USERNAME;
        var note = Note.builder()
                .username(USERNAME)
                .build();
        var expected = List.of(note);

        when(userService.getAuthenticatedUsername()).thenReturn(username);
        when(noteRepository.findByUsername(username)).thenReturn(expected);
        var actual = noteService.getAll();

        Assertions.assertIterableEquals(expected, actual);
    }

    @Test
    void getById() {
        var username = USERNAME;
        var id = ID;
        var expected = Note.builder()
                .id(id)
                .username(USERNAME)
                .build();

        when(userService.getAuthenticatedUsername()).thenReturn(username);
        when(noteRepository.findByIdAndUsername(id, username)).thenReturn(Optional.of(expected));
        var actual = noteService.get(id);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getByIdIfNotFound() {
        var id = ID;
        var username = USERNAME;

        when(userService.getAuthenticatedUsername()).thenReturn(username);
        when(noteRepository.findByIdAndUsername(id, username)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> noteService.get(id));
    }

    @Test
    void search() {
        var username = USERNAME;
        var title = TITLE;
        var note = Note.builder()
                .username(username)
                .title(title)
                .build();
        var expected = List.of(note);

        when(userService.getAuthenticatedUsername()).thenReturn(username);
        when(noteRepository.findAll(Mockito.<Specification<Note>>any())).thenReturn(expected);
        var actual = noteService.search(title);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void save() {
        var note = new Note();
        noteService.save(note);

        Mockito.verify(noteRepository).save(note);
    }

    @Test
    void update() {
        var id = ID;
        var username = USERNAME;
        var note = Note.builder()
                .id(id)
                .username(username)
                .build();

        when(userService.getAuthenticatedUsername()).thenReturn(username);
        when(noteRepository.findByIdAndUsername(id, username)).thenReturn(Optional.of(note));
        noteService.update(note);

        Mockito.verify(noteRepository).save(note);
    }

    @Test
    void updateIfHasNotPermissions() {
        var id = ID;
        var illegalUsername = USERNAME;
        var note = Note.builder()
                .id(id)
                .username(USERNAME)
                .build();

        when(userService.getAuthenticatedUsername()).thenReturn(illegalUsername);
        when(noteRepository.findByIdAndUsername(id, illegalUsername)).thenReturn(Optional.empty());

        Assertions.assertThrows(AccessDeniedException.class, () -> noteService.update(note));
    }

    @Test
    void delete() {
        var username = USERNAME;
        var id = ID;

        when(userService.getAuthenticatedUsername()).thenReturn(username);
        noteService.delete(id);

        Mockito.verify(noteRepository).deleteByIdAndUsername(id, username);
    }
}
