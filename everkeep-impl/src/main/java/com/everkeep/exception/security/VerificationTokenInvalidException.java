package com.everkeep.exception.security;

public class VerificationTokenInvalidException extends RuntimeException {

    public VerificationTokenInvalidException(String message) {
        super(message);
    }
}
