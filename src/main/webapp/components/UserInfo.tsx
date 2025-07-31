import React, { useState, useEffect } from 'react';
import { Dropdown, Avatar, Space, Button, message } from 'antd';
import { UserOutlined, LogoutOutlined, SettingOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import { TokenManager } from '../utils/tokenManager';
import authService from '../services/authService';

interface UserInfo {
  id: number;
  username: string;
  email: string;
  status: string;
  roles: string[];
  locale: string;
}

/**
 * 用户信息组件
 * 显示当前登录用户的信息和操作菜单
 */
const UserInfoComponent: React.FC = () => {
  const { t } = useTranslation();
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadUserInfo();
  }, []);

  const loadUserInfo = () => {
    try {
      // 从localStorage获取用户信息
      const storedUserInfo = localStorage.getItem('user_info');
      if (storedUserInfo) {
        setUserInfo(JSON.parse(storedUserInfo));
      } else {
        // 如果localStorage中没有，尝试从JWT令牌解析
        const tokenUser = TokenManager.getCurrentUser();
        if (tokenUser) {
          setUserInfo(tokenUser);
        }
      }
    } catch (error) {
      console.error('Failed to load user info:', error);
    }
  };

  const handleLogout = async () => {
    setLoading(true);
    try {
      // 调用登出接口
      await authService.logout();
      
      // 清除本地存储
      TokenManager.clearTokens();
      localStorage.removeItem('user_info');
      
      message.success(t('auth.logout.success'));
      
      // 跳转到登录页
      window.location.href = '/login';
    } catch (error) {
      console.error('Logout failed:', error);
      message.error(t('auth.logout.failed'));
      
      // 即使登出接口失败，也清除本地数据
      TokenManager.clearTokens();
      localStorage.removeItem('user_info');
      window.location.href = '/login';
    } finally {
      setLoading(false);
    }
  };

  const handleSettings = () => {
    // TODO: 实现用户设置功能
    message.info(t('common.comingSoon'));
  };

  if (!userInfo) {
    return null;
  }

  const menuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: (
        <div>
          <div style={{ fontWeight: 'bold' }}>{userInfo.username}</div>
          <div style={{ fontSize: '12px', color: '#666' }}>{userInfo.email}</div>
          <div style={{ fontSize: '12px', color: '#999' }}>
            {t('user.roles')}: {userInfo.roles.join(', ')}
          </div>
        </div>
      ),
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: t('user.settings'),
      onClick: handleSettings,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: t('auth.logout.button'),
      onClick: handleLogout,
    },
  ];

  return (
    <Dropdown
      menu={{ items: menuItems }}
      placement="bottomRight"
      trigger={['click']}
    >
      <Button type="text" loading={loading} style={{ color: '#fff' }}>
        <Space>
          <Avatar size="small" icon={<UserOutlined />} />
          {userInfo.username}
        </Space>
      </Button>
    </Dropdown>
  );
};

export default UserInfoComponent;