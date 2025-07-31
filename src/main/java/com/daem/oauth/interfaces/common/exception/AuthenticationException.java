package com.daem.oauth.interfaces.common.exception;

/**
 * 认证异常
 */
public class AuthenticationException extends RuntimeException {
    
    private final String errorCode;
    private final Object[] args;
    
    public AuthenticationException(String message) {
        super(message);
        this.errorCode = null;
        this.args = null;
    }
    
    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    public AuthenticationException(String message, String errorCode, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.args = null;
    }
    
    public AuthenticationException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
}