package com.everkeep.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import com.everkeep.model.security.User;

@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final User user;

    public RegistrationCompleteEvent(User user, String appUrl) {
        super(user);

        this.user = user;
        this.appUrl = appUrl;
    }
}