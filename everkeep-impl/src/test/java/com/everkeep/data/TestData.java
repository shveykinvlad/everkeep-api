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
                        .setText("text"),
                new Note()
                        .setText("text"),
                new Note()
                        .setText("text"));
    }

    public NoteDto getNoteDto() {
        return new NoteDto()
                .setText("text");
    }
}
