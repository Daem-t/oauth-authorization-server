package com.daem.oauth.interfaces.admin.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * 批量用户状态更新请求DTO
 */
public class BatchUserStatusUpdateRequest {
    
    @NotEmpty(message = "{validation.userIds.required}")
    private List<Long> userIds;
    
    @NotBlank(message = "{validation.status.required}")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|DISABLED)$", message = "{validation.status.invalid}")
    private String status;
    
    private String reason; // 状态变更原因
    
    public BatchUserStatusUpdateRequest() {}
    
    public BatchUserStatusUpdateRequest(List<Long> userIds, String status) {
        this.userIds = userIds;
        this.status = status;
    }
    
    public BatchUserStatusUpdateRequest(List<Long> userIds, String status, String reason) {
        this.userIds = userIds;
        this.status = status;
        this.reason = reason;
    }
    
    // Getters and Setters
    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}