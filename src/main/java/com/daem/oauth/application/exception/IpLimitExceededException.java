package com.daem.oauth.application.exception;

import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class IpLimitExceededException extends BusinessException {
    public IpLimitExceededException() {
        super("IP limit exceeded", MessageConstants.System.IP_LIMIT_EXCEEDED);
    }
    
    public IpLimitExceededException(String message) {
        super(message, MessageConstants.System.IP_LIMIT_EXCEEDED);
    }
}
