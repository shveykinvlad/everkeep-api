package com.everkeep.config.properties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("jwt")
@Validated
@ConstructorBinding
public record JwtProperties(
        @NotEmpty
        String secret,

        @NotNull
        Duration expiryDuration
) { }
