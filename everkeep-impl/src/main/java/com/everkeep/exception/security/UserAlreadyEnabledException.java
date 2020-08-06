package com.everkeep.exception.security;

public class UserAlreadyEnabledException extends RuntimeException {

    private final String email;

    public UserAlreadyEnabledException(String message, String email) {
        super(message);
        this.email = email;
    }
}

