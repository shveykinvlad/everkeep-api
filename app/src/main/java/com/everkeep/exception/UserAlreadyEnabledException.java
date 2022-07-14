package com.everkeep.exception;

import lombok.Getter;

@Getter
public class UserAlreadyEnabledException extends RuntimeException {

    private final String email;

    public UserAlreadyEnabledException(String message, String email) {
        super(message);
        this.email = email;
    }
}

