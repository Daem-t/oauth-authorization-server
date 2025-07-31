package com.daem.oauth.interfaces.common.constants;

/**
 * 消息常量类 - 用于国际化消息键
 */
public final class MessageConstants {
    
    private MessageConstants() {
        // 工具类，禁止实例化
    }
    
    // 认证相关消息
    public static final class Auth {
        public static final String REGISTER_SUCCESS = "auth.register.success";
        public static final String REGISTER_FAILED = "auth.register.failed";
        public static final String LOGIN_SUCCESS = "auth.login.success";
        public static final String LOGIN_FAILED = "auth.login.failed";
        public static final String LOGIN_INVALID_CREDENTIALS = "auth.login.invalid.credentials";
        public static final String LOGIN_ACCOUNT_DISABLED = "auth.account.disabled";
        public static final String LOGIN_ACCOUNT_LOCKED = "auth.account.locked";
        public static final String LOGIN_TOO_MANY_ATTEMPTS = "auth.login.too.many.attempts";
        public static final String ACTIVATE_SUCCESS = "auth.activate.success";
        public static final String ACTIVATE_FAILED = "auth.activate.failed";
        public static final String LOGOUT_SUCCESS = "auth.logout.success";
        public static final String TOKEN_REFRESH_SUCCESS = "auth.token.refresh.success";
        public static final String TOKEN_REFRESH_FAILED = "auth.token.refresh.failed";
        public static final String TOKEN_INVALID = "auth.token.invalid";
        public static final String TOKEN_EXPIRED = "auth.token.expired";
    }
    
    // 管理员相关消息
    public static final class Admin {
        public static final String USERS_UPDATE_SUCCESS = "admin.users.update.success";
        public static final String USERS_STATUS_UPDATE_SUCCESS = "admin.users.status.update.success";
        public static final String USERS_PASSWORD_RESET_SUCCESS = "admin.users.password.reset.success";
        public static final String USERS_BATCH_UPDATE_SUCCESS = "admin.users.batch.update.success";
        public static final String ACCESS_DENIED = "admin.access.denied";
    }
    
    // 用户相关消息
    public static final class User {
        public static final String USER_NOT_FOUND = "user.not.found";
        public static final String USER_ALREADY_EXISTS = "user.already.exists";
        public static final String USER_NOT_ACTIVATED = "user.not.activated";
        public static final String USER_AUTHENTICATION_INCONSISTENT = "user.authentication.inconsistent";
        public static final String EMAIL_ALREADY_EXISTS = "email.already.exists";
        public static final String ACTIVATION_TOKEN_INVALID = "user.activation.token.invalid";
        public static final String ACTIVATION_TOKEN_EXPIRED = "user.activation.token.expired";
        public static final String ACTIVATION_SUCCESS = "user.activation.success";
        public static final String REGISTRATION_SUCCESS = "user.registration.success";
        public static final String ALREADY_ACTIVE = "user.already.active";
    }
    
    // 验证相关消息
    public static final class Validation {
        public static final String INVALID_CAPTCHA = "validation.captcha.invalid";
        public static final String CAPTCHA_EXPIRED = "validation.captcha.expired";
        public static final String INVALID_TOKEN = "validation.token.invalid";
        public static final String TOKEN_EXPIRED = "validation.token.expired";
        public static final String PARAMETER_INVALID = "validation.parameter.invalid";
        public static final String CONSTRAINT_VIOLATION = "validation.constraint.violation";
        public static final String USERNAME_REQUIRED = "validation.username.required";
        public static final String USERNAME_LENGTH = "validation.username.length";
        public static final String PASSWORD_REQUIRED = "validation.password.required";
        public static final String PASSWORD_LENGTH = "validation.password.length";
        public static final String EMAIL_REQUIRED = "validation.email.required";
        public static final String EMAIL_FORMAT = "validation.email.format";
        public static final String STATUS_REQUIRED = "validation.status.required";
        public static final String STATUS_INVALID = "validation.status.invalid";
        public static final String USER_IDS_REQUIRED = "validation.userIds.required";
        public static final String PAGE_MIN = "validation.page.min";
        public static final String SIZE_MIN = "validation.size.min";
        public static final String SIZE_MAX = "validation.size.max";
    }
    
    // 系统相关消息
    public static final class System {
        public static final String INTERNAL_ERROR = "system.internal.error";
        public static final String SERVICE_UNAVAILABLE = "system.service.unavailable";
        public static final String IP_LIMIT_EXCEEDED = "system.ip.limit.exceeded";
    }
}