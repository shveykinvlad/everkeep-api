package com.everkeep.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import com.everkeep.AbstractTest;
import com.everkeep.model.Note;
import com.everkeep.model.NotePriority;
import com.everkeep.repository.NoteRepository;

@SpringBootTest(classes = NoteService.class)
class NoteServiceTest extends AbstractTest {

    @Autowired
    private NoteService noteService;
    @MockBean
    private UserService userService;
    @MockBean
    private NoteRepository noteRepository;

    @Test
    void getAll() {
        var username = "one@localhost";
        var note = Note.builder()
                .id(1L)
                .title("First title")
                .text("First text")
                .priority(NotePriority.NONE)
                .username(username)
                .build();
        var savedNotes = List.of(note);
        when(userService.getAuthenticatedUsername()).thenReturn(username);
        when(noteRepository.findByUsername(username)).thenReturn(savedNotes);

        var receivedNotes = noteService.getAll();

        assertIterableEquals(savedNotes, receivedNotes);
    }

    @Test
    void getById() {
        var id = 1L;
        var savedNote = Note.builder()
                .id(id)
                .title("Second title")
                .text("Second text")
                .priority(NotePriority.NONE)
                .username("two@localhost")
                .build();
        when(userService.getAuthenticatedUsername()).thenReturn(savedNote.getUsername());
        when(noteRepository.findByIdAndUsername(savedNote.getId(), savedNote.getUsername())).thenReturn(Optional.of(savedNote));

        var receivedNote = noteService.get(id);

        assertEquals(savedNote, receivedNote);
    }

    @Test
    void getByIdIfNotFound() {
        var id = 1L;
        var username = "unknown@localhost";
        when(userService.getAuthenticatedUsername()).thenReturn(username);
        when(noteRepository.findByIdAndUsername(id, username)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> noteService.get(id));
    }

    @Test
    void search() {
        var note = Note.builder()
                .id(1L)
                .title("Third title")
                .text("Third text")
                .priority(NotePriority.NONE)
                .username("three@localhost")
                .build();
        var savedNotes = List.of(note);
        when(userService.getAuthenticatedUsername()).thenReturn(note.getUsername());
        when(noteRepository.findAll(Mockito.<Specification<Note>>any())).thenReturn(savedNotes);

        var receivedNotes = noteService.search(note.getTitle());

        assertEquals(savedNotes, receivedNotes);
    }

    @Test
    void save() {
        var note = Note.builder()
                .id(1L)
                .title("Fourth title")
                .text("Fourth text")
                .priority(NotePriority.NONE)
                .username("four@localhost")
                .build();

        noteService.save(note);

        verify(noteRepository).save(note);
    }

    @Test
    void update() {
        var id = 1L;
        var note = Note.builder()
                .id(id)
                .title("Fifth title")
                .text("Fifth text")
                .priority(NotePriority.NONE)
                .username("five@localhost")
                .build();
        when(userService.getAuthenticatedUsername()).thenReturn(note.getUsername());
        when(noteRepository.findByIdAndUsername(id, note.getUsername())).thenReturn(Optional.of(note));

        noteService.update(note);

        verify(noteRepository).save(note);
    }

    @Test
    void updateIfHasNotPermissions() {
        var id = 1L;
        var illegalUsername = "banned@localhost";
        var note = Note.builder()
                .id(id)
                .title("Sixth title")
                .text("Sixth text")
                .priority(NotePriority.NONE)
                .username("six@localhost")
                .build();
        when(userService.getAuthenticatedUsername()).thenReturn(illegalUsername);
        when(noteRepository.findByIdAndUsername(id, illegalUsername)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> noteService.update(note));
    }

    @Test
    void delete() {
        var username = "seven@localhost";
        var id = 1L;
        when(userService.getAuthenticatedUsername()).thenReturn(username);

        noteService.delete(id);

        verify(noteRepository).deleteByIdAndUsername(id, username);
    }
}
