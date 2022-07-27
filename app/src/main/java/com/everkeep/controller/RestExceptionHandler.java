package com.everkeep.controller;

import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.everkeep.controller.dto.ErrorDto;
import com.everkeep.exception.NoteNotFoundException;
import com.everkeep.exception.UserAlreadyEnabledException;
import com.everkeep.exception.UserAlreadyExistsException;
import com.everkeep.exception.VerificationTokenExpiredException;
import com.everkeep.exception.VerificationTokenNotFoundException;

@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            NoteNotFoundException.class,
            VerificationTokenNotFoundException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ErrorDto> handleNotFoundException(Exception ex) {
        return new ResponseEntity<>(buildErrorDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            UserAlreadyEnabledException.class,
            UserAlreadyExistsException.class
    })
    public ResponseEntity<ErrorDto> handleInvalidStateException(Exception ex) {
        return new ResponseEntity<>(buildErrorDto(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthenticationException(Exception ex) {
        return new ResponseEntity<>(buildErrorDto(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ErrorDto> handleVerificationTokenExpiredException(Exception ex) {
        return new ResponseEntity<>(buildErrorDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorDto> handleMailException(MailException ex) {
        return new ResponseEntity<>(buildErrorDto(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException ex) {
        return new ResponseEntity<>(buildErrorDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private ErrorDto buildErrorDto(String message) {
        return ErrorDto.builder()
                .message(message)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
