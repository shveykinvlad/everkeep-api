package com.everkeep.config.properties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Getter
@ConfigurationProperties(prefix = "spring.mail")
@Validated
@ConstructorBinding
@RequiredArgsConstructor
public class MailProperties {

    @NotEmpty
    private final String host;
    @NotNull
    private final Integer port;
    @NotEmpty
    private final String protocol;
    @NotEmpty
    private final String username;
    @NotEmpty
    private final String password;
}
