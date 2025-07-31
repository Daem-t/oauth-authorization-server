package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AuthenticationInconsistentException extends BusinessException {
    public AuthenticationInconsistentException() {
        super("Authentication inconsistent", MessageConstants.User.USER_AUTHENTICATION_INCONSISTENT);
    }
    
    public AuthenticationInconsistentException(String message) {
        super(message, MessageConstants.User.USER_AUTHENTICATION_INCONSISTENT);
    }
}