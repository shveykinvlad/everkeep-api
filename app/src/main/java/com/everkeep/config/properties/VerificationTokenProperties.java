package com.everkeep.config.properties;

import javax.validation.constraints.NotNull;
import java.time.Duration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties("verification-token")
@ConstructorBinding
@RequiredArgsConstructor
public class VerificationTokenProperties {

    @NotNull
    private final Duration expiryDuration;
}
