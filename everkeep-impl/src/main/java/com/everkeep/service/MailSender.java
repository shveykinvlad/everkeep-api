package com.everkeep.service;

import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.everkeep.config.IntegrationProperties;
import com.everkeep.config.MailProperties;

@Service
@RequiredArgsConstructor
public class MailSender {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final IntegrationProperties integrationProperties;
    private final MessageSource messageSource;

    public void sendUserConfirmationMail(String mailTo, String tokenValue) {
        var subject = getDefaultMessage("user.confirm.email.subject");
        var message = getDefaultMessage("user.confirm.email.message", integrationProperties.getUiUrl(), tokenValue);

        send(mailTo, subject, message);
    }

    public void sendResetPasswordMail(String mailTo, String tokenValue) {
        var subject = getDefaultMessage("user.password.reset.email.subject");
        var message = getDefaultMessage("user.password.reset.email.message", integrationProperties.getUiUrl(), mailTo, tokenValue);

        send(mailTo, subject, message);
    }

    private void send(String mailTo, String subject, String message) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailProperties.getUsername());
        mailMessage.setTo(mailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

    private String getDefaultMessage(String code, Object... objects) {
        return messageSource.getMessage(code, objects, Locale.getDefault());
    }
}
