package com.everkeep.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.everkeep.config.security.JwtProperties;

@EnableConfigurationProperties({
        JwtProperties.class,
        MailProperties.class,
        IntegrationProperties.class})
@Configuration
public class PropertiesConfiguration {

}
