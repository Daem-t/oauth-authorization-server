/**
 * JWT令牌管理工具类
 */
export class TokenManager {
  private static readonly ACCESS_TOKEN_KEY = 'access_token';
  private static readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private static readonly TOKEN_EXPIRES_AT_KEY = 'token_expires_at';

  /**
   * 保存令牌
   */
  static saveTokens(accessToken: string, refreshToken: string, expiresIn: number): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
    localStorage.setItem(this.TOKEN_EXPIRES_AT_KEY, String(Date.now() + (expiresIn * 1000)));
  }

  /**
   * 获取访问令牌
   */
  static getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  /**
   * 获取刷新令牌
   */
  static getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  /**
   * 获取令牌过期时间
   */
  static getTokenExpiresAt(): number | null {
    const expiresAt = localStorage.getItem(this.TOKEN_EXPIRES_AT_KEY);
    return expiresAt ? parseInt(expiresAt, 10) : null;
  }

  /**
   * 检查令牌是否过期
   */
  static isTokenExpired(): boolean {
    const expiresAt = this.getTokenExpiresAt();
    if (!expiresAt) return true;
    
    // 提前5分钟认为令牌过期，给刷新留出时间
    return Date.now() > (expiresAt - 5 * 60 * 1000);
  }

  /**
   * 检查是否已登录
   */
  static isLoggedIn(): boolean {
    const accessToken = this.getAccessToken();
    const refreshToken = this.getRefreshToken();
    
    return !!(accessToken && refreshToken && !this.isTokenExpired());
  }

  /**
   * 清除所有令牌
   */
  static clearTokens(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.TOKEN_EXPIRES_AT_KEY);
  }

  /**
   * 获取令牌剩余时间（秒）
   */
  static getTokenRemainingTime(): number {
    const expiresAt = this.getTokenExpiresAt();
    if (!expiresAt) return 0;
    
    const remainingTime = Math.floor((expiresAt - Date.now()) / 1000);
    return Math.max(0, remainingTime);
  }

  /**
   * 解析JWT令牌（仅解析payload，不验证签名）
   */
  static parseJwtPayload(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Failed to parse JWT payload:', error);
      return null;
    }
  }

  /**
   * 获取当前用户信息（从JWT令牌中解析）
   */
  static getCurrentUser(): any {
    const token = this.getAccessToken();
    if (!token) return null;
    
    const payload = this.parseJwtPayload(token);
    if (!payload) return null;
    
    return {
      id: payload.user_id,
      username: payload.username,
      email: payload.email,
      roles: payload.roles || []
    };
  }

  /**
   * 检查用户是否有指定角色
   */
  static hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.roles?.includes(role) || false;
  }

  /**
   * 检查用户是否有管理员权限
   */
  static isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }
}