import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import authService from '../services/authService';
import { TokenManager } from './tokenManager';

// 请求拦截器：自动添加JWT令牌
axios.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const token = TokenManager.getAccessToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器：自动处理令牌过期
axios.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    // 如果是401错误且不是登录或刷新令牌请求
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      const refreshToken = TokenManager.getRefreshToken();
      
      if (refreshToken && !originalRequest.url?.includes('/auth/login') && !originalRequest.url?.includes('/auth/refresh')) {
        try {
          // 尝试刷新令牌
          const response = await authService.refreshToken(refreshToken);
          
          // 使用TokenManager更新令牌
          TokenManager.saveTokens(response.access_token, response.refresh_token, response.expires_in);
          
          // 如果有用户信息，也更新一下
          if (response.user_info) {
            localStorage.setItem('user_info', JSON.stringify(response.user_info));
          }
          
          // 重新发送原始请求
          originalRequest.headers.Authorization = `Bearer ${response.access_token}`;
          return axios(originalRequest);
          
        } catch (refreshError) {
          console.error('Token refresh failed:', refreshError);
          // 刷新失败，清除所有令牌和用户信息
          TokenManager.clearTokens();
          localStorage.removeItem('user_info');
          
          // 如果不是在登录页面，则跳转到登录页
          if (!window.location.pathname.includes('/login')) {
            window.location.href = '/login?return_url=' + encodeURIComponent(window.location.pathname);
          }
        }
      } else {
        // 没有刷新令牌，清除所有数据并跳转到登录页
        TokenManager.clearTokens();
        localStorage.removeItem('user_info');
        
        if (!window.location.pathname.includes('/login')) {
          window.location.href = '/login?return_url=' + encodeURIComponent(window.location.pathname);
        }
      }
    }
    
    return Promise.reject(error);
  }
);

export default axios;