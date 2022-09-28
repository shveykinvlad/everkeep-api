package com.everkeep.config.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties("verification-token")
public record VerificationTokenProperties(
        @NotNull
        Duration expiryDuration
) { }
