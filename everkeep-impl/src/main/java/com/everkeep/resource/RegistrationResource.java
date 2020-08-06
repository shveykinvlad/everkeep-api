package com.everkeep.resource;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everkeep.dto.UserDto;
import com.everkeep.model.security.User;
import com.everkeep.model.security.VerificationToken;
import com.everkeep.service.security.RegistrationService;
import com.everkeep.service.security.UserService;
import com.everkeep.service.security.VerificationService;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/registration")
@RequiredArgsConstructor
public class RegistrationResource implements RegistrationResourceApi {

    private final UserService userService;
    private final VerificationService verificationService;
    private final RegistrationService registrationService;

    @Override
    public void register(UserDto userDto, HttpServletRequest request) {
        User registeredUser = userService.register(userDto);

        String appUrl = request.getContextPath();
        registrationService.confirmRegistration(appUrl, registeredUser);
    }

    @Override
    public void confirm(String token) {
        VerificationToken verificationToken = verificationService.get(token);
        verificationService.validateToken(verificationToken);

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userService.save(user);
    }

    @Override
    public void resend(String email, HttpServletRequest request) {
        String appUrl = request.getContextPath();
        User user = userService.get(email);

        registrationService.resendToken(appUrl, user);
    }
}
