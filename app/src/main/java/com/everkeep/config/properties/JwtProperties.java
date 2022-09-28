package com.everkeep.config.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties("jwt")
public record JwtProperties(
        @NotEmpty
        String secret,

        @NotNull
        Duration expiryDuration
) { }
