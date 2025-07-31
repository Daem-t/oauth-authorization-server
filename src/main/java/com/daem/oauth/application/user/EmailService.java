package com.daem.oauth.application.user;

import com.daem.oauth.interfaces.common.service.MessageService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MessageService messageService;
    
    @Value("${spring.mail.username:noreply@example.com}")
    private String fromEmail;
    
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, MessageService messageService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageService = messageService;
    }
    
    /**
     * 发送账户激活邮件
     * @param toEmail 收件人邮箱
     * @param username 用户名
     * @param activationLink 激活链接
     */
    public void sendActivationEmail(String toEmail, String username, String activationLink) {
        sendActivationEmail(toEmail, username, activationLink, LocaleContextHolder.getLocale());
    }
    
    /**
     * 发送账户激活邮件（指定语言）
     * @param toEmail 收件人邮箱
     * @param username 用户名
     * @param activationLink 激活链接
     * @param locale 语言环境
     */
    public void sendActivationEmail(String toEmail, String username, String activationLink, Locale locale) {
        Context context = new Context(locale);
        context.setVariable("username", username);
        context.setVariable("activationLink", activationLink);
        
        // 获取国际化的邮件主题和内容
        String subject = messageService.getMessage("email.activation.subject", new Object[]{"OAuth认证系统"}, locale);
        String templateName = getLocalizedTemplateName("email/activation", locale);
        
        sendHtmlEmail(toEmail, subject, templateName, context, locale);
    }
    
    /**
     * 发送密码重置邮件
     * @param toEmail 收件人邮箱
     * @param username 用户名
     * @param resetLink 重置链接
     */
    public void sendPasswordResetEmail(String toEmail, String username, String resetLink) {
        sendPasswordResetEmail(toEmail, username, resetLink, LocaleContextHolder.getLocale());
    }
    
    /**
     * 发送密码重置邮件（指定语言）
     * @param toEmail 收件人邮箱
     * @param username 用户名
     * @param resetLink 重置链接
     * @param locale 语言环境
     */
    public void sendPasswordResetEmail(String toEmail, String username, String resetLink, Locale locale) {
        Context context = new Context(locale);
        context.setVariable("username", username);
        context.setVariable("resetLink", resetLink);
        
        String subject = messageService.getMessage("email.password.reset.subject", new Object[]{"OAuth认证系统"}, locale);
        String templateName = getLocalizedTemplateName("email/password-reset", locale);
        
        sendHtmlEmail(toEmail, subject, templateName, context, locale);
    }
    
    /**
     * 发送通用HTML邮件
     * @param toEmail 收件人邮箱
     * @param subject 邮件主题
     * @param templateName 模板名称
     * @param context 模板变量
     * @param locale 语言环境
     */
    private void sendHtmlEmail(String toEmail, String subject, String templateName, Context context, Locale locale) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            
            // 设置模板上下文的语言环境
            context.setLocale(locale);
            
            // 渲染HTML模板
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            String errorMessage = messageService.getMessage("email.send.failed", 
                    new Object[]{e.getMessage()}, locale);
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    /**
     * 获取本地化的模板名称
     * @param baseTemplateName 基础模板名称
     * @param locale 语言环境
     * @return 本地化模板名称
     */
    private String getLocalizedTemplateName(String baseTemplateName, Locale locale) {
        // 根据语言环境选择模板
        String language = locale.getLanguage();
        String country = locale.getCountry();
        
        // 对于英文，尝试使用英文模板
        if ("en".equals(language)) {
            // 尝试完整的语言-国家代码
            if (country != null && !country.isEmpty()) {
                String fullLocaleName = language + "_" + country.toUpperCase();
                String templateName = baseTemplateName + "_" + fullLocaleName;
                if (templateExists(templateName)) {
                    return templateName;
                }
            }
            
            // 尝试仅语言代码
            String templateName = baseTemplateName + "_" + language;
            if (templateExists(templateName)) {
                return templateName;
            }
        }
        
        // 对于中文或其他语言，使用默认模板
        // 默认模板就是中文模板
        return baseTemplateName;
    }
    
    /**
     * 检查模板是否存在
     * @param templateName 模板名称
     * @return 是否存在
     */
    private boolean templateExists(String templateName) {
        try {
            // 使用空的上下文尝试解析模板
            Context emptyContext = new Context();
            emptyContext.setVariable("username", "test");
            emptyContext.setVariable("activationLink", "test");
            emptyContext.setVariable("resetLink", "test");
            
            // 尝试处理模板，如果模板不存在会抛出异常
            templateEngine.process(templateName, emptyContext);
            return true;
        } catch (Exception e) {
            // 模板不存在或处理失败
            return false;
        }
    }
}