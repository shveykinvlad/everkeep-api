package com.everkeep.data;

import com.everkeep.dto.NoteDto;
import com.everkeep.model.Note;

import java.util.Arrays;
import java.util.List;

public class TestData {

    public Note getNote() {
        return new Note()
                .setText("text");
    }

    public List<Note> getNotes() {
        return Arrays.asList(
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
        return new NoteDto()
                .setText("text");
    }
}
