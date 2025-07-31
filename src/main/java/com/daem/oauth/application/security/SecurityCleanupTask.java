package com.daem.oauth.application.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 安全相关的清理任务
 */
@Component
public class SecurityCleanupTask {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityCleanupTask.class);
    
    private final LoginAttemptService loginAttemptService;
    
    public SecurityCleanupTask(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
    
    /**
     * 每小时清理一次过期的登录尝试记录
     */
    @Scheduled(fixedRate = 3600000) // 1小时 = 3600000毫秒
    public void cleanupExpiredLoginAttempts() {
        try {
            logger.debug("Starting cleanup of expired login attempts");
            loginAttemptService.cleanupExpiredAttempts();
            logger.debug("Completed cleanup of expired login attempts");
        } catch (Exception e) {
            logger.error("Error during login attempts cleanup", e);
        }
    }
}