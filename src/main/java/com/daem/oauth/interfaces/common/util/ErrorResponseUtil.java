package com.daem.oauth.interfaces.common.util;

import com.daem.oauth.interfaces.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ErrorResponse工具类
 * 提供便捷的方法创建标准化的错误响应
 */
public final class ErrorResponseUtil {
    
    private static final String ERROR_TYPE_PREFIX = "https://api.oauth.example.com/errors/";
    
    private ErrorResponseUtil() {
        // 工具类，禁止实例化
    }
    
    /**
     * 创建基础错误响应
     */
    public static ErrorResponse create(String title, String detail, String code) {
        return ErrorResponse.builder()
                .title(title)
                .detail(detail)
                .code(code)
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 创建完整的错误响应
     */
    public static ErrorResponse create(HttpStatus status, String title, String detail, 
                                     String code, HttpServletRequest request) {
        return ErrorResponse.builder()
                .type(ERROR_TYPE_PREFIX + code.toLowerCase().replace("_", "-"))
                .title(title)
                .status(status.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .code(code)
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 创建验证错误响应
     */
    public static ErrorResponse createValidationError(String title, BindingResult bindingResult, 
                                                    HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList());
        
        // 添加全局错误
        bindingResult.getGlobalErrors().forEach(error -> 
                fieldErrors.add(new ErrorResponse.FieldError(
                        error.getObjectName(),
                        error.getDefaultMessage(),
                        null
                ))
        );
        
        return ErrorResponse.builder()
                .type(ERROR_TYPE_PREFIX + "validation-error")
                .title(title)
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("请求参数验证失败")
                .instance(request.getRequestURI())
                .code("VALIDATION_ERROR")
                .errors(fieldErrors)
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 创建认证错误响应
     */
    public static ErrorResponse createAuthError(String title, String detail, 
                                              HttpServletRequest request) {
        return ErrorResponse.builder()
                .type(ERROR_TYPE_PREFIX + "authentication-error")
                .title(title)
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .code("AUTHENTICATION_ERROR")
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 创建授权错误响应
     */
    public static ErrorResponse createAuthorizationError(String title, String detail, 
                                                       HttpServletRequest request) {
        return ErrorResponse.builder()
                .type(ERROR_TYPE_PREFIX + "authorization-error")
                .title(title)
                .status(HttpStatus.FORBIDDEN.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .code("AUTHORIZATION_ERROR")
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 创建资源未找到错误响应
     */
    public static ErrorResponse createNotFoundError(String title, String detail, 
                                                  HttpServletRequest request) {
        return ErrorResponse.builder()
                .type(ERROR_TYPE_PREFIX + "not-found")
                .title(title)
                .status(HttpStatus.NOT_FOUND.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .code("NOT_FOUND")
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 创建冲突错误响应
     */
    public static ErrorResponse createConflictError(String title, String detail, String code,
                                                  HttpServletRequest request) {
        return ErrorResponse.builder()
                .type(ERROR_TYPE_PREFIX + code.toLowerCase().replace("_", "-"))
                .title(title)
                .status(HttpStatus.CONFLICT.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .code(code)
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 创建限流错误响应
     */
    public static ErrorResponse createRateLimitError(String title, String detail, 
                                                   HttpServletRequest request) {
        return ErrorResponse.builder()
                .type(ERROR_TYPE_PREFIX + "rate-limit-exceeded")
                .title(title)
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .code("RATE_LIMIT_EXCEEDED")
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 创建服务器内部错误响应
     */
    public static ErrorResponse createInternalError(String title, String detail, 
                                                  HttpServletRequest request) {
        return ErrorResponse.builder()
                .type(ERROR_TYPE_PREFIX + "internal-server-error")
                .title(title)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .code("INTERNAL_SERVER_ERROR")
                .traceId(generateTraceId())
                .build();
    }
    
    /**
     * 生成追踪ID
     */
    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}