package com.everkeep.exception;

import lombok.Getter;

@Getter
public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(String message) {
        super(message);
    }
}
