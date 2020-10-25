package com.everkeep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.everkeep.config.MailProperties;

@Service
@RequiredArgsConstructor
public class MailSender {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public void send(String emailTo, String subject, String message) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailProperties.getUsername());
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }
}
