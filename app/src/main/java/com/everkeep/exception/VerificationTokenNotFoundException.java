package com.everkeep.exception;

import lombok.Getter;

@Getter
public class VerificationTokenNotFoundException extends RuntimeException {

    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}

