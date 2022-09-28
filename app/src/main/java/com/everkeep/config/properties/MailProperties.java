package com.everkeep.config.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.mail")
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
