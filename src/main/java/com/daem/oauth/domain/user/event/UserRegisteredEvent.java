package com.daem.oauth.domain.user.event;

import com.daem.oauth.domain.user.User;

/**
 * A Domain Event indicating that a new user has been successfully registered.
 * This event can be used to trigger side effects, such as sending a welcome/activation email.
 */
public class UserRegisteredEvent {

    private final User user;

    public UserRegisteredEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
