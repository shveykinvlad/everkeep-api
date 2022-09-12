package com.everkeep.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@ConfigurationProperties(prefix = "integration")
@Validated
@ConstructorBinding
public record IntegrationProperties(
        @NotEmpty
        String uiUrl
) { }
