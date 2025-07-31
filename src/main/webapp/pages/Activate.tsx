import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Result, Spin, Button, Layout } from 'antd';
import authService from '../services/authService';

const { Content } = Layout;

type ActivationStatus = 'activating' | 'success' | 'failed';

const Activate: React.FC = () => {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState<ActivationStatus>('activating');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = searchParams.get('token');

    if (!token) {
      setStatus('failed');
      setError(t('error.invalidToken', 'Invalid activation link: token is missing.'));
      return;
    }

    const activateAccount = async () => {
      try {
        const response = await authService.activate(token);
        setStatus('success');
      } catch (err: any) {
        setStatus('failed');
        const errorMessage = err.response?.data?.message || err.message || t('error.unknownError');
        setError(errorMessage);
      }
    };

    activateAccount();
  }, [searchParams, t]);

  const renderContent = () => {
    switch (status) {
      case 'activating':
        return (
          <div style={{ textAlign: 'center', padding: '100px' }}>
            <Spin size="large" tip={t('auth.activate.activating', 'Activating your account...')} />
          </div>
        );
      case 'success':
        return (
          <Result
            status="success"
            title={t('auth.activate.success', 'Account Activation Successful!')}
            subTitle={t('auth.activate.successSubtitle', 'Your account is now active. You can proceed to login.')}
            extra={[
              <Link to="/login" key="login">
                <Button type="primary">{t('auth.activate.loginButton', 'Login Now')}</Button>
              </Link>,
            ]}
          />
        );
      case 'failed':
        return (
          <Result
            status="error"
            title={t('auth.activate.failed', 'Account Activation Failed')}
            subTitle={error || t('auth.activate.failedSubtitle', 'The activation link may be invalid or expired.')}
            extra={[
              <Link to="/register" key="register">
                <Button>{t('auth.activate.registerButton', 'Register Again')}</Button>
              </Link>,
              <Link to="/login" key="login">
                <Button type="default">{t('auth.activate.backToLogin', 'Back to Login')}</Button>
              </Link>,
            ]}
          />
        );
      default:
        return null;
    }
  };

  return (
    <Layout style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Content>
        {renderContent()}
      </Content>
    </Layout>
  );
};

export default Activate;
