package com.daem.oauth.application.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 审计日志服务
 */
@Service
public class AuditService {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    
    /**
     * 记录登录成功事件
     */
    public void logLoginSuccess(String username, Long userId, String ip, String userAgent, String deviceInfo) {
        auditLogger.info("LOGIN_SUCCESS - Username: {}, UserId: {}, IP: {}, UserAgent: {}, Device: {}", 
                         username, userId, ip, userAgent, deviceInfo);
    }
    
    /**
     * 记录登录失败事件
     */
    public void logLoginFailure(String username, String reason, String ip, String userAgent) {
        auditLogger.warn("LOGIN_FAILURE - Username: {}, Reason: {}, IP: {}, UserAgent: {}", 
                         username, reason, ip, userAgent);
    }
    
    /**
     * 记录令牌刷新事件
     */
    public void logTokenRefresh(String username, Long userId, String ip) {
        auditLogger.info("TOKEN_REFRESH - Username: {}, UserId: {}, IP: {}", 
                         username, userId, ip);
    }
    
    /**
     * 记录登出事件
     */
    public void logLogout(String username, Long userId, String ip) {
        auditLogger.info("LOGOUT - Username: {}, UserId: {}, IP: {}", 
                         username, userId, ip);
    }
    
    /**
     * 记录安全事件
     */
    public void logSecurityEvent(String event, String username, String ip, String details) {
        auditLogger.warn("SECURITY_EVENT - Event: {}, Username: {}, IP: {}, Details: {}", 
                         event, username, ip, details);
    }
    
    /**
     * 从请求中提取客户端信息
     */
    public String extractClientInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String xRealIp = request.getHeader("X-Real-IP");
        
        StringBuilder clientInfo = new StringBuilder();
        clientInfo.append("IP: ").append(getClientIpAddress(request));
        
        if (userAgent != null) {
            clientInfo.append(", UserAgent: ").append(userAgent);
        }
        
        if (xForwardedFor != null) {
            clientInfo.append(", X-Forwarded-For: ").append(xForwardedFor);
        }
        
        if (xRealIp != null) {
            clientInfo.append(", X-Real-IP: ").append(xRealIp);
        }
        
        return clientInfo.toString();
    }
    
    /**
     * 获取客户端真实IP地址
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}