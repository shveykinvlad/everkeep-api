package com.everkeep.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@ConfigurationProperties("jwt")
@Validated
@ConstructorBinding
public record JwtProperties(
        @NotEmpty
        String secret,

        @NotNull
        Duration expiryDuration
) {
}
