import React from 'react';
import { Menu } from 'antd';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { HomeOutlined, UserOutlined, AppstoreOutlined, SettingOutlined } from '@ant-design/icons';
import { useAuth } from '../hooks/useAuth';

const DashboardSidebar: React.FC = () => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const location = useLocation();

  const isAdmin = user?.roles.includes('ADMIN');

  const menuItems = [
    {
      key: '/dashboard',
      icon: <HomeOutlined />,
      label: <Link to="/dashboard">{t('dashboard.menu.home')}</Link>,
    },
    isAdmin && {
      key: '/admin/users',
      icon: <UserOutlined />,
      label: <Link to="/admin/users">{t('dashboard.menu.userManagement')}</Link>,
    },
    // Add other menu items here as needed
    {
      key: '/dashboard/clients',
      icon: <AppstoreOutlined />,
      label: <Link to="/dashboard/clients">{t('dashboard.menu.clientManagement')}</Link>,
    },
    {
      key: 'system',
      icon: <SettingOutlined />,
      label: t('dashboard.menu.system'),
      children: [
        {
          key: '/dashboard/settings',
          label: <Link to="/dashboard/settings">{t('dashboard.menu.settings')}</Link>,
        },
        {
          key: '/dashboard/audit',
          label: <Link to="/dashboard/audit">{t('dashboard.menu.auditLog')}</Link>,
        },
      ],
    },
  ].filter(Boolean); // Filter out false values (like the admin link if not an admin)

  // Determine selected keys based on current location
  const selectedKeys = [location.pathname];

  return (
    <Menu
      theme="dark"
      mode="inline"
      selectedKeys={selectedKeys}
      items={menuItems as any} // Type assertion to handle filtered items
    />
  );
};

export default DashboardSidebar;
