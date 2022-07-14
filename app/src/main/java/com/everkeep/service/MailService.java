package com.everkeep.service;

import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.everkeep.config.properties.IntegrationProperties;
import com.everkeep.config.properties.MailProperties;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final IntegrationProperties integrationProperties;
    private final MessageSource messageSource;

    public void sendUserConfirmationMail(String to, String tokenValue) {
        var subject = getDefaultMessage("user.confirm.email.subject");
        var text = getDefaultMessage("user.confirm.email.message", integrationProperties.getUiUrl(), tokenValue);

        send(to, subject, text);
    }

    public void sendResetPasswordMail(String to, String tokenValue) {
        var subject = getDefaultMessage("user.password.reset.email.subject");
        var text = getDefaultMessage("user.password.reset.email.message", integrationProperties.getUiUrl(), to, tokenValue);

        send(to, subject, text);
    }

    private void send(String to, String subject, String text) {
        var message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }

    private String getDefaultMessage(String code, Object... objects) {
        return messageSource.getMessage(code, objects, Locale.getDefault());
    }
}
