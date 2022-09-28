package com.everkeep.config.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration")
public record IntegrationProperties(
        @NotEmpty
        String uiUrl
) { }
