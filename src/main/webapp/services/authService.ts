import axios from '../utils/axiosConfig';
import { RegisterRequest, LoginRequest, LoginResponse, RefreshTokenRequest, CaptchaResponse, ApiResponse } from '../types/auth';

class AuthService {
  private static instance: AuthService;
  
  private constructor() {}
  
  public static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  /**
   * 获取验证码
   */
  async getCaptcha(): Promise<CaptchaResponse> {
    const response = await axios.get<CaptchaResponse>('/api/captcha');
    return response.data;
  }

  /**
   * 用户注册
   */
  async register(data: RegisterRequest): Promise<ApiResponse> {
    const response = await axios.post<ApiResponse>('/api/auth/register', data);
    return response.data;
  }

  /**
   * 用户登录
   */
  async login(data: LoginRequest): Promise<LoginResponse> {
    const response = await axios.post<LoginResponse>('/api/auth/login', data);
    return response.data;
  }
  
  /**
   * 刷新访问令牌
   */
  async refreshToken(refreshToken: string): Promise<LoginResponse> {
    const response = await axios.post<LoginResponse>('/api/auth/refresh', {
      refreshToken
    });
    return response.data;
  }
  
  /**
   * 用户登出
   */
  async logout(): Promise<ApiResponse> {
    const response = await axios.post<ApiResponse>('/api/auth/logout');
    return response.data;
  }

  /**
   * 激活账户
   */
  async activate(token: string): Promise<ApiResponse> {
    const response = await axios.get<ApiResponse>(`/api/auth/activate?token=${token}`);
    return response.data;
  }

  /**
   * 验证验证码
   */
  async verifyCaptcha(captchaId: string, code: string): Promise<{ result: boolean }> {
    const response = await axios.post<{ result: boolean }>('/api/captcha/verify', {
      captchaId,
      code
    });
    return response.data;
  }
}

export default AuthService.getInstance();