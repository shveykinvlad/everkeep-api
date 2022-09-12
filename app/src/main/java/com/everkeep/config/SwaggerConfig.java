package com.everkeep.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String BEARER = "Bearer";

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        BEARER,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme(BEARER)
                                                .bearerFormat("JWT")
                                )
                )
                .info(
                        new Info()
                                .title("Everkeep API")
                                .description("Note application")
                );
    }
}
