package com.everkeep.controller;

import static com.everkeep.utils.Headers.X_API_KEY;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.controller.dto.SessionRequest;
import com.everkeep.controller.dto.SessionResponse;
import com.everkeep.service.SessionService;

@RestController
@RequestMapping(SessionController.SESSIONS_URL)
@RequiredArgsConstructor
public class SessionController {

    public static final String SESSIONS_URL = "/api/sessions";

    private final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Create session")
    public SessionResponse create(@RequestBody @Valid SessionRequest request) {
        return sessionService.create(request.email(), request.password());
    }

    @PutMapping
    @Operation(summary = "Update session")
    public SessionResponse update(@RequestHeader(X_API_KEY) @NotBlank String token) {
        return sessionService.update(token);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete session")
    public void delete(@RequestHeader(X_API_KEY) @NotBlank String token) {
        sessionService.delete(token);
    }
}
