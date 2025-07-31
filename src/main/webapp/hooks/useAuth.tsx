import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import { TokenManager } from '../utils/tokenManager';
import { UserInfo as User } from '../types/auth'; // Assuming UserInfo is the user type
import authService from '../services/authService';

interface AuthContextType {
  isLoggedIn: boolean;
  user: User | null;
  isLoading: boolean;
  login: (accessToken: string, refreshToken: string, expiresIn: number) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const fetchUserInfo = useCallback(() => {
    const userInfo = TokenManager.getCurrentUser();
    if (userInfo) {
      setUser(userInfo);
      setIsLoggedIn(true);
    } else {
      setUser(null);
      setIsLoggedIn(false);
    }
    setIsLoading(false);
  }, []);

  useEffect(() => {
    fetchUserInfo();
  }, [fetchUserInfo]);

  const login = (accessToken: string, refreshToken: string, expiresIn: number) => {
    TokenManager.saveTokens(accessToken, refreshToken, expiresIn);
    fetchUserInfo(); // Update state after login
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error("Logout failed on server, clearing tokens locally.", error);
    } finally {
      TokenManager.clearTokens();
      setUser(null);
      setIsLoggedIn(false);
    }
  };

  const value = {
    isLoggedIn,
    user,
    isLoading,
    login,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
