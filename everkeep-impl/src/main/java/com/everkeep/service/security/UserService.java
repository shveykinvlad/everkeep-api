package com.everkeep.service.security;

import java.util.Locale;
import javax.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.everkeep.dto.AuthResponse;
import com.everkeep.dto.RegistrationRequest;
import com.everkeep.enums.TokenAction;
import com.everkeep.exception.security.UserAlreadyEnabledException;
import com.everkeep.exception.security.UserAlreadyExistsException;
import com.everkeep.model.security.User;
import com.everkeep.model.security.VerificationToken;
import com.everkeep.repository.security.RoleRepository;
import com.everkeep.repository.security.UserRepository;
import com.everkeep.service.MailSender;
import com.everkeep.util.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MapperFacade mapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final MailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    public User get(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email = " + email + " not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void register(RegistrationRequest registrationRequest, String applicationUrl) {
        if (userExists(registrationRequest.getEmail())) {
            throw new UserAlreadyExistsException("User already exist", registrationRequest.getEmail());
        }
        var user = createUser(registrationRequest);
        sendToken(user, applicationUrl);
    }

    public void confirm(String tokenValue) {
        var verificationToken = verificationService.validateAndUtilize(tokenValue, TokenAction.CONFIRM_ACCOUNT);

        var user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void resendToken(String email, String applicationUrl) {
        var user = get(email);
        if (user.isEnabled()) {
            throw new UserAlreadyEnabledException("User already enabled", user.getEmail());
        }
        var token = verificationService.create(user, TokenAction.CONFIRM_ACCOUNT);
        var subject = getDefaultMessage("user.activation.email.subject");
        var message = getDefaultMessage("user.activation.email.message", applicationUrl, token.getValue());

        mailSender.send(user.getEmail(), subject, message);
    }

    public void resetPassword(String email, String applicationUrl) {
        var user = get(email);
        var token = verificationService.create(user, TokenAction.UPDATE_PASSWORD);
        var subject = getDefaultMessage("user.password.reset.email.subject");
        var message = getDefaultMessage("user.password.reset.email.message", email, token.getValue());

        mailSender.send(user.getEmail(), subject, message);
    }

    public void updatePassword(String tokenValue, String password) {
        var verificationToken = verificationService.validateAndUtilize(tokenValue, TokenAction.UPDATE_PASSWORD);

        var user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public AuthResponse authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        var user = get(email);

        var jwtToken = jwtTokenProvider.generateToken(user);
        VerificationToken refreshToken = verificationService.create(user, TokenAction.REFRESH_ACCESS);

        return new AuthResponse()
                .setJwt(jwtToken)
                .setRefreshTokenValue(refreshToken.getValue())
                .setUserEmail(user.getEmail());
    }

    private void sendToken(User user, String applicationUrl) {
        var token = verificationService.create(user, TokenAction.CONFIRM_ACCOUNT);
        var subject = getDefaultMessage("user.activation.email.subject");
        var message = getDefaultMessage("user.activation.email.message", applicationUrl, token.getValue());

        mailSender.send(user.getEmail(), subject, message);
    }

    private boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private User createUser(RegistrationRequest registrationRequest) {
        var user = mapper.map(registrationRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(roleRepository.findByName("ROLE_USER"));

        return userRepository.save(user);
    }

    private String getDefaultMessage(String code, Object... objects) {
        return messageSource.getMessage(code, objects, Locale.getDefault());
    }

    public AuthResponse refreshAccessToken(String oldTokenValue) {
        var oldToken = verificationService.validateAndUtilize(oldTokenValue, TokenAction.REFRESH_ACCESS);

        var user = oldToken.getUser();
        var jwt = jwtTokenProvider.generateToken(user);
        var newToken = verificationService.create(user, TokenAction.REFRESH_ACCESS);

        return new AuthResponse()
                .setJwt(jwt)
                .setRefreshTokenValue(newToken.getValue())
                .setUserEmail(user.getEmail());
    }
}
