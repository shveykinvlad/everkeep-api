package com.everkeep.config.properties;

import javax.validation.constraints.NotEmpty;

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

    @NotEmpty
    private final String uiUrl;
}
