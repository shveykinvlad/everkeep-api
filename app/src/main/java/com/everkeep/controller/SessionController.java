package com.everkeep.controller;

import com.everkeep.controller.dto.SessionRequest;
import com.everkeep.controller.dto.SessionResponse;
import com.everkeep.service.SessionService;
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

@RestController
@RequestMapping(SessionController.SESSIONS_URL)
@RequiredArgsConstructor
public class SessionController {

    public static final String SESSIONS_URL = "/api/sessions";

    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";

    private final SessionService sessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create")
    public SessionResponse create(@RequestBody @Valid SessionRequest request) {
        return sessionService.create(request.email(), request.password());
    }

    @PutMapping
    @Operation(summary = "Update")
    public SessionResponse update(@RequestHeader(REFRESH_TOKEN_HEADER) @NotBlank String refreshToken) {
        return sessionService.update(refreshToken);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete")
    public void delete(@RequestHeader(REFRESH_TOKEN_HEADER) @NotBlank String refreshToken) {
        sessionService.delete(refreshToken);
    }
}
