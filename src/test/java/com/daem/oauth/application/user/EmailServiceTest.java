package com.daem.oauth.application.user;

import com.daem.oauth.interfaces.common.service.MessageService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MessageService messageService;

    @Mock
    private MimeMessage mimeMessage;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, templateEngine, messageService);
    }

    @Test
    void testSendActivationEmailInChinese() {
        // Given
        String toEmail = "test@example.com";
        String username = "测试用户";
        String activationLink = "http://localhost:8080/activate?token=123";
        Locale locale = Locale.SIMPLIFIED_CHINESE;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageService.getMessage(eq("email.activation.subject"), any(), eq(locale)))
                .thenReturn("账户激活 - OAuth认证系统");
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>测试邮件内容</body></html>");

        // When
        emailService.sendActivationEmail(toEmail, username, activationLink, locale);

        // Then
        verify(messageService).getMessage("email.activation.subject", new Object[]{"OAuth认证系统"}, locale);
        
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(anyString(), contextCaptor.capture());
        
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getLocale()).isEqualTo(locale);
        assertThat(capturedContext.getVariable("username")).isEqualTo(username);
        assertThat(capturedContext.getVariable("activationLink")).isEqualTo(activationLink);
        
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendActivationEmailInEnglish() {
        // Given
        String toEmail = "test@example.com";
        String username = "TestUser";
        String activationLink = "http://localhost:8080/activate?token=123";
        Locale locale = Locale.US;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageService.getMessage(eq("email.activation.subject"), any(), eq(locale)))
                .thenReturn("Account Activation - OAuth Authentication System");
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>Test email content</body></html>");

        // When
        emailService.sendActivationEmail(toEmail, username, activationLink, locale);

        // Then
        verify(messageService).getMessage("email.activation.subject", new Object[]{"OAuth认证系统"}, locale);
        
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(anyString(), contextCaptor.capture());
        
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getLocale()).isEqualTo(locale);
        assertThat(capturedContext.getVariable("username")).isEqualTo(username);
        assertThat(capturedContext.getVariable("activationLink")).isEqualTo(activationLink);
        
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendPasswordResetEmailInChinese() {
        // Given
        String toEmail = "test@example.com";
        String username = "测试用户";
        String resetLink = "http://localhost:8080/reset-password?token=123";
        Locale locale = Locale.SIMPLIFIED_CHINESE;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageService.getMessage(eq("email.password.reset.subject"), any(), eq(locale)))
                .thenReturn("密码重置 - OAuth认证系统");
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>密码重置邮件</body></html>");

        // When
        emailService.sendPasswordResetEmail(toEmail, username, resetLink, locale);

        // Then
        verify(messageService).getMessage("email.password.reset.subject", new Object[]{"OAuth认证系统"}, locale);
        
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(anyString(), contextCaptor.capture());
        
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getLocale()).isEqualTo(locale);
        assertThat(capturedContext.getVariable("username")).isEqualTo(username);
        assertThat(capturedContext.getVariable("resetLink")).isEqualTo(resetLink);
        
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendPasswordResetEmailInEnglish() {
        // Given
        String toEmail = "test@example.com";
        String username = "TestUser";
        String resetLink = "http://localhost:8080/reset-password?token=123";
        Locale locale = Locale.US;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageService.getMessage(eq("email.password.reset.subject"), any(), eq(locale)))
                .thenReturn("Password Reset - OAuth Authentication System");
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>Password reset email</body></html>");

        // When
        emailService.sendPasswordResetEmail(toEmail, username, resetLink, locale);

        // Then
        verify(messageService).getMessage("email.password.reset.subject", new Object[]{"OAuth认证系统"}, locale);
        
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(anyString(), contextCaptor.capture());
        
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getLocale()).isEqualTo(locale);
        assertThat(capturedContext.getVariable("username")).isEqualTo(username);
        assertThat(capturedContext.getVariable("resetLink")).isEqualTo(resetLink);
        
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendActivationEmailWithDefaultLocale() {
        // Given
        String toEmail = "test@example.com";
        String username = "TestUser";
        String activationLink = "http://localhost:8080/activate?token=123";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageService.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("账户激活 - OAuth认证系统");
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>测试邮件</body></html>");

        // When
        emailService.sendActivationEmail(toEmail, username, activationLink);

        // Then
        verify(messageService).getMessage(eq("email.activation.subject"), any(), any(Locale.class));
        verify(templateEngine).process(anyString(), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testEmailSendingFailure() {
        // Given
        String toEmail = "test@example.com";
        String username = "TestUser";
        String activationLink = "http://localhost:8080/activate?token=123";
        Locale locale = Locale.SIMPLIFIED_CHINESE;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageService.getMessage(anyString(), any(), eq(locale)))
                .thenReturn("账户激活 - OAuth认证系统");
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>测试邮件</body></html>");
        
        // 模拟邮件发送失败
        doThrow(new RuntimeException("SMTP server not available"))
                .when(mailSender).send(any(MimeMessage.class));
        
        when(messageService.getMessage(eq("email.send.failed"), any(), eq(locale)))
                .thenReturn("邮件发送失败: SMTP server not available");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendActivationEmail(toEmail, username, activationLink, locale);
        });

        assertThat(exception.getMessage()).contains("邮件发送失败");
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testTemplateProcessingFailure() {
        // Given
        String toEmail = "test@example.com";
        String username = "TestUser";
        String activationLink = "http://localhost:8080/activate?token=123";
        Locale locale = Locale.SIMPLIFIED_CHINESE;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageService.getMessage(anyString(), any(), eq(locale)))
                .thenReturn("账户激活 - OAuth认证系统");
        
        // 模拟模板处理失败
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenThrow(new RuntimeException("Template not found"));
        
        when(messageService.getMessage(eq("email.send.failed"), any(), eq(locale)))
                .thenReturn("邮件发送失败: Template not found");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendActivationEmail(toEmail, username, activationLink, locale);
        });

        assertThat(exception.getMessage()).contains("邮件发送失败");
        verify(templateEngine).process(anyString(), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testLocalizedTemplateSelection() {
        // Given
        String toEmail = "test@example.com";
        String username = "TestUser";
        String activationLink = "http://localhost:8080/activate?token=123";
        Locale locale = Locale.US;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageService.getMessage(anyString(), any(), eq(locale)))
                .thenReturn("Account Activation - OAuth Authentication System");
        
        // 模拟英文模板存在
        when(templateEngine.process(eq("email/activation_en"), any(Context.class)))
                .thenReturn("<html><body>English email content</body></html>");

        // When
        emailService.sendActivationEmail(toEmail, username, activationLink, locale);

        // Then
        verify(templateEngine).process(eq("email/activation_en"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }
}