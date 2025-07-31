import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Form, Input, Button, Typography, Alert, message, Layout } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';
import authService from '../services/authService';

const { Title } = Typography;
const { Content } = Layout;

const Register: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onFinish = async (values: any) => {
    setLoading(true);
    setError(null);
    try {
      const response = await authService.register({
        username: values.username,
        email: values.email,
        password: values.password,
      });
      message.success(t('auth.register.success', 'Registration successful! Please check your email to activate your account.'));
      navigate('/login');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || t('error.unknownError');
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout style={{ minHeight: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <Content style={{ maxWidth: '400px', width: '100%', padding: '20px' }}>
        <Title level={2} style={{ textAlign: 'center' }}>{t('auth.register.title', 'Create Account')}</Title>
        {error && <Alert message={error} type="error" showIcon closable style={{ marginBottom: '20px' }} />}
        <Form
          form={form}
          name="register"
          onFinish={onFinish}
          scrollToFirstError
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: t('validation.required.username', 'Please input your Username!') },
              { min: 4, message: t('validation.format.usernameLength', 'Username must be at least 4 characters.') }
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder={t('auth.register.username', 'Username')} />
          </Form.Item>

          <Form.Item
            name="email"
            rules={[
              { type: 'email', message: t('validation.format.email', 'The input is not valid E-mail!') },
              { required: true, message: t('validation.required.email', 'Please input your E-mail!') },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder={t('auth.register.email', 'Email')} />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: t('validation.required.password', 'Please input your password!') },
              { min: 6, message: t('validation.format.passwordLength', 'Password must be at least 6 characters.') }
            ]}
            hasFeedback
          >
            <Input.Password prefix={<LockOutlined />} placeholder={t('auth.register.password', 'Password')} />
          </Form.Item>

          <Form.Item
            name="confirm"
            dependencies={['password']}
            hasFeedback
            rules={[
              { required: true, message: t('validation.required.confirmPassword', 'Please confirm your password!') },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error(t('validation.format.passwordMismatch', 'The two passwords that you entered do not match!')));
                },
              }),
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder={t('auth.register.confirmPassword', 'Confirm Password')} />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} style={{ width: '100%' }}>
              {t('auth.register.submitButton', 'Register')}
            </Button>
          </Form.Item>
        </Form>
        <div style={{ textAlign: 'center' }}>
          <Link to="/login">{t('auth.register.loginLink', 'Already have an account? Sign in')}</Link>
        </div>
      </Content>
    </Layout>
  );
};

export default Register;
