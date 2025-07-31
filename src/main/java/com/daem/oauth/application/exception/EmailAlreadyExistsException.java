package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException() {
        super("Email already exists", MessageConstants.User.EMAIL_ALREADY_EXISTS);
    }
    
    public EmailAlreadyExistsException(String message) {
        super(message, MessageConstants.User.EMAIL_ALREADY_EXISTS);
    }
}
