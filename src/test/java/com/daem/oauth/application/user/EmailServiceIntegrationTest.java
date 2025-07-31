package com.daem.oauth.application.user;

import com.daem.oauth.interfaces.common.service.MessageService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.TemplateEngine;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 邮件服务集成测试
 * 使用GreenMail进行邮件发送测试
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.mail.host=smtp.qq.com",
    "spring.mail.port=465",
    "spring.mail.username=945460266@qq.com",
    "spring.mail.password=phglrqxfjvqabdjf",
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.starttls.enable=false"
})
class EmailServiceIntegrationTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MessageService messageService;

    private EmailService emailService;

    // GreenMail扩展，用于模拟SMTP服务器
    static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("945460266@qq.com", "phglrqxfjvqabdjf"));

    @BeforeEach
    void setUp() {
        greenMail.start();
        emailService = new EmailService(mailSender, templateEngine, messageService);
    }

    @Test
    void testSendActivationEmailInChinese() throws Exception {
        // Given
        String toEmail = "t.daem28@gmail.com";
        String username = "测试用户";
        String activationLink = "http://localhost:8080/activate?token=test-token-123";
        Locale locale = Locale.SIMPLIFIED_CHINESE;

        // When
        emailService.sendActivationEmail(toEmail, username, activationLink, locale);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals(toEmail, message.getAllRecipients()[0].toString());
        assertTrue(message.getSubject().contains("账户激活"));
        
        String content = message.getContent().toString();
        assertTrue(content.contains(username));
        assertTrue(content.contains(activationLink));
        assertTrue(content.contains("OAuth认证系统"));
        assertTrue(content.contains("立即激活账户"));
    }

    @Test
    void testSendActivationEmailInEnglish() throws Exception {
        // Given
        String toEmail = "user@example.com";
        String username = "TestUser";
        String activationLink = "http://localhost:8080/activate?token=test-token-123";
        Locale locale = Locale.US;

        // When
        emailService.sendActivationEmail(toEmail, username, activationLink, locale);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals(toEmail, message.getAllRecipients()[0].toString());
        assertTrue(message.getSubject().contains("Account Activation"));
        
        String content = message.getContent().toString();
        assertTrue(content.contains(username));
        assertTrue(content.contains(activationLink));
        assertTrue(content.contains("OAuth Authentication System"));
        assertTrue(content.contains("Activate Account Now"));
    }

    @Test
    void testSendPasswordResetEmailInChinese() throws Exception {
        // Given
        String toEmail = "user@example.com";
        String username = "测试用户";
        String resetLink = "http://localhost:8080/reset-password?token=test-token-123";
        Locale locale = Locale.SIMPLIFIED_CHINESE;

        // When
        emailService.sendPasswordResetEmail(toEmail, username, resetLink, locale);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals(toEmail, message.getAllRecipients()[0].toString());
        assertTrue(message.getSubject().contains("密码重置"));
        
        String content = message.getContent().toString();
        assertTrue(content.contains(username));
        assertTrue(content.contains(resetLink));
        assertTrue(content.contains("重置密码"));
    }

    @Test
    void testSendPasswordResetEmailInEnglish() throws Exception {
        // Given
        String toEmail = "user@example.com";
        String username = "TestUser";
        String resetLink = "http://localhost:8080/reset-password?token=test-token-123";
        Locale locale = Locale.US;

        // When
        emailService.sendPasswordResetEmail(toEmail, username, resetLink, locale);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals(toEmail, message.getAllRecipients()[0].toString());
        assertTrue(message.getSubject().contains("Password Reset"));
        
        String content = message.getContent().toString();
        assertTrue(content.contains(username));
        assertTrue(content.contains(resetLink));
        assertTrue(content.contains("Reset Password"));
    }

    @Test
    void testEmailContentStructure() throws Exception {
        // Given
        String toEmail = "user@example.com";
        String username = "TestUser";
        String activationLink = "http://localhost:8080/activate?token=test-token-123";

        // When
        emailService.sendActivationEmail(toEmail, username, activationLink, Locale.SIMPLIFIED_CHINESE);

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        MimeMessage message = receivedMessages[0];
        String content = message.getContent().toString();

        // 验证HTML结构
        assertTrue(content.contains("<!DOCTYPE html"));
        assertTrue(content.contains("<html"));
        assertTrue(content.contains("</html>"));
        
        // 验证CSS样式
        assertTrue(content.contains("<style>"));
        assertTrue(content.contains("</style>"));
        
        // 验证关键元素
        assertTrue(content.contains("class=\"container\""));
        assertTrue(content.contains("class=\"btn\""));
        assertTrue(content.contains("class=\"warning\""));
        
        // 验证链接
        assertTrue(content.contains("href=\"" + activationLink + "\""));
    }

    @Test
    void testMultipleEmailsSent() throws Exception {
        // Given
        String[] emails = {"user1@example.com", "user2@example.com", "user3@example.com"};
        String activationLink = "http://localhost:8080/activate?token=test-token";

        // When
        for (int i = 0; i < emails.length; i++) {
            emailService.sendActivationEmail(emails[i], "User" + (i + 1), activationLink + (i + 1), Locale.SIMPLIFIED_CHINESE);
        }

        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(emails.length, receivedMessages.length);

        for (int i = 0; i < emails.length; i++) {
            assertEquals(emails[i], receivedMessages[i].getAllRecipients()[0].toString());
            assertTrue(receivedMessages[i].getContent().toString().contains("User" + (i + 1)));
        }
    }
}