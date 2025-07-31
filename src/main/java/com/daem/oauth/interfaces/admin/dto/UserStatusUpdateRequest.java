package com.daem.oauth.interfaces.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 用户状态更新请求DTO
 */
public class UserStatusUpdateRequest {
    
    @NotBlank(message = "{validation.status.required}")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|DISABLED)$", message = "{validation.status.invalid}")
    private String status;
    
    private String reason; // 状态变更原因
    
    public UserStatusUpdateRequest() {}
    
    public UserStatusUpdateRequest(String status) {
        this.status = status;
    }
    
    public UserStatusUpdateRequest(String status, String reason) {
        this.status = status;
        this.reason = reason;
    }
    
    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}