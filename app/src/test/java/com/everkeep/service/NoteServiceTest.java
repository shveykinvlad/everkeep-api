package com.everkeep.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.everkeep.AbstractTest;
import com.everkeep.exception.NoteNotFoundException;
import com.everkeep.model.Note;
import com.everkeep.model.NotePriority;
import com.everkeep.repository.NoteRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

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
        var username = "tarantino@localhost";
        var note = Note.builder()
                .id(1L)
                .title("Pulp Fiction")
                .text("""
                        The lives of two mob hitmen, a boxer, a gangster and his wife, \
                        and a pair of diner bandits intertwine in four tales of violence and redemption.
                        """)
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
                .title("The Good, the Bad and the Ugly")
                .text("""
                        A bounty hunting scam joins two men in an uneasy alliance against a third in a race \
                        to find a fortune in gold buried in a remote cemetery.
                        """)
                .priority(NotePriority.NONE)
                .username("leone@localhost")
                .build();
        when(userService.getAuthenticatedUsername()).thenReturn(savedNote.getUsername());
        when(noteRepository.findByIdAndUsername(savedNote.getId(), savedNote.getUsername()))
                .thenReturn(Optional.of(savedNote));

        var receivedNote = noteService.get(id);

        assertEquals(savedNote, receivedNote);
    }

    @Test
    void getByIdIfNotFound() {
        var id = 1L;
        var username = "unknown@localhost";
        when(userService.getAuthenticatedUsername()).thenReturn(username);
        when(noteRepository.findByIdAndUsername(id, username)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteService.get(id));
    }

    @Test
    void search() {
        var note = Note.builder()
                .id(1L)
                .title("Forrest Gump")
                .text("""
                        The presidencies of Kennedy and Johnson, the Vietnam War, \
                        the Watergate scandal and other historical events unfold from the perspective \
                        of an Alabama man with an IQ of 75, \
                        whose only desire is to be reunited with his childhood sweetheart.
                        """)
                .priority(NotePriority.NONE)
                .username("zemeckis@localhost")
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
                .title("Fight Club")
                .text("""
                        An insomniac office worker and a devil-may-care soap maker form \
                        an underground fight club that evolves into much more.
                        """)
                .priority(NotePriority.NONE)
                .username("fincher@localhost")
                .build();

        noteService.save(note);

        verify(noteRepository).save(note);
    }

    @Test
    void update() {
        var id = 1L;
        var note = Note.builder()
                .id(id)
                .title("Inception")
                .text("""
                        A thief who steals corporate secrets through the use of dream-sharing technology \
                        is given the inverse task of planting an idea into the mind of a C.E.O., \
                        but his tragic past may doom the project and his team to disaster.
                        """)
                .priority(NotePriority.NONE)
                .username("nolan@localhost")
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
                .title("The Matrix")
                .text("""
                        When a beautiful stranger leads computer hacker Neo to a forbidding underworld, \
                        he discovers the shocking truth--the life he knows is the \
                        elaborate deception of an evil cyber-intelligence.
                        """)
                .priority(NotePriority.NONE)
                .username("wachowski@localhost")
                .build();
        when(userService.getAuthenticatedUsername()).thenReturn(illegalUsername);
        when(noteRepository.findByIdAndUsername(id, illegalUsername)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> noteService.update(note));
    }

    @Test
    void delete() {
        var username = "dike@localhost";
        var id = 1L;
        when(userService.getAuthenticatedUsername()).thenReturn(username);

        noteService.delete(id);

        verify(noteRepository).deleteByIdAndUsername(id, username);
    }
}
