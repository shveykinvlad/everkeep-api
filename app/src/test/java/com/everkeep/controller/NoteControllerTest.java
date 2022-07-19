package com.everkeep.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.everkeep.AbstractIntegrationTest;
import com.everkeep.controller.dto.NoteDto;
import com.everkeep.model.Note;
import com.everkeep.model.NotePriority;
import com.everkeep.model.User;
import com.everkeep.repository.NoteRepository;
import com.everkeep.repository.UserRepository;
import com.everkeep.service.converter.NoteConverter;

class NoteControllerTest extends AbstractIntegrationTest {

    private static final String NOTES_URL = "/api/notes";
    private static final String ID_URL = "/{id}";
    private static final String SEARCH_URL = "/search";
    private static final String VALUE_PARAM = "value";

    private static final String USERNAME = "email@example.com";

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(buildUser());
    }

    @AfterEach
    void tearDown() {
        noteRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getAll() throws Exception {
        var note = noteRepository.save(
                Note.builder()
                        .title("First")
                        .text("First note")
                        .priority(NotePriority.NONE)
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.get(NOTES_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(note.getId()))
                .andExpect(jsonPath("$[0].title").value(note.getTitle()))
                .andExpect(jsonPath("$[0].text").value(note.getText()))
                .andExpect(jsonPath("$[0].priority").value(note.getPriority().name()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void get() throws Exception {
        var note = noteRepository.save(
                Note.builder()
                        .title("Second")
                        .text("Second note")
                        .priority(NotePriority.NONE)
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders.get(NOTES_URL + ID_URL, note.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(note.getId()))
                .andExpect(jsonPath("$.title").value(note.getTitle()))
                .andExpect(jsonPath("$.text").value(note.getText()))
                .andExpect(jsonPath("$.priority").value(note.getPriority().name()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void search() throws Exception {
        var note = noteRepository.save(
                Note.builder()
                        .title("Third")
                        .text("Third note")
                        .priority(NotePriority.NONE)
                        .build());

        mockMvc.perform(MockMvcRequestBuilders.get(NOTES_URL + SEARCH_URL)
                        .param(VALUE_PARAM, note.getTitle()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(note.getId()))
                .andExpect(jsonPath("$[0].title").value(note.getTitle()))
                .andExpect(jsonPath("$[0].text").value(note.getText()))
                .andExpect(jsonPath("$[0].priority").value(note.getPriority().name()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void save() throws Exception {
        var noteDto = NoteConverter.convert(
                Note.builder()
                        .title("Fourth")
                        .text("Fourth note")
                        .priority(NotePriority.NONE)
                        .build());

        mockMvc.perform(MockMvcRequestBuilders.post(NOTES_URL)
                        .content(mapper.writeValueAsString(noteDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(noteDto.title()))
                .andExpect(jsonPath("$.text").value(noteDto.text()))
                .andExpect(jsonPath("$.priority").value(noteDto.priority().name()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void update() throws Exception {
        var note = noteRepository.save(
                Note.builder()
                        .title("Fifth")
                        .text("Fifth note")
                        .priority(NotePriority.NONE)
                        .build()
        );
        var noteDto = NoteDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .text(note.getText())
                .priority(NotePriority.HIGH)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put(NOTES_URL + ID_URL, note.getId())
                        .content(mapper.writeValueAsString(noteDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(noteDto.title()))
                .andExpect(jsonPath("$.text").value(noteDto.text()))
                .andExpect(jsonPath("$.priority").value(noteDto.priority().name()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void delete() throws Exception {
        var note = noteRepository.save(Note.builder()
                .title("Sixth")
                .text("Sixth note")
                .priority(NotePriority.NONE)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.delete(NOTES_URL + ID_URL, note.getId()))
                .andExpect(status().isNoContent());

        assertFalse(noteRepository.existsById(note.getId()));
    }

    private User buildUser() {
        return User.builder()
                .password("$2a$10$l13RhzScYa0XCo4AGvbxTe2/f7W8.0b5bLf5Plwq713G15rcxlpJe")
                .email(USERNAME)
                .enabled(true)
                .build();
    }
}
