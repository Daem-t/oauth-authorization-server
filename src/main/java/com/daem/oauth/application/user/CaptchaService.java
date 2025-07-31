package com.daem.oauth.application.user;

import com.daem.oauth.interfaces.common.CacheConfig;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CaptchaService {
    
    private final Cache captchaCache;
    
    public CaptchaService(CacheManager cacheManager) {
        this.captchaCache = Objects.requireNonNull(cacheManager.getCache(CacheConfig.CAPTCHA_CACHE));
    }
    
    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param captchaCode 用户输入的验证码
     * @return 验证是否成功
     */
    public boolean verifyCaptcha(String captchaId, String captchaCode) {
        if (captchaId == null || captchaCode == null) {
            return false;
        }
        
        Cache.ValueWrapper wrapper = captchaCache.get(captchaId);
        if (wrapper == null) {
            return false; // 验证码不存在或已过期
        }
        
        String storedCode = (String) wrapper.get();
        boolean isValid = captchaCode.equalsIgnoreCase(storedCode);
        
        // 验证后立即删除验证码，防止重复使用
        if (isValid) {
            captchaCache.evict(captchaId);
        }
        
        return isValid;
    }
}