package com.daem.oauth.application.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * 邮件模板测试
 */
@SpringBootTest
@ActiveProfiles("test")
class EmailTemplateTest {

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void testChineseActivationTemplate() {
        // Given
        Context context = new Context(Locale.SIMPLIFIED_CHINESE);
        context.setVariable("username", "测试用户");
        context.setVariable("activationLink", "http://localhost:8080/activate?token=test");

        // When & Then
        assertDoesNotThrow(() -> {
            String result = templateEngine.process("email/activation", context);
            assertThat(result).isNotEmpty();
            assertThat(result).contains("测试用户");
            assertThat(result).contains("OAuth认证系统");
            assertThat(result).contains("立即激活账户");
        });
    }

    @Test
    void testEnglishActivationTemplate() {
        // Given
        Context context = new Context(Locale.US);
        context.setVariable("username", "TestUser");
        context.setVariable("activationLink", "http://localhost:8080/activate?token=test");

        // When & Then
        assertDoesNotThrow(() -> {
            String result = templateEngine.process("email/activation_en", context);
            assertThat(result).isNotEmpty();
            assertThat(result).contains("TestUser");
            assertThat(result).contains("OAuth Authentication System");
            assertThat(result).contains("Activate Account Now");
        });
    }

    @Test
    void testChinesePasswordResetTemplate() {
        // Given
        Context context = new Context(Locale.SIMPLIFIED_CHINESE);
        context.setVariable("username", "测试用户");
        context.setVariable("resetLink", "http://localhost:8080/reset-password?token=test");

        // When & Then
        assertDoesNotThrow(() -> {
            String result = templateEngine.process("email/password-reset", context);
            assertThat(result).isNotEmpty();
            assertThat(result).contains("测试用户");
            assertThat(result).contains("密码重置");
            assertThat(result).contains("重置密码");
        });
    }

    @Test
    void testEnglishPasswordResetTemplate() {
        // Given
        Context context = new Context(Locale.US);
        context.setVariable("username", "TestUser");
        context.setVariable("resetLink", "http://localhost:8080/reset-password?token=test");

        // When & Then
        assertDoesNotThrow(() -> {
            String result = templateEngine.process("email/password-reset_en", context);
            assertThat(result).isNotEmpty();
            assertThat(result).contains("TestUser");
            assertThat(result).contains("Password Reset");
            assertThat(result).contains("Reset Password");
        });
    }

    @Test
    void testTemplateStructure() {
        // Given
        Context context = new Context();
        context.setVariable("username", "TestUser");
        context.setVariable("activationLink", "http://localhost:8080/activate?token=test");

        // When
        String result = templateEngine.process("email/activation", context);

        // Then
        assertThat(result).contains("<!DOCTYPE html");
        assertThat(result).contains("<html");
        assertThat(result).contains("</html>");
        assertThat(result).contains("<style>");
        assertThat(result).contains("</style>");
        assertThat(result).contains("class=\"container\"");
        assertThat(result).contains("class=\"btn\"");
    }
}