package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCaptchaException extends BusinessException {
    public InvalidCaptchaException() {
        super("Invalid captcha", MessageConstants.Validation.INVALID_CAPTCHA);
    }
    
    public InvalidCaptchaException(String message) {
        super(message, MessageConstants.Validation.INVALID_CAPTCHA);
    }
}
