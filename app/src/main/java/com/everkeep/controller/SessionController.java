package com.everkeep.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

import static com.everkeep.utils.Headers.REFRESH_TOKEN;

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
    public SessionResponse update(@RequestHeader(REFRESH_TOKEN) @NotBlank String refreshToken) {
        return sessionService.update(refreshToken);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete session")
    public void delete(@RequestHeader(REFRESH_TOKEN) @NotBlank String refreshToken) {
        sessionService.delete(refreshToken);
    }
}
