package com.everkeep.service;

import java.util.Locale;

import com.everkeep.config.properties.IntegrationProperties;
import com.everkeep.config.properties.MailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final String CONFIRMATION_SUBJECT_CODE = "user.confirm.email.subject";
    private static final String CONFIRMATION_MESSAGE_CODE = "user.confirm.email.message";
    private static final String RESET_PASSWORD_SUBJECT_CODE = "user.password.reset.email.subject";
    private static final String RESET_PASSWORD_MESSAGE_CODE = "user.password.reset.email.message";

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final IntegrationProperties integrationProperties;
    private final MessageSource messageSource;

    public void sendConfirmationMail(String to, String tokenValue) {
        var subject = getMessage(CONFIRMATION_SUBJECT_CODE);
        var text = getMessage(CONFIRMATION_MESSAGE_CODE, integrationProperties.uiUrl(), tokenValue);

        send(to, subject, text);
    }

    public void sendResetPasswordMail(String to, String tokenValue) {
        var subject = getMessage(RESET_PASSWORD_SUBJECT_CODE);
        var text = getMessage(RESET_PASSWORD_MESSAGE_CODE, integrationProperties.uiUrl(), to, tokenValue);

        send(to, subject, text);
    }

    private void send(String to, String subject, String text) {
        var message = new SimpleMailMessage();
        message.setFrom(mailProperties.username());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }

    private String getMessage(String code, Object... objects) {
        return messageSource.getMessage(code, objects, Locale.getDefault());
    }
}
