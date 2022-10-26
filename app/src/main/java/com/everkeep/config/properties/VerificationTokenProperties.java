package com.everkeep.config.properties;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("verification")
public record VerificationTokenProperties(
        @NotNull
        Duration expiryDuration
) { }
