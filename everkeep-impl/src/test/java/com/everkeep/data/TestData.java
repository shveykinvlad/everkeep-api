package com.everkeep.data;

import java.util.List;

import com.everkeep.dto.AuthRequest;
import com.everkeep.dto.NoteDto;
import com.everkeep.model.Note;
import com.everkeep.model.security.Role;
import com.everkeep.model.security.User;

public class TestData {

    public Note getNote() {
        return new Note()
                .setText("text")
                .setTitle("title");
    }

    public List<Note> getNotes() {
        return List.of(
                new Note()
                        .setText("text one")
                        .setTitle("title one"),
                new Note()
                        .setText("text two")
                        .setTitle("title two"),
                new Note()
                        .setText("text three")
                        .setTitle("title three"));
    }

    public NoteDto getNoteDto() {
        return NoteDto.builder()
                .text("text")
                .title("title")
                .build();
    }

    public User getUser() {
        return new User()
                .setEmail("user@user.com")
                .setPassword("$2a$10$gsBrwudDcRaiRpHAT/Jtl.jUFm4gUF9ahdlLWcW8UF4M18wIm6a9K") // user
                .setEnabled(true);
    }

    public Role getUserRole() {
        return new Role()
                .setName("ROLE_USER");
    }

    public AuthRequest getUserAuthenticationRequest() {
        return new AuthRequest("user@user.com", "user");
    }
}
