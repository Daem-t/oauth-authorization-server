export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  captchaId: string;
  captchaCode: string;
}

export interface LoginRequest {
  username: string;
  password: string;
  captchaId: string;
  captchaCode: string;
}

export interface LoginResponse {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
  refresh_expires_in?: number;
  message?: string;
  timestamp?: number;
  user_info?: {
    id: number;
    username: string;
    email: string;
    status: string;
    roles: string[];
    locale: string;
  };
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface CaptchaResponse {
  captchaId: string;
  image: string;
}

export interface ApiResponse<T = any> {
  message?: string;
  error?: string;
  data?: T;
}