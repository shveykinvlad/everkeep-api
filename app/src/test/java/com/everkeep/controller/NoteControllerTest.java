package com.everkeep.controller;

import static com.everkeep.controller.NoteController.ID_URL;
import static com.everkeep.controller.NoteController.NOTES_URL;
import static com.everkeep.controller.NoteController.SEARCH_URL;
import static com.everkeep.controller.NoteController.VALUE_PARAM;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private static final String USERNAME = "email@localhost";

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
                        .title("The Shawshank Redemption")
                        .text("""
                                Two imprisoned men bond over a number of years, \
                                finding solace and eventual redemption through acts of common decency.""")
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
                        .title("The Godfather")
                        .text("""
                                The aging patriarch of an organized crime dynasty in postwar New York City \
                                transfers control of his clandestine empire to his reluctant youngest son.""")
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
                        .title("The Dark Knight")
                        .text("""
                                When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, \
                                Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice. \
                                """)
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
                        .title("12 Angry Men")
                        .text("""
                                The jury in a New York City murder trial is frustrated by a single member \
                                whose skeptical caution forces them to more carefully consider the evidence \
                                before jumping to a hasty verdict.""")
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

        var note = noteRepository.findAll().get(0);
        assertAll("Should persist username and creation timestamp",
                () -> assertEquals(USERNAME, note.getUsername()),
                () -> assertNotNull(note.getCreationTimestamp())
        );
    }

    @Test
    @WithMockUser(username = USERNAME)
    void update() throws Exception {
        var note = noteRepository.save(
                Note.builder()
                        .title("Schindler's List")
                        .text("""
                                In German-occupied Poland during World War II, \
                                industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce \
                                after witnessing their persecution by the Nazis.""")
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
        var note = noteRepository.save(
                Note.builder()
                        .title("The Lord of the Rings: The Return of the King")
                        .text("""
                                Gandalf and Aragorn lead the World of Men against Sauron's army to draw his gaze \
                                from Frodo and Sam as they approach Mount Doom with the One Ring.""")
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
