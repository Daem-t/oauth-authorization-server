package com.daem.oauth.interfaces.common;

import com.daem.oauth.application.exception.*;
import com.daem.oauth.interfaces.common.constants.MessageConstants;
import com.daem.oauth.interfaces.common.dto.ErrorResponse;
import com.daem.oauth.interfaces.common.service.MessageService;
import com.daem.oauth.interfaces.common.util.ErrorResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 * 统一处理应用程序中的异常，并返回国际化的错误消息
 */
@RestControllerAdvice
public class RestExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
    
    private final MessageService messageService;
    
    public RestExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 处理请求参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.warn("Validation error at {}: {}", request.getRequestURI(), 
                   ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        
        String errorTitle = messageService.getMessage("validation.parameter.invalid", new Object[]{"参数校验失败"});
        ErrorResponse errorResponse = ErrorResponseUtil.createValidationError(errorTitle, ex.getBindingResult(), request);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 处理约束验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().iterator().next().getMessage();
        logger.warn("Constraint violation at {}: {}", request.getRequestURI(), message);
        
        String errorTitle = messageService.getMessage("validation.constraint.violation", new Object[]{"约束验证失败"});
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errorTitle, message, "CONSTRAINT_VIOLATION"));
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = String.format("缺少必需的请求参数: %s", ex.getParameterName());
        logger.warn("Missing parameter at {}: {}", request.getRequestURI(), message);
        
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("缺少参数", message, "MISSING_PARAMETER"));
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler({TypeMismatchException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            Exception ex, HttpServletRequest request) {
        String message = "参数类型不匹配";
        if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException typeMismatch = (MethodArgumentTypeMismatchException) ex;
            message = String.format("参数 '%s' 类型错误，期望类型: %s", 
                    typeMismatch.getName(), 
                    typeMismatch.getRequiredType().getSimpleName());
        }
        logger.warn("Type mismatch at {}: {}", request.getRequestURI(), message);
        
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("参数类型错误", message, "TYPE_MISMATCH"));
    }

    /**
     * 处理HTTP消息不可读异常（JSON格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "请求体格式错误或无法解析";
        logger.warn("Message not readable at {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("请求格式错误", message, "MESSAGE_NOT_READABLE"));
    }

    /**
     * 处理不支持的HTTP方法异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String message = String.format("不支持的HTTP方法: %s，支持的方法: %s", 
                ex.getMethod(), String.join(", ", ex.getSupportedMethods()));
        logger.warn("Method not supported at {}: {}", request.getRequestURI(), message);
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("方法不支持", message, "METHOD_NOT_SUPPORTED"));
    }

    /**
     * 处理不支持的媒体类型异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String message = String.format("不支持的媒体类型: %s", ex.getContentType());
        logger.warn("Media type not supported at {}: {}", request.getRequestURI(), message);
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorResponse("媒体类型不支持", message, "MEDIA_TYPE_NOT_SUPPORTED"));
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        String message = String.format("请求的资源不存在: %s %s", ex.getHttpMethod(), ex.getRequestURL());
        logger.warn("No handler found: {}", message);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("资源不存在", message, "NOT_FOUND"));
    }

    /**
     * 处理验证码错误异常
     */
    @ExceptionHandler(InvalidCaptchaException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCaptcha(
            InvalidCaptchaException ex, HttpServletRequest request) {
        String message = messageService.getMessage(MessageConstants.Validation.INVALID_CAPTCHA);
        logger.warn("Invalid captcha at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        String errorTitle = messageService.getMessage("validation.captcha.title", new Object[]{"验证码错误"});
        ErrorResponse errorResponse = ErrorResponseUtil.create(HttpStatus.BAD_REQUEST, errorTitle, message, "INVALID_CAPTCHA", request);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 处理IP限制异常
     */
    @ExceptionHandler(IpLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleIpLimitExceeded(
            IpLimitExceededException ex, HttpServletRequest request) {
        String message = messageService.getMessage(MessageConstants.System.IP_LIMIT_EXCEEDED);
        logger.warn("IP limit exceeded at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        String errorTitle = messageService.getMessage("system.rate.limit.title", new Object[]{"请求过于频繁"});
        ErrorResponse errorResponse = ErrorResponseUtil.createRateLimitError(errorTitle, message, request);
        
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    /**
     * 处理用户已存在异常
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex, HttpServletRequest request) {
        String message = messageService.getMessage(MessageConstants.User.USER_ALREADY_EXISTS);
        logger.warn("User already exists at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        String errorTitle = messageService.getMessage("user.conflict.title", new Object[]{"用户冲突"});
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(errorTitle, message, "USER_ALREADY_EXISTS"));
    }

    /**
     * 处理邮箱已存在异常
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex, HttpServletRequest request) {
        String message = messageService.getMessage(MessageConstants.User.EMAIL_ALREADY_EXISTS);
        logger.warn("Email already exists at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        String errorTitle = messageService.getMessage("email.conflict.title", new Object[]{"邮箱冲突"});
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(errorTitle, message, "EMAIL_ALREADY_EXISTS"));
    }

    /**
     * 处理用户未激活异常
     */
    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotActivated(
            UserNotActivatedException ex, HttpServletRequest request) {
        String message = messageService.getMessage(MessageConstants.User.USER_NOT_ACTIVATED);
        logger.warn("User not activated at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        String errorTitle = messageService.getMessage("user.not.activated.title", new Object[]{"用户未激活"});
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(errorTitle, message, "USER_NOT_ACTIVATED"));
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        String message = messageService.getMessage(MessageConstants.User.USER_NOT_FOUND);
        logger.warn("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
        
        String errorTitle = messageService.getMessage("resource.not.found.title", new Object[]{"资源未找到"});
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(errorTitle, message, "RESOURCE_NOT_FOUND"));
    }

    /**
     * 处理凭证错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        String message = messageService.getMessage(MessageConstants.Auth.LOGIN_FAILED);
        logger.warn("Bad credentials at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        String errorTitle = messageService.getMessage("auth.credentials.invalid.title", new Object[]{"凭证无效"});
        ErrorResponse errorResponse = ErrorResponseUtil.createAuthError(errorTitle, message, request);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 处理账户被禁用异常
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(
            DisabledException ex, HttpServletRequest request) {
        String message = messageService.getMessage("auth.account.disabled", new Object[]{"账户已被禁用"});
        logger.warn("Account disabled at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("账户被禁用", message, "ACCOUNT_DISABLED"));
    }

    /**
     * 处理账户被锁定异常
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLocked(
            LockedException ex, HttpServletRequest request) {
        String message = messageService.getMessage("auth.account.locked", new Object[]{"账户已被锁定"});
        logger.warn("Account locked at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("账户被锁定", message, "ACCOUNT_LOCKED"));
    }

    /**
     * 处理访问被拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        String message = messageService.getMessage("auth.access.denied", new Object[]{"无权限访问此资源"});
        logger.warn("Access denied at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("访问被拒绝", message, "ACCESS_DENIED"));
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(
            AuthenticationException ex, HttpServletRequest request) {
        String message = messageService.getMessage(MessageConstants.Auth.LOGIN_FAILED);
        logger.warn("Authentication failed at {} from IP {}: {}", 
                   request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
        
        String errorTitle = messageService.getMessage("auth.failed.title", new Object[]{"认证失败"});
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(errorTitle, message, "AUTHENTICATION_FAILED"));
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        // 处理激活相关的运行时异常
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("无效的激活链接")) {
                String message = messageService.getMessage(MessageConstants.Validation.INVALID_TOKEN);
                logger.warn("Invalid activation token at {} from IP {}: {}", 
                           request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
                
                String errorTitle = messageService.getMessage("validation.token.invalid.title", new Object[]{"无效令牌"});
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(errorTitle, message, "INVALID_TOKEN"));
            }
            if (ex.getMessage().contains("激活链接已过期")) {
                String message = messageService.getMessage(MessageConstants.Validation.TOKEN_EXPIRED);
                logger.warn("Activation token expired at {} from IP {}: {}", 
                           request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
                
                String errorTitle = messageService.getMessage("validation.token.expired.title", new Object[]{"令牌过期"});
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(errorTitle, message, "TOKEN_EXPIRED"));
            }
            if (ex.getMessage().contains("邮件发送失败")) {
                logger.error("Email sending failed at {} from IP {}: {}", 
                            request.getRequestURI(), request.getRemoteAddr(), ex.getMessage());
                
                String message = messageService.getMessage("system.email.send.failed", new Object[]{"邮件发送失败，请稍后重试"});
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ErrorResponse("邮件服务异常", message, "EMAIL_SEND_FAILED"));
            }
        }
        
        // 记录详细的异常信息用于调试
        logger.error("Runtime exception at {} from IP {}: ", 
                    request.getRequestURI(), request.getRemoteAddr(), ex);
        
        String message = messageService.getMessage(MessageConstants.System.INTERNAL_ERROR);
        String errorTitle = messageService.getMessage("system.runtime.error.title", new Object[]{"运行时错误"});
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(errorTitle, message, "RUNTIME_ERROR"));
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherException(
            Exception ex, HttpServletRequest request) {
        
        // 记录完整的异常堆栈信息
        logger.error("Unexpected exception at {} from IP {}: ", 
                    request.getRequestURI(), request.getRemoteAddr(), ex);
        
        String message = messageService.getMessage(MessageConstants.System.INTERNAL_ERROR);
        String errorTitle = messageService.getMessage("system.internal.error.title", new Object[]{"系统异常"});
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(errorTitle, message, "INTERNAL_ERROR"));
    }
}
 