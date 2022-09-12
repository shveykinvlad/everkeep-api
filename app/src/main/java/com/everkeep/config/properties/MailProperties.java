package com.everkeep.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "spring.mail")
@Validated
@ConstructorBinding
public record MailProperties(
        @NotEmpty
        String host,

        @NotNull
        Integer port,

        @NotEmpty
        String protocol,

        @NotEmpty
        String username,

        @NotEmpty
        String password
) { }
