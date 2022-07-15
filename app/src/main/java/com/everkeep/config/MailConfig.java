package com.everkeep.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.everkeep.config.properties.MailProperties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.host());
        mailSender.setPort(mailProperties.port());
        mailSender.setProtocol(mailProperties.protocol());
        mailSender.setUsername(mailProperties.username());
        mailSender.setPassword(mailProperties.password());

        return mailSender;
    }
}
