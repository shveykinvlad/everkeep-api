package com.everkeep.config.properties;

import javax.validation.constraints.NotNull;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("verification-token")
@Validated
@ConstructorBinding
public record VerificationTokenProperties(
        @NotNull
        Duration expiryDuration
) { }
