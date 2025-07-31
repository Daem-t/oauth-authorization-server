package com.daem.oauth.interfaces.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 消息响应DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    private String message;
    private Long timestamp;

    public MessageResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public MessageResponse(String message) {
        this();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}