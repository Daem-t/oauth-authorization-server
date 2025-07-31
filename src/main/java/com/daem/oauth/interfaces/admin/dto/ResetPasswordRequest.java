package com.daem.oauth.interfaces.admin.dto;

import jakarta.validation.constraints.Size;

/**
 * 重置密码请求DTO
 */
public class ResetPasswordRequest {
    
    @Size(min = 6, max = 100, message = "{validation.password.length}")
    private String newPassword;
    
    private Boolean sendEmail = true; // 是否发送邮件通知用户
    private Boolean forceChangeOnNextLogin = true; // 下次登录时强制修改密码
    
    public ResetPasswordRequest() {}
    
    public ResetPasswordRequest(String newPassword) {
        this.newPassword = newPassword;
    }
    
    // Getters and Setters
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    
    public Boolean getSendEmail() { return sendEmail; }
    public void setSendEmail(Boolean sendEmail) { this.sendEmail = sendEmail; }
    
    public Boolean getForceChangeOnNextLogin() { return forceChangeOnNextLogin; }
    public void setForceChangeOnNextLogin(Boolean forceChangeOnNextLogin) { 
        this.forceChangeOnNextLogin = forceChangeOnNextLogin; 
    }
}