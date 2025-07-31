package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotActivatedException extends BusinessException {
    public UserNotActivatedException() {
        super("Account not activated", MessageConstants.User.USER_NOT_ACTIVATED);
    }
    
    public UserNotActivatedException(String message) {
        super(message, MessageConstants.User.USER_NOT_ACTIVATED);
    }
}
