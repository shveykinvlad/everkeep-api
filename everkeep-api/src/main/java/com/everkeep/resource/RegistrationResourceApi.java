package com.everkeep.resource;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.everkeep.dto.UserDto;

public interface RegistrationResourceApi {

    @PostMapping
    void register(@RequestBody @Valid UserDto userDto, HttpServletRequest request);

    @GetMapping("/confirm")
    void confirm(@RequestParam("token") String token);
}
