package com.daem.oauth.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyActiveException extends BusinessException {
    public UserAlreadyActiveException(String messageCode, Object... args) {
        super(messageCode, args);
    }

    public UserAlreadyActiveException(String messageCode) {
        super(messageCode);
    }
}
