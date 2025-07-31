package com.daem.oauth.application.user;

import com.daem.oauth.interfaces.common.service.MessageService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StopWatch;
import org.thymeleaf.TemplateEngine;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 邮件服务性能测试
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.username=test@example.com",
    "spring.mail.password=test",
    "spring.mail.properties.mail.smtp.auth=false",
    "spring.mail.properties.mail.smtp.starttls.enable=false"
})
class EmailServicePerformanceTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MessageService messageService;

    private EmailService emailService;

    static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@example.com", "test"));

    @BeforeEach
    void setUp() {
        greenMail.start();
        emailService = new EmailService(mailSender, templateEngine, messageService);
    }

    @Test
    void testSingleEmailPerformance() throws Exception {
        // Given
        String toEmail = "performance@example.com";
        String username = "PerformanceUser";
        String activationLink = "http://localhost:8080/activate?token=perf-test";

        // When
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        emailService.sendActivationEmail(toEmail, username, activationLink, Locale.SIMPLIFIED_CHINESE);
        
        stopWatch.stop();

        // Then
        long executionTime = stopWatch.getTotalTimeMillis();
        System.out.println("单个邮件发送耗时: " + executionTime + "ms");
        
        assertThat(executionTime).isLessThan(5000); // 应该在5秒内完成
        assertThat(greenMail.getReceivedMessages()).hasSize(1);
    }

    @Test
    void testBatchEmailPerformance() throws Exception {
        // Given
        int emailCount = 10;
        String baseEmail = "batch-test-";
        String activationLink = "http://localhost:8080/activate?token=batch-perf-test";

        // When
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < emailCount; i++) {
            String email = baseEmail + i + "@example.com";
            String username = "BatchUser" + i;
            emailService.sendActivationEmail(email, username, activationLink, Locale.SIMPLIFIED_CHINESE);
        }

        stopWatch.stop();

        // Then
        long totalTime = stopWatch.getTotalTimeMillis();
        double avgTime = (double) totalTime / emailCount;
        
        System.out.println("批量发送 " + emailCount + " 个邮件总耗时: " + totalTime + "ms");
        System.out.println("平均每个邮件耗时: " + avgTime + "ms");
        
        assertThat(totalTime).isLessThan(30000); // 总时间应该在30秒内
        assertThat(avgTime).isLessThan(3000); // 平均每个邮件应该在3秒内
        assertThat(greenMail.getReceivedMessages()).hasSize(emailCount);
    }

    @Test
    void testConcurrentEmailPerformance() throws Exception {
        // Given
        int emailCount = 20;
        int threadCount = 5;
        String activationLink = "http://localhost:8080/activate?token=concurrent-perf-test";
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // When
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CompletableFuture<Void>[] futures = IntStream.range(0, emailCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        String email = "concurrent-test-" + i + "@example.com";
                        String username = "ConcurrentUser" + i;
                        emailService.sendActivationEmail(email, username, activationLink, Locale.SIMPLIFIED_CHINESE);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
        stopWatch.stop();

        // Then
        long totalTime = stopWatch.getTotalTimeMillis();
        double avgTime = (double) totalTime / emailCount;
        
        System.out.println("并发发送 " + emailCount + " 个邮件总耗时: " + totalTime + "ms");
        System.out.println("平均每个邮件耗时: " + avgTime + "ms");
        System.out.println("使用线程数: " + threadCount);
        
        assertThat(totalTime).isLessThan(20000); // 并发应该更快，20秒内完成
        assertThat(greenMail.getReceivedMessages()).hasSize(emailCount);
        
        executor.shutdown();
    }

    @Test
    void testTemplateRenderingPerformance() throws Exception {
        // Given
        String toEmail = "template-perf@example.com";
        String username = "TemplateUser";
        String activationLink = "http://localhost:8080/activate?token=template-perf-test";
        int renderCount = 100;

        // When - 测试模板渲染性能
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < renderCount; i++) {
            // 只测试模板渲染，不实际发送邮件
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context(Locale.SIMPLIFIED_CHINESE);
            context.setVariable("username", username + i);
            context.setVariable("activationLink", activationLink);
            
            templateEngine.process("email/activation", context);
        }

        stopWatch.stop();

        // Then
        long totalTime = stopWatch.getTotalTimeMillis();
        double avgTime = (double) totalTime / renderCount;
        
        System.out.println("渲染 " + renderCount + " 个模板总耗时: " + totalTime + "ms");
        System.out.println("平均每个模板渲染耗时: " + avgTime + "ms");
        
        assertThat(totalTime).isLessThan(5000); // 模板渲染应该很快
        assertThat(avgTime).isLessThan(100); // 平均每个模板应该在100ms内
    }

    @Test
    void testMultiLanguagePerformance() throws Exception {
        // Given
        String baseEmail = "multilang-perf-";
        String username = "MultiLangUser";
        String activationLink = "http://localhost:8080/activate?token=multilang-perf-test";
        Locale[] locales = {Locale.SIMPLIFIED_CHINESE, Locale.US};
        int emailsPerLanguage = 5;

        // When
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (Locale locale : locales) {
            for (int i = 0; i < emailsPerLanguage; i++) {
                String email = baseEmail + locale.getLanguage() + "-" + i + "@example.com";
                emailService.sendActivationEmail(email, username, activationLink, locale);
            }
        }

        stopWatch.stop();

        // Then
        long totalTime = stopWatch.getTotalTimeMillis();
        int totalEmails = locales.length * emailsPerLanguage;
        double avgTime = (double) totalTime / totalEmails;
        
        System.out.println("多语言发送 " + totalEmails + " 个邮件总耗时: " + totalTime + "ms");
        System.out.println("平均每个邮件耗时: " + avgTime + "ms");
        
        assertThat(totalTime).isLessThan(15000);
        assertThat(greenMail.getReceivedMessages()).hasSize(totalEmails);
    }
}