package com.everkeep.config;

import com.everkeep.config.properties.AuthenticationProperties;
import com.everkeep.config.properties.IntegrationProperties;
import com.everkeep.config.properties.MailProperties;
import com.everkeep.config.properties.VerificationTokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
        AuthenticationProperties.class,
        MailProperties.class,
        IntegrationProperties.class,
        VerificationTokenProperties.class
})
@Configuration
public class PropertiesConfig {

}
