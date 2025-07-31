package com.daem.oauth.interfaces.common;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

/**
 * 邮件模板预览控制器
 * 仅在开发环境中启用
 */
@Controller
@RequestMapping("/dev/email-preview")
@Profile("dev")
public class EmailPreviewController {

    @GetMapping("/activation")
    public String previewActivationEmail(
            @RequestParam(defaultValue = "zh") String lang,
            Model model) {
        
        // 设置模板变量
        model.addAttribute("username", "测试用户");
        model.addAttribute("activationLink", "http://localhost:8080/activate?token=sample-token-123");
        
        // 根据语言选择模板
        if ("en".equals(lang)) {
            return "email/activation_en";
        } else {
            return "email/activation";
        }
    }

    @GetMapping("/password-reset")
    public String previewPasswordResetEmail(
            @RequestParam(defaultValue = "zh") String lang,
            Model model) {
        
        // 设置模板变量
        model.addAttribute("username", "测试用户");
        model.addAttribute("resetLink", "http://localhost:8080/reset-password?token=sample-token-123");
        
        // 根据语言选择模板
        if ("en".equals(lang)) {
            return "email/password-reset_en";
        } else {
            return "email/password-reset";
        }
    }
}