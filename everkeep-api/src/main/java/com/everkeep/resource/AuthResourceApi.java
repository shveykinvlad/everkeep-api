package com.everkeep.resource;

import com.everkeep.dto.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface AuthResourceApi {

    @PostMapping("/registration")
    void register(@RequestBody @Valid UserDto userDto);
}
