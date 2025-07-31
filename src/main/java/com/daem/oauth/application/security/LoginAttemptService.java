package com.daem.oauth.application.security;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 登录尝试限制服务
 */
@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    private final ConcurrentHashMap<String, AttemptInfo> attemptCache = new ConcurrentHashMap<>();
    
    /**
     * 记录登录失败
     */
    public void recordFailedAttempt(String key) {
        AttemptInfo info = attemptCache.computeIfAbsent(key, k -> new AttemptInfo());
        info.incrementAttempts();
        info.setLastAttemptTime(LocalDateTime.now());
    }
    
    /**
     * 重置登录尝试
     */
    public void resetAttempts(String key) {
        attemptCache.remove(key);
    }
    
    /**
     * 检查是否被锁定
     */
    public boolean isBlocked(String key) {
        AttemptInfo info = attemptCache.get(key);
        if (info == null) {
            return false;
        }
        
        // 检查锁定是否已过期
        if (info.getLastAttemptTime().plus(LOCKOUT_DURATION_MINUTES, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())) {
            attemptCache.remove(key);
            return false;
        }
        
        return info.getAttempts() >= MAX_ATTEMPTS;
    }
    
    /**
     * 获取剩余尝试次数
     */
    public int getRemainingAttempts(String key) {
        AttemptInfo info = attemptCache.get(key);
        if (info == null) {
            return MAX_ATTEMPTS;
        }
        
        return Math.max(0, MAX_ATTEMPTS - info.getAttempts());
    }
    
    /**
     * 获取锁定剩余时间（分钟）
     */
    public long getLockoutRemainingMinutes(String key) {
        AttemptInfo info = attemptCache.get(key);
        if (info == null || info.getAttempts() < MAX_ATTEMPTS) {
            return 0;
        }
        
        LocalDateTime unlockTime = info.getLastAttemptTime().plus(LOCKOUT_DURATION_MINUTES, ChronoUnit.MINUTES);
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), unlockTime);
    }
    
    /**
     * 清理过期的尝试记录
     */
    public void cleanupExpiredAttempts() {
        LocalDateTime cutoff = LocalDateTime.now().minus(LOCKOUT_DURATION_MINUTES, ChronoUnit.MINUTES);
        attemptCache.entrySet().removeIf(entry -> 
            entry.getValue().getLastAttemptTime().isBefore(cutoff)
        );
    }
    
    /**
     * 尝试信息内部类
     */
    private static class AttemptInfo {
        private final AtomicInteger attempts = new AtomicInteger(0);
        private volatile LocalDateTime lastAttemptTime;
        
        public int incrementAttempts() {
            return attempts.incrementAndGet();
        }
        
        public int getAttempts() {
            return attempts.get();
        }
        
        public LocalDateTime getLastAttemptTime() {
            return lastAttemptTime;
        }
        
        public void setLastAttemptTime(LocalDateTime lastAttemptTime) {
            this.lastAttemptTime = lastAttemptTime;
        }
    }
}