package com.everkeep.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VerificationTokenNotFoundException extends RuntimeException {

    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}

