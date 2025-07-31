package com.daem.oauth.interfaces.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * 语言环境工具类
 */
public final class LocaleUtil {
    
    private LocaleUtil() {
        // 工具类，禁止实例化
    }
    
    /**
     * 从HTTP请求中检测用户的语言偏好
     * @param request HTTP请求
     * @return 语言环境
     */
    public static Locale detectLocaleFromRequest(HttpServletRequest request) {
        // 1. 首先检查URL参数中的lang参数
        String langParam = request.getParameter("lang");
        if (StringUtils.hasText(langParam)) {
            return parseLocale(langParam);
        }
        
        // 2. 检查Accept-Language请求头
        String acceptLanguage = request.getHeader("Accept-Language");
        if (StringUtils.hasText(acceptLanguage)) {
            return parseAcceptLanguageHeader(acceptLanguage);
        }
        
        // 3. 默认返回中文
        return Locale.SIMPLIFIED_CHINESE;
    }
    
    /**
     * 解析语言字符串为Locale对象
     * @param langString 语言字符串
     * @return Locale对象
     */
    public static Locale parseLocale(String langString) {
        if (!StringUtils.hasText(langString)) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        
        // 处理常见的语言代码格式
        switch (langString.toLowerCase()) {
            case "zh":
            case "zh-cn":
            case "zh_cn":
                return Locale.SIMPLIFIED_CHINESE;
            case "zh-tw":
            case "zh_tw":
                return Locale.TRADITIONAL_CHINESE;
            case "en":
            case "en-us":
            case "en_us":
                return Locale.US;
            case "en-gb":
            case "en_gb":
                return Locale.UK;
            default:
                // 尝试解析标准格式 language_country 或 language-country
                if (langString.contains("_") || langString.contains("-")) {
                    String[] parts = langString.split("[_-]");
                    if (parts.length >= 2) {
                        return new Locale(parts[0].toLowerCase(), parts[1].toUpperCase());
                    } else {
                        return new Locale(parts[0].toLowerCase());
                    }
                } else {
                    return new Locale(langString.toLowerCase());
                }
        }
    }
    
    /**
     * 解析Accept-Language请求头
     * @param acceptLanguage Accept-Language头的值
     * @return 最匹配的Locale
     */
    private static Locale parseAcceptLanguageHeader(String acceptLanguage) {
        // Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
        String[] languages = acceptLanguage.split(",");
        
        for (String lang : languages) {
            // 移除质量值 (q=0.9)
            String langCode = lang.split(";")[0].trim();
            
            // 优先匹配中文
            if (langCode.startsWith("zh")) {
                if (langCode.contains("TW") || langCode.contains("HK") || langCode.contains("MO")) {
                    return Locale.TRADITIONAL_CHINESE;
                } else {
                    return Locale.SIMPLIFIED_CHINESE;
                }
            }
            
            // 匹配英文
            if (langCode.startsWith("en")) {
                return Locale.US;
            }
        }
        
        // 默认返回中文
        return Locale.SIMPLIFIED_CHINESE;
    }
    
    /**
     * 获取语言代码字符串（用于前端）
     * @param locale Locale对象
     * @return 语言代码字符串
     */
    public static String getLanguageCode(Locale locale) {
        if (locale == null) {
            return "zh-CN";
        }
        
        String language = locale.getLanguage();
        String country = locale.getCountry();
        
        if (StringUtils.hasText(country)) {
            return language + "-" + country;
        } else {
            return language;
        }
    }
    
    /**
     * 检查是否为中文环境
     * @param locale Locale对象
     * @return 是否为中文
     */
    public static boolean isChinese(Locale locale) {
        return locale != null && "zh".equals(locale.getLanguage());
    }
    
    /**
     * 检查是否为英文环境
     * @param locale Locale对象
     * @return 是否为英文
     */
    public static boolean isEnglish(Locale locale) {
        return locale != null && "en".equals(locale.getLanguage());
    }
}