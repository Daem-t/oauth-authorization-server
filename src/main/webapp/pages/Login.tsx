import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Card, message, Row, Col, Alert } from 'antd';
import { Link, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { LoginRequest, LoginResponse, CaptchaResponse } from '../types/auth';
import authService from '../services/authService';
import LanguageSwitcher from '../components/LanguageSwitcher';
import { ErrorHandler } from '../utils/errorHandler';
import { TokenManager } from '../utils/tokenManager';

interface LoginFormValues {
  username: string;
  password: string;
  captcha: string;
}

const Login: React.FC = () => {
  const { t } = useTranslation();
  const [loading, setLoading] = useState<boolean>(false);
  const [captcha, setCaptcha] = useState<CaptchaResponse>({ captchaId: '', image: '' });
  const [error, setError] = useState<string | null>(null);
  const [searchParams] = useSearchParams();
  const [form] = Form.useForm<LoginFormValues>();

  const fetchCaptcha = async (): Promise<void> => {
    try {
      const captchaData = await authService.getCaptcha();
      setCaptcha(captchaData);
    } catch (err) {
      message.error(t('error.captchaLoadFailed'));
    }
  };

  useEffect(() => {
    fetchCaptcha();
  }, []);

  const onFinish = async (values: LoginFormValues): Promise<void> => {
    setLoading(true);
    setError(null);
    try {
      const loginData: LoginRequest = {
        username: values.username,
        password: values.password,
        captchaId: captcha.captchaId,
        captchaCode: values.captcha,
      };

      // 1. 调用登录接口
      const response: LoginResponse = await authService.login(loginData);

      // 2. 从响应中获取 tokens
      const { access_token, refresh_token, message: responseMessage } = response;

      if (access_token && refresh_token) {
        // 3. 使用TokenManager保存令牌
        TokenManager.saveTokens(access_token, refresh_token, response.expires_in);
        
        // 4. 保存用户信息
        if (response.user_info) {
          localStorage.setItem('user_info', JSON.stringify(response.user_info));
        }

        message.success(responseMessage || t('auth.login.success'));

        // 5. 重定向到之前的页面或主页
        const returnUrl = searchParams.get('return_url');
        window.location.href = returnUrl || '/dashboard';
      } else {
        throw new Error(t('auth.login.tokenMissing'));
      }
    } catch (err: any) {
      const errorMessage = ErrorHandler.extractErrorMessage(err, t, 'auth.login.failed');
      setError(errorMessage);
      fetchCaptcha(); // 登录失败后刷新验证码
      form.resetFields(['captcha']);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card
      title={t('auth.login.title')}
      style={{ maxWidth: 450, margin: '40px auto' }}
      extra={<LanguageSwitcher />}
    >
      {error && (
        <Alert
          message={error}
          type="error"
          showIcon
          closable
          onClose={() => setError(null)}
          style={{ marginBottom: 24 }}
        />
      )}
      <Form form={form} name="login" onFinish={onFinish} autoComplete="off">
        <Form.Item
          name="username"
          rules={[{ required: true, message: t('validation.required.username') }]}
        >
          <Input placeholder={t('auth.login.username')} />
        </Form.Item>
        <Form.Item
          name="password"
          rules={[{ required: true, message: t('validation.required.password') }]}
        >
          <Input.Password placeholder={t('auth.login.password')} />
        </Form.Item>
        <Form.Item>
          <Row gutter={8}>
            <Col span={16}>
              <Form.Item
                name="captcha"
                noStyle
                rules={[{ required: true, message: t('validation.required.captcha') }]}
              >
                <Input placeholder={t('auth.login.captcha')} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <img
                src={captcha.image}
                alt="Captcha"
                onClick={fetchCaptcha}
                style={{ cursor: 'pointer', height: 32, width: '100%', display: 'block' }}
              />
            </Col>
          </Row>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} block>
            {t('auth.login.submitButton')}
          </Button>
        </Form.Item>
        <Form.Item>
          <Link to="/register">{t('auth.login.registerLink')}</Link>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default Login;
