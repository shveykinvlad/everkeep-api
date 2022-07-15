package com.everkeep.config.properties;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "integration")
@Validated
@ConstructorBinding
public record IntegrationProperties(
        @NotEmpty
        String uiUrl
) { }
