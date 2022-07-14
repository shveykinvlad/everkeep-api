package com.everkeep.config.properties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties("jwt")
@ConstructorBinding
@RequiredArgsConstructor
public class JwtProperties {

    @NotEmpty
    private final String secret;
    @NotNull
    private final Duration expiryDuration;
}
