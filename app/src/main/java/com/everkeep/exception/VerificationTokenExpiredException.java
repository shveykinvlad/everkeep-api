package com.everkeep.exception;

import lombok.Getter;

@Getter
public class VerificationTokenExpiredException extends RuntimeException {

    private final String token;

    public VerificationTokenExpiredException(String message, String token) {
        super(message);
        this.token = token;
    }
}
