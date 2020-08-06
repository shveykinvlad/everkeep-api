package com.everkeep.exception.security;

public class UserAlreadyExistException extends RuntimeException {

    private final String email;

    public UserAlreadyExistException(String message, String email) {
        super(message);
        this.email = email;
    }
}
