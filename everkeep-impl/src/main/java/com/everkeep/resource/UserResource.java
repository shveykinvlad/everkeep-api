package com.everkeep.resource;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.dto.UserDto;
import com.everkeep.service.security.UserService;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
public class UserResource implements UserResourceApi {

    private final UserService userService;

    @Override
    public void register(UserDto userDto, HttpServletRequest request) {
        userService.register(userDto, request.getContextPath());
    }

    @Override
    public void confirm(String tokenValue) {
        userService.confirm(tokenValue);
    }

    @Override
    public void resendToken(String email, HttpServletRequest request) {
        userService.resendToken(email, request.getContextPath());
    }

    @Override
    public void resetPassword(String email, HttpServletRequest request) {
        userService.resetPassword(email, request.getContextPath());
    }

    @Override
    public void updatePassword(String tokenValue, @Valid UserDto userDto) {
        userService.updatePassword(tokenValue, userDto.getEmail(), userDto.getPassword());
    }
}
