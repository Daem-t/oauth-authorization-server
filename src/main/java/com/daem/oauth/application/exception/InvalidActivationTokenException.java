package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidActivationTokenException extends BusinessException {
    public InvalidActivationTokenException() {
        super("Invalid activation token", MessageConstants.User.ACTIVATION_TOKEN_INVALID);
    }
    
    public InvalidActivationTokenException(String message) {
        super(message, MessageConstants.User.ACTIVATION_TOKEN_INVALID);
    }
}