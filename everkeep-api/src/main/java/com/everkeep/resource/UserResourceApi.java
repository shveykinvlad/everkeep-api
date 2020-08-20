package com.everkeep.resource;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.everkeep.dto.AuthenticationRequest;
import com.everkeep.dto.AuthenticationResponse;
import com.everkeep.dto.UserDto;

public interface UserResourceApi {

    @PostMapping
    void register(@RequestBody @Valid UserDto userDto,
                  HttpServletRequest request);

    @GetMapping("/confirm")
    void confirm(@RequestParam("token") String tokenValue);

    @GetMapping("/confirm/resend")
    void resendToken(@RequestParam("email") String email,
                     HttpServletRequest request);

    @GetMapping("/password/reset")
    void resetPassword(@RequestParam("email") String email,
                       HttpServletRequest request);

    @PutMapping("/password/change")
    void updatePassword(@RequestParam("token") String tokenValue,
                        @RequestBody @Valid UserDto userDto);

    @PostMapping("/authenticate")
    AuthenticationResponse authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest);
}
