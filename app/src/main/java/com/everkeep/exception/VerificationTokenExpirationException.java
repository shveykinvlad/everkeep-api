package com.everkeep.exception;

import lombok.Getter;

@Getter
public class VerificationTokenExpirationException extends RuntimeException {

    private final String token;

    public VerificationTokenExpirationException(String message, String token) {
        super(message);
        this.token = token;
    }
}
