package com.everkeep.resource;

import java.time.OffsetDateTime;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.dto.UserDto;
import com.everkeep.event.RegistrationCompleteEvent;
import com.everkeep.exception.security.VerificationTokenExpirationException;
import com.everkeep.model.security.User;
import com.everkeep.model.security.VerificationToken;
import com.everkeep.service.security.UserService;
import com.everkeep.service.security.VerificationService;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/registration")
@RequiredArgsConstructor
public class RegistrationResource implements RegistrationResourceApi {

    private final UserService userService;
    private final VerificationService verificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void register(UserDto userDto, HttpServletRequest request) {
        User registeredUser = userService.register(userDto);

        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new RegistrationCompleteEvent(registeredUser, appUrl));
    }

    @Override
    public void confirm(String token) {
        VerificationToken verificationToken = verificationService.get(token);

        User user = verificationToken.getUser();
        if ((OffsetDateTime.now().isAfter(verificationToken.getExpiryTime()))) {
            throw new VerificationTokenExpirationException("Verification token is expired", token);
        }

        user.setEnabled(true);
        userService.save(user);
    }
}
