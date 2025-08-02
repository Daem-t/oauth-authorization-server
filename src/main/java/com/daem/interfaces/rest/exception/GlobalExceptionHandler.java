package com.daem.interfaces.rest.exception;

import com.daem.application.exception.ClientIdMismatchException;
import com.daem.application.exception.ClientNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> handleClientNotFoundException(ClientNotFoundException ex, Locale locale) {
        String errorMessage = messageSource.getMessage("client.error.notFound", new Object[]{ex.getMessage()}, locale);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClientIdMismatchException.class)
    public ResponseEntity<String> handleClientIdMismatchException(ClientIdMismatchException ex, Locale locale) {
        String errorMessage = messageSource.getMessage("client.error.idMismatch", null, locale);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Generic exception handler for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex, Locale locale) {
        String errorMessage = messageSource.getMessage("error.generic", null, locale);
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
