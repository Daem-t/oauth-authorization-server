package com.daem.oauth.infrastructure.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;

@Component
public class SecurityAuditListener implements ApplicationListener<AbstractAuthenticationEvent> {
    private static final Logger log = LoggerFactory.getLogger(SecurityAuditListener.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    private String getClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null) ip = request.getRemoteAddr();
            return ip;
        }
        return "unknown";
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        String username = event.getAuthentication().getName();
        String ip = getClientIp();
        String detail = event.toString();
        String eventType = event.getClass().getSimpleName();
        AuditLogEntity logEntity = new AuditLogEntity();
        logEntity.setUsername(username);
        logEntity.setEvent(eventType);
        logEntity.setIp(ip);
        logEntity.setTimestamp(LocalDateTime.now());
        logEntity.setDetail(detail);
        auditLogRepository.save(logEntity);

        if (event instanceof AuthenticationSuccessEvent) {
            log.info("用户登录成功: {}", username);
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            log.warn("用户登录失败: {}", username);
        } else if (event instanceof AuthenticationFailureLockedEvent) {
            log.warn("用户被锁定: {}", username);
        }
    }
} 