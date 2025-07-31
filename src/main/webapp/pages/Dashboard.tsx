import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Layout, Card, Typography } from 'antd';
import { useTranslation } from 'react-i18next';
import DashboardSidebar from '../components/DashboardSidebar';


const { Sider, Content } = Layout;
const { Title, Paragraph } = Typography;

// A placeholder component for the dashboard home
const DashboardHome: React.FC = () => {
  const { t } = useTranslation();
  return (
    <Card>
      <Title level={2}>{t('dashboard.title')}</Title>
      <Paragraph>{t('dashboard.welcome')}</Paragraph>
      <Paragraph>
        Please select a menu item from the sidebar to start managing the system.
      </Paragraph>
    </Card>
  );
};

// A placeholder for coming soon features
const ComingSoon: React.FC = () => {
  const { t } = useTranslation();
  return (
    <Card>
      <Title level={3}>{t('common.comingSoon')}</Title>
      <Paragraph>This feature is under development.</Paragraph>
    </Card>
  );
};

const Dashboard: React.FC = () => {
  return (
    <Layout style={{ minHeight: 'calc(100vh - 64px - 64px)' }}>
      <Sider
        width={220}
        theme="dark"
        breakpoint="lg"
        collapsedWidth="0"
      >
        <div style={{ height: '32px', margin: '16px', background: 'rgba(255, 255, 255, 0.2)', borderRadius: '6px' }} />
        <DashboardSidebar />
      </Sider>
      <Layout style={{ padding: '24px' }}>
        <Content
          style={{
            padding: 24,
            margin: 0,
            background: '#fff',
            borderRadius: '8px'
          }}
        >
          {/* Nested Routes for Dashboard Content */}
          <Routes>
            <Route index element={<DashboardHome />} />
            {/* The /admin/users route is handled at the App level, so we don't need it here */}
            <Route path="clients" element={<ComingSoon />} />
            <Route path="settings" element={<ComingSoon />} />
            <Route path="audit" element={<ComingSoon />} />
          </Routes>
        </Content>
      </Layout>
    </Layout>
  );
};

export default Dashboard;
 