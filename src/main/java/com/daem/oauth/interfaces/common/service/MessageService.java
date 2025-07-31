package com.daem.oauth.interfaces.common.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {
    
    private final MessageSource messageSource;
    
    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    /**
     * 获取国际化消息
     * @param code 消息代码
     * @return 国际化消息
     */
    public String getMessage(String code) {
        return getMessage(code, null);
    }
    
    /**
     * 获取国际化消息
     * @param code 消息代码
     * @param args 消息参数
     * @return 国际化消息
     */
    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, LocaleContextHolder.getLocale());
    }
    
    /**
     * 获取国际化消息
     * @param code 消息代码
     * @param args 消息参数
     * @param locale 语言环境
     * @return 国际化消息
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, code, locale);
    }
}