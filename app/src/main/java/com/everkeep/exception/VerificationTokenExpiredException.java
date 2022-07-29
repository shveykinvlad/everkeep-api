package com.everkeep.exception;

import lombok.Getter;

@Getter
public class VerificationTokenExpiredException extends RuntimeException {

    private final String token;
    private final String email;

    public VerificationTokenExpiredException(String message, String token, String email) {
        super(message);
        this.token = token;
        this.email = email;
    }
}
