package com.daem.oauth.interfaces.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 登录响应DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("token_type")
    private String tokenType = "Bearer";
    
    @JsonProperty("expires_in")
    private Long expiresIn;
    
    @JsonProperty("refresh_expires_in")
    private Long refreshExpiresIn;
    
    private String message;
    
    private Long timestamp;
    
    @JsonProperty("user_info")
    private UserInfo userInfo;

    public LoginResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, String message) {
        this();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.message = message;
    }
    
    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, Long refreshExpiresIn, String message, UserInfo userInfo) {
        this();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
        this.message = message;
        this.userInfo = userInfo;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    public Long getRefreshExpiresIn() { return refreshExpiresIn; }
    public void setRefreshExpiresIn(Long refreshExpiresIn) { this.refreshExpiresIn = refreshExpiresIn; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }
    
    /**
     * 用户基本信息
     */
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String status;
        private String[] roles;
        private String locale;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String email, String status, String[] roles, String locale) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.status = status;
            this.roles = roles;
            this.locale = locale;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String[] getRoles() { return roles; }
        public void setRoles(String[] roles) { this.roles = roles; }
        public String getLocale() { return locale; }
        public void setLocale(String locale) { this.locale = locale; }
    }
}