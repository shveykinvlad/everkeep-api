package com.everkeep.service.security;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.everkeep.exception.security.UserAlreadyEnabledException;
import com.everkeep.model.security.User;
import com.everkeep.service.MailSender;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final VerificationService verificationService;
    private final MailSender mailSender;

    public void confirmRegistration(String appUrl, User user) {
        String token = UUID.randomUUID().toString();

        verificationService.create(user, token);

        String subject = "Everkeep account confirmation!";
        String message = "http://localhost:8080" + appUrl + "/api/registration/confirm?token=" + token;

        mailSender.send(user.getEmail(), subject, message);
    }

    public void resendToken(String appUrl, User user) {
        if (user.isEnabled()) {
            throw new UserAlreadyEnabledException("User already enabled", user.getEmail());
        }
        String token = UUID.randomUUID().toString();
        verificationService.create(user, token);

        String subject = "Everkeep account confirmation!";
        String message = "http://localhost:8080" + appUrl + "/api/registration/confirm?token=" + token;

        mailSender.send(user.getEmail(), subject, message);
    }
}
