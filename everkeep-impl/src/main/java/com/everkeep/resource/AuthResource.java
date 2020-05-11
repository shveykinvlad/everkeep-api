package com.everkeep.resource;

import com.everkeep.dto.UserDto;
import com.everkeep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthResource implements AuthResourceApi {

    private final UserService userService;

    @Override
    public void register(UserDto userDto) {
        userService.register(userDto);
    }
}
