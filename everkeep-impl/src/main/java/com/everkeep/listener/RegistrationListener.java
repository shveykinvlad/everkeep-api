package com.everkeep.listener;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.everkeep.event.RegistrationCompleteEvent;
import com.everkeep.model.security.User;
import com.everkeep.service.MailSender;
import com.everkeep.service.security.VerificationService;

@Component
@RequiredArgsConstructor
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final VerificationService verificationService;
    private final MailSender mailSender;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();

        verificationService.create(user, token);

        String subject = "Everkeep account confirmation!";
        String message = "http://localhost:8080" + event.getAppUrl() + "/api/registration/confirm?token=" + token;

        mailSender.send(user.getEmail(), subject, message);
    }
}