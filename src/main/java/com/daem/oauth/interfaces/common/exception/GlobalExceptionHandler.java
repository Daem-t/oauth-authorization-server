package com.daem.oauth.interfaces.common.exception;

import com.daem.oauth.application.exception.BusinessException;
import com.daem.oauth.interfaces.auth.dto.LoginResponse;
import com.daem.oauth.interfaces.common.constants.MessageConstants;
import com.daem.oauth.interfaces.common.dto.MessageResponse;
import com.daem.oauth.interfaces.common.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private final MessageService messageService;
    
    public GlobalExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }
    
    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<LoginResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        logger.warn("Authentication exception: {}", ex.getMessage());
        
        String message = ex.getErrorCode() != null ? 
                messageService.getMessage(ex.getErrorCode(), ex.getArgs()) : 
                ex.getMessage();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(null, null, null, message));
    }
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<MessageResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        logger.warn("Business exception: {}", ex.getMessage());
        
        String message = ex.getErrorCode() != null ? 
                messageService.getMessage(ex.getErrorCode(), ex.getArgs()) : 
                ex.getMessage();
        
        // 根据异常类型确定HTTP状态码
        HttpStatus status = determineHttpStatus(ex);
        
        return ResponseEntity.status(status)
                .body(new MessageResponse(message));
    }
    
    /**
     * 根据异常类型确定HTTP状态码
     */
    private HttpStatus determineHttpStatus(BusinessException ex) {
        // 检查异常类上的@ResponseStatus注解
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value();
        }
        
        // 默认返回400 Bad Request
        return HttpStatus.BAD_REQUEST;
    }
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            // 如果错误消息是国际化键，则进行翻译
            if (errorMessage != null && errorMessage.startsWith("{") && errorMessage.endsWith("}")) {
                String key = errorMessage.substring(1, errorMessage.length() - 1);
                errorMessage = messageService.getMessage(key);
            }
            errors.put(fieldName, errorMessage);
        });
        
        String baseMessage = messageService.getMessage(MessageConstants.Validation.PARAMETER_INVALID);
        String detailMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
        
        String message = baseMessage + " - " + detailMessage;
        
        logger.warn("Validation exception: {}", message);
        
        return ResponseEntity.badRequest()
                .body(new MessageResponse(message));
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<MessageResponse> handleBindException(BindException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            // 如果错误消息是国际化键，则进行翻译
            if (errorMessage != null && errorMessage.startsWith("{") && errorMessage.endsWith("}")) {
                String key = errorMessage.substring(1, errorMessage.length() - 1);
                errorMessage = messageService.getMessage(key);
            }
            errors.put(fieldName, errorMessage);
        });
        
        String baseMessage = messageService.getMessage(MessageConstants.Validation.PARAMETER_INVALID);
        String detailMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
        
        String message = baseMessage + " - " + detailMessage;
        
        logger.warn("Bind exception: {}", message);
        
        return ResponseEntity.badRequest()
                .body(new MessageResponse(message));
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageResponse> handleConstraintViolationException(
            ConstraintViolationException ex) {
        
        String detailMessage = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String message = violation.getMessage();
                    // 如果错误消息是国际化键，则进行翻译
                    if (message != null && message.startsWith("{") && message.endsWith("}")) {
                        String key = message.substring(1, message.length() - 1);
                        message = messageService.getMessage(key);
                    }
                    return violation.getPropertyPath() + ": " + message;
                })
                .collect(Collectors.joining("; "));
        
        String baseMessage = messageService.getMessage(MessageConstants.Validation.CONSTRAINT_VIOLATION);
        String message = baseMessage + " - " + detailMessage;
        
        logger.warn("Constraint violation exception: {}", message);
        
        return ResponseEntity.badRequest()
                .body(new MessageResponse(message));
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected exception: {}", ex.getMessage(), ex);
        
        String message = messageService.getMessage(MessageConstants.System.INTERNAL_ERROR);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse(message));
    }
}