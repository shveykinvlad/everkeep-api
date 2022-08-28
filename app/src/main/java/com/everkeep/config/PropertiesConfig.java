package com.everkeep.config;

import com.everkeep.config.properties.IntegrationProperties;
import com.everkeep.config.properties.JwtProperties;
import com.everkeep.config.properties.MailProperties;
import com.everkeep.config.properties.VerificationTokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
        JwtProperties.class,
        MailProperties.class,
        IntegrationProperties.class,
        VerificationTokenProperties.class
})
@Configuration
public class PropertiesConfig {

}
