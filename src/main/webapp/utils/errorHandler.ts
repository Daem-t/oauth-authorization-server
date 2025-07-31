import { TFunction } from 'i18next';

/**
 * 错误响应接口（对应后台的ErrorResponse）
 */
export interface ErrorResponse {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  code?: string;
  timestamp?: string;
  errors?: FieldError[];
  traceId?: string;
}

export interface FieldError {
  field: string;
  message: string;
  rejectedValue?: any;
}

/**
 * 旧版本的错误响应格式（向后兼容）
 */
export interface LegacyErrorResponse {
  error?: string;
  message?: string;
  code?: string;
  timestamp?: number;
}

/**
 * 错误处理工具类
 */
export class ErrorHandler {
  
  /**
   * 提取错误消息
   * @param error 错误对象
   * @param t 翻译函数
   * @param defaultKey 默认翻译键
   * @returns 错误消息
   */
  static extractErrorMessage(error: any, t: TFunction, defaultKey: string): string {
    if (!error?.response?.data) {
      return t(defaultKey);
    }

    const errorData = error.response.data;

    // 处理新版ErrorResponse格式
    if (errorData.detail) {
      return errorData.detail;
    }

    // 处理字段验证错误
    if (errorData.errors && Array.isArray(errorData.errors) && errorData.errors.length > 0) {
      return errorData.errors[0].message;
    }

    // 处理旧版格式（向后兼容）
    if (errorData.message) {
      return errorData.message;
    }

    if (errorData.error) {
      return errorData.error;
    }

    // 根据错误码返回对应的翻译
    if (errorData.code) {
      const translationKey = this.getTranslationKeyByCode(errorData.code);
      const translatedMessage = t(translationKey);
      if (translatedMessage !== translationKey) {
        return translatedMessage;
      }
    }

    return t(defaultKey);
  }

  /**
   * 根据错误码获取翻译键
   */
  private static getTranslationKeyByCode(code: string): string {
    const codeMap: Record<string, string> = {
      'VALIDATION_ERROR': 'error.validationError',
      'INVALID_CAPTCHA': 'error.invalidCaptcha',
      'IP_LIMIT_EXCEEDED': 'error.ipLimitExceeded',
      'USER_ALREADY_EXISTS': 'error.userAlreadyExists',
      'EMAIL_ALREADY_EXISTS': 'error.emailAlreadyExists',
      'USER_NOT_ACTIVATED': 'error.userNotActivated',
      'BAD_CREDENTIALS': 'error.badCredentials',
      'AUTHENTICATION_ERROR': 'error.authenticationError',
      'AUTHORIZATION_ERROR': 'error.authorizationError',
      'NOT_FOUND': 'error.notFound',
      'INTERNAL_ERROR': 'error.internalError',
      'INVALID_TOKEN': 'error.invalidToken',
      'TOKEN_EXPIRED': 'error.tokenExpired',
    };

    return codeMap[code] || 'error.unknownError';
  }

  /**
   * 获取字段验证错误信息
   */
  static getFieldErrors(error: any): FieldError[] {
    if (!error?.response?.data?.errors) {
      return [];
    }

    return error.response.data.errors;
  }

  /**
   * 检查是否为网络错误
   */
  static isNetworkError(error: any): boolean {
    return !error.response || error.code === 'NETWORK_ERROR';
  }

  /**
   * 检查是否为服务器错误
   */
  static isServerError(error: any): boolean {
    return error.response && error.response.status >= 500;
  }

  /**
   * 检查是否为客户端错误
   */
  static isClientError(error: any): boolean {
    return error.response && error.response.status >= 400 && error.response.status < 500;
  }
}