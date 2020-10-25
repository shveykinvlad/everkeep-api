package com.everkeep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.everkeep.config.MailProperties;
import com.everkeep.config.security.JwtProperties;

/*
    TODO: Increase test coverage
    TODO: Add notes filtration by user
    TODO: Update Swagger to version 3
    TODO: Rename Resource to Controller
    TODO: Add creation note functionality on frontend
    TODO: Add units to time properties
    TODO: Manual test
 */
@EnableConfigurationProperties({
        JwtProperties.class,
        MailProperties.class})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
