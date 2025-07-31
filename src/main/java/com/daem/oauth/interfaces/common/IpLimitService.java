package com.daem.oauth.interfaces.common;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class IpLimitService {
    private static final Set<String> BLACK_LIST = ConcurrentHashMap.newKeySet();
    private static final Set<String> WHITE_LIST = ConcurrentHashMap.newKeySet();
    private static final int MAX_ATTEMPT = 30; // 每分钟最大尝试次数

    private final Cache ipCache;

    public IpLimitService(CacheManager cacheManager) {
        this.ipCache = Objects.requireNonNull(cacheManager.getCache(CacheConfig.IP_LIMIT_CACHE));
    }

    /**
     * 检查指定IP是否被允许访问。
     * @param ip 客户端IP地址
     * @return 如果允许访问，返回true；否则返回false。
     */
    public boolean isAllowed(String ip) {
        if (WHITE_LIST.contains(ip)) {
            return true;
        }
        if (BLACK_LIST.contains(ip)) {
            return false;
        }

        AtomicInteger count = ipCache.get(ip, AtomicInteger.class);
        if (count == null) {
            count = new AtomicInteger(0);
        }

        if (count.get() >= MAX_ATTEMPT) {
            return false;
        }

        count.incrementAndGet();
        ipCache.put(ip, count);
        return true;
    }

    /**
     * 重置指定IP的访问计数。
     * @param ip 客户端IP地址
     */
    public void reset(String ip) {
        ipCache.evict(ip);
    }

    /**
     * 将指定IP添加到黑名单。
     * @param ip 客户端IP地址
     */
    public void addToBlackList(String ip) {
        BLACK_LIST.add(ip);
    }

    /**
     * 将指定IP添加到白名单。
     * @param ip 客户端IP地址
     */
    public void addToWhiteList(String ip) {
        WHITE_LIST.add(ip);
    }
}
 