import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout, Space } from 'antd';
import { useTranslation } from 'react-i18next';
import Login from './pages/Login';
import Register from './pages/Register';
import Activate from './pages/Activate';
import Consent from './pages/Consent';
import Dashboard from './pages/Dashboard';
import UserManagement from './pages/admin/UserManagement';
import ProtectedRoute from './components/ProtectedRoute';
import UserInfo from './components/UserInfo';
import LanguageSwitcher from './components/LanguageSwitcher';
import { AuthProvider, useAuth } from './hooks/useAuth'; // 引入 AuthProvider 和 useAuth
import './i18n'; // 初始化国际化

const { Header, Content, Footer } = Layout;

const AppContent: React.FC = () => {
  const { t } = useTranslation();
  const { isLoggedIn } = useAuth(); // 使用 hook 获取动态的登录状态

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
        color: '#fff', 
        fontSize: 20 
      }}>
        <div>{t('app.title')}</div>
        <Space>
          <LanguageSwitcher />
          {isLoggedIn && <UserInfo />}
        </Space>
      </Header>
      <Content>
        <Routes>
          <Route 
            path="/login" 
            element={
              <ProtectedRoute requireAuth={false}>
                <Login />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/register" 
            element={
              <ProtectedRoute requireAuth={false}>
                <Register />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/activate" 
            element={
              <ProtectedRoute requireAuth={false}>
                <Activate />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/consent" 
            element={
              <ProtectedRoute>
                <Consent />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/dashboard/*" 
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/admin/users"
            element={
              <ProtectedRoute requiredRoles={['ADMIN']}>
                <UserManagement />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/dashboard" />} />
        </Routes>
      </Content>
      <Footer style={{ textAlign: 'center' }}>
        {t('app.footer')}
      </Footer>
    </Layout>
  );
};

const App: React.FC = () => (
  <BrowserRouter>
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  </BrowserRouter>
);

export default App;
 