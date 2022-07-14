package com.everkeep.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.everkeep.config.properties.IntegrationProperties;
import com.everkeep.config.properties.JwtProperties;
import com.everkeep.config.properties.MailProperties;
import com.everkeep.config.properties.VerificationTokenProperties;

@EnableConfigurationProperties({
        JwtProperties.class,
        MailProperties.class,
        IntegrationProperties.class,
        VerificationTokenProperties.class
})
@Configuration
public class PropertiesConfig {

}
