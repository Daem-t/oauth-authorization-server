package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException() {
        super("Username already exists", MessageConstants.User.USER_ALREADY_EXISTS);
    }
    
    public UserAlreadyExistsException(String message) {
        super(message, MessageConstants.User.USER_ALREADY_EXISTS);
    }
}
