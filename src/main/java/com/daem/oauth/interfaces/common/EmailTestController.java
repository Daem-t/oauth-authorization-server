package com.daem.oauth.interfaces.common;

import com.daem.oauth.application.user.EmailService;
import com.daem.oauth.interfaces.common.dto.MessageResponse;
import com.daem.oauth.interfaces.common.util.LocaleUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * 邮件测试控制器
 * 仅在开发和测试环境中启用，用于手动测试邮件发送功能
 */
@RestController
@RequestMapping("/dev/email-test")
@Profile({"dev", "test"})
public class EmailTestController {

    private final EmailService emailService;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * 测试发送激活邮件
     */
    @PostMapping("/activation")
    public ResponseEntity<MessageResponse> testActivationEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "TestUser") String username,
            @RequestParam(defaultValue = "zh-CN") String lang) {
        
        try {
            Locale locale = LocaleUtil.parseLocale(lang);
            String activationLink = "http://localhost:8080/activate?token=test-token-" + System.currentTimeMillis();
            
            emailService.sendActivationEmail(email, username, activationLink, locale);
            
            return ResponseEntity.ok(new MessageResponse("激活邮件发送成功到: " + email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("邮件发送失败: " + e.getMessage()));
        }
    }

    /**
     * 测试发送密码重置邮件
     */
    @PostMapping("/password-reset")
    public ResponseEntity<MessageResponse> testPasswordResetEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "TestUser") String username,
            @RequestParam(defaultValue = "zh-CN") String lang) {
        
        try {
            Locale locale = LocaleUtil.parseLocale(lang);
            String resetLink = "http://localhost:8080/reset-password?token=test-token-" + System.currentTimeMillis();
            
            emailService.sendPasswordResetEmail(email, username, resetLink, locale);
            
            return ResponseEntity.ok(new MessageResponse("密码重置邮件发送成功到: " + email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("邮件发送失败: " + e.getMessage()));
        }
    }

    /**
     * 批量测试邮件发送
     */
    @PostMapping("/batch")
    public ResponseEntity<MessageResponse> testBatchEmails(
            @RequestParam String emails, // 逗号分隔的邮箱列表
            @RequestParam(defaultValue = "activation") String type,
            @RequestParam(defaultValue = "zh-CN") String lang) {
        
        try {
            String[] emailArray = emails.split(",");
            Locale locale = LocaleUtil.parseLocale(lang);
            int successCount = 0;
            int failCount = 0;
            
            for (String email : emailArray) {
                try {
                    email = email.trim();
                    if (email.isEmpty()) continue;
                    
                    String username = "TestUser" + (successCount + 1);
                    
                    if ("activation".equals(type)) {
                        String activationLink = "http://localhost:8080/activate?token=batch-test-" + System.currentTimeMillis();
                        emailService.sendActivationEmail(email, username, activationLink, locale);
                    } else if ("password-reset".equals(type)) {
                        String resetLink = "http://localhost:8080/reset-password?token=batch-test-" + System.currentTimeMillis();
                        emailService.sendPasswordResetEmail(email, username, resetLink, locale);
                    }
                    
                    successCount++;
                    
                    // 添加延迟避免发送过快
                    Thread.sleep(100);
                    
                } catch (Exception e) {
                    failCount++;
                }
            }
            
            return ResponseEntity.ok(new MessageResponse(
                    String.format("批量邮件发送完成: 成功 %d 个, 失败 %d 个", successCount, failCount)));
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("批量邮件发送失败: " + e.getMessage()));
        }
    }

    /**
     * 测试不同语言的邮件
     */
    @PostMapping("/multilang")
    public ResponseEntity<MessageResponse> testMultiLanguageEmails(
            @RequestParam String email,
            @RequestParam(defaultValue = "TestUser") String username) {
        
        try {
            // 发送中文邮件
            String activationLinkZh = "http://localhost:8080/activate?token=zh-test-" + System.currentTimeMillis();
            emailService.sendActivationEmail(email, username + "(中文)", activationLinkZh, Locale.SIMPLIFIED_CHINESE);
            
            Thread.sleep(500);
            
            // 发送英文邮件
            String activationLinkEn = "http://localhost:8080/activate?token=en-test-" + System.currentTimeMillis();
            emailService.sendActivationEmail(email, username + "(English)", activationLinkEn, Locale.US);
            
            return ResponseEntity.ok(new MessageResponse("多语言邮件发送成功到: " + email));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("多语言邮件发送失败: " + e.getMessage()));
        }
    }

    /**
     * 获取邮件发送状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<MessageResponse> getEmailStatus() {
        try {
            // 这里可以添加邮件队列状态、发送统计等信息
            return ResponseEntity.ok(new MessageResponse("邮件服务运行正常"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("邮件服务状态检查失败: " + e.getMessage()));
        }
    }
}