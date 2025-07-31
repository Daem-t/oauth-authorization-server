package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super("User not found", MessageConstants.User.USER_NOT_FOUND);
    }
    
    public UserNotFoundException(String message) {
        super(message, MessageConstants.User.USER_NOT_FOUND);
    }
}