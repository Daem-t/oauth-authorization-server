package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExpiredActivationTokenException extends BusinessException {
    public ExpiredActivationTokenException() {
        super("Activation token expired", MessageConstants.User.ACTIVATION_TOKEN_EXPIRED);
    }
    
    public ExpiredActivationTokenException(String message) {
        super(message, MessageConstants.User.ACTIVATION_TOKEN_EXPIRED);
    }
}