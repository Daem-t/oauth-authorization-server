package com.daem.oauth.interfaces.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 统一错误响应DTO
 * 遵循RFC 7807 Problem Details for HTTP APIs标准
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * 错误类型标识符（通常是URI）
     */
    @JsonProperty("type")
    private String type;
    
    /**
     * 错误标题（简短的人类可读描述）
     */
    @JsonProperty("title")
    private String title;
    
    /**
     * HTTP状态码
     */
    @JsonProperty("status")
    private Integer status;
    
    /**
     * 详细的错误描述
     */
    @JsonProperty("detail")
    private String detail;
    
    /**
     * 错误发生的实例URI（通常是请求路径）
     */
    @JsonProperty("instance")
    private String instance;
    
    /**
     * 应用特定的错误代码
     */
    @JsonProperty("code")
    private String code;
    
    /**
     * 错误发生的时间戳
     */
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 验证错误的详细信息（用于参数验证失败）
     */
    @JsonProperty("errors")
    private List<FieldError> errors;
    
    /**
     * 追踪ID（用于日志关联）
     */
    @JsonProperty("traceId")
    private String traceId;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 基础构造函数
     */
    public ErrorResponse(String title, String detail) {
        this();
        this.title = title;
        this.detail = detail;
    }

    /**
     * 包含错误代码的构造函数
     */
    public ErrorResponse(String title, String detail, String code) {
        this(title, detail);
        this.code = code;
    }

    /**
     * 完整构造函数
     */
    public ErrorResponse(String type, String title, Integer status, String detail, String instance, String code) {
        this();
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.code = code;
    }

    /**
     * 建造者模式
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ErrorResponse errorResponse;

        public Builder() {
            this.errorResponse = new ErrorResponse();
        }

        public Builder type(String type) {
            errorResponse.type = type;
            return this;
        }

        public Builder title(String title) {
            errorResponse.title = title;
            return this;
        }

        public Builder status(Integer status) {
            errorResponse.status = status;
            return this;
        }

        public Builder detail(String detail) {
            errorResponse.detail = detail;
            return this;
        }

        public Builder instance(String instance) {
            errorResponse.instance = instance;
            return this;
        }

        public Builder code(String code) {
            errorResponse.code = code;
            return this;
        }

        public Builder errors(List<FieldError> errors) {
            errorResponse.errors = errors;
            return this;
        }

        public Builder traceId(String traceId) {
            errorResponse.traceId = traceId;
            return this;
        }

        public ErrorResponse build() {
            return errorResponse;
        }
    }

    /**
     * 字段验证错误详情
     */
    public static class FieldError {
        @JsonProperty("field")
        private String field;
        
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("rejectedValue")
        private Object rejectedValue;

        public FieldError() {}

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getRejectedValue() { return rejectedValue; }
        public void setRejectedValue(Object rejectedValue) { this.rejectedValue = rejectedValue; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FieldError that = (FieldError) o;
            return Objects.equals(field, that.field) && 
                   Objects.equals(message, that.message) && 
                   Objects.equals(rejectedValue, that.rejectedValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field, message, rejectedValue);
        }

        @Override
        public String toString() {
            return String.format("FieldError{field='%s', message='%s', rejectedValue=%s}", 
                               field, message, rejectedValue);
        }
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getInstance() { return instance; }
    public void setInstance(String instance) { this.instance = instance; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public List<FieldError> getErrors() { return errors; }
    public void setErrors(List<FieldError> errors) { this.errors = errors; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(type, that.type) && 
               Objects.equals(title, that.title) && 
               Objects.equals(status, that.status) && 
               Objects.equals(detail, that.detail) && 
               Objects.equals(instance, that.instance) && 
               Objects.equals(code, that.code) && 
               Objects.equals(timestamp, that.timestamp) && 
               Objects.equals(errors, that.errors) && 
               Objects.equals(traceId, that.traceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, title, status, detail, instance, code, timestamp, errors, traceId);
    }

    @Override
    public String toString() {
        return String.format("ErrorResponse{type='%s', title='%s', status=%d, detail='%s', instance='%s', code='%s', timestamp=%s, errors=%s, traceId='%s'}", 
                           type, title, status, detail, instance, code, timestamp, errors, traceId);
    }
}