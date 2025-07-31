package com.daem.oauth.interfaces.common;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CAPTCHA_CACHE = "captcha";
    public static final String IP_LIMIT_CACHE = "ipLimit";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // 验证码缓存：5分钟过期，最多1000个
        cacheManager.registerCustomCache(CAPTCHA_CACHE, 
            Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .maximumSize(1000)
                .build());
        
        // IP限制缓存：1小时过期，最多10000个
        cacheManager.registerCustomCache(IP_LIMIT_CACHE,
            Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10000)
                .build());
        
        return cacheManager;
    }
}