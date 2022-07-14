package com.everkeep.config.properties;

import javax.validation.constraints.NotEmpty;

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
    @NotEmpty
    private final String expirationTimeSec;
}
