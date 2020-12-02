package com.everkeep.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Getter
@ConfigurationProperties(prefix = "integration")
@Validated
@ConstructorBinding
@RequiredArgsConstructor
public class IntegrationProperties {

    private final String uiUrl;
}
