import React, { useState, useEffect } from 'react';
import { Card, Button, List, Typography, message, Form, Checkbox, Space, Alert } from 'antd';
import { useSearchParams } from 'react-router-dom';
import axios from 'axios';

interface ConsentInfo {
  clientName: string;
  scopes: { scope: string; description: string }[];
  state: string;
}

const Consent: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [consentInfo, setConsentInfo] = useState<ConsentInfo | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [searchParams] = useSearchParams();
  const [form] = Form.useForm();

  useEffect(() => {
    const fetchConsentInfo = async () => {
      setLoading(true);
      setError(null);
      const clientId = searchParams.get('client_id');
      const scope = searchParams.get('scope');
      const state = searchParams.get('state');

      if (!clientId || !scope || !state) {
        setError('无效的授权请求，缺少必要的参数。');
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get('/api/oauth2/consent', { params: { clientId, scope, state } });
        setConsentInfo(response.data);
        // 默认选中所有请求的权限
        form.setFieldsValue({
          scopes: response.data.scopes.map((s: any) => s.scope),
        });
      } catch (err: any) {
        if (err.response?.status === 401) {
          // 如果用户未登录，后端会返回401，前端应重定向到登录页
          const currentUrl = window.location.href;
          // 将当前同意页的URL作为参数传递给登录页，以便登录后可以跳回来
          window.location.href = `/login?return_url=${encodeURIComponent(currentUrl)}`;
        } else {
          setError(err.response?.data?.message || '获取授权信息失败。');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchConsentInfo();
  }, [searchParams, form]);

  const onFinish = async (values: { scopes: string[] }) => {
    setSubmitting(true);
    try {
      const decision = {
        clientId: searchParams.get('client_id'),
        state: searchParams.get('state'),
        scopes: values.scopes,
        approved: true, // 提交即代表同意
      };
      const response = await axios.post('/api/oauth2/consent', decision);
      // 授权成功后，后端会返回一个重定向URL
      window.location.href = response.data.redirect_uri;
    } catch (err: any) {
      message.error(err.response?.data?.message || '授权处理失败');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeny = async () => {
    // 用户拒绝授权，也需要通知后端
    const decision = {
      clientId: searchParams.get('client_id'),
      state: searchParams.get('state'),
      scopes: [],
      approved: false,
    };
    const response = await axios.post('/api/oauth2/consent', decision);
    window.location.href = response.data.redirect_uri;
  };

  if (loading) {
    return <Card title="加载中..." style={{ maxWidth: 500, margin: '40px auto' }} loading />;
  }

  if (error) {
    return <Alert message={error} type="error" showIcon style={{ maxWidth: 500, margin: '40px auto' }} />;
  }

  return (
    <Card title="授权同意" style={{ maxWidth: 500, margin: '40px auto' }}>
      <Typography.Paragraph>
        应用 <b>{consentInfo?.clientName}</b> 请求以下权限：
      </Typography.Paragraph>
      <Form form={form} onFinish={onFinish}>
        <Form.Item name="scopes">
          <Checkbox.Group>
            <Space direction="vertical">
              {consentInfo?.scopes.map(s => (
                <Checkbox key={s.scope} value={s.scope}>
                  {s.scope} - {s.description}
                </Checkbox>
              ))}
            </Space>
          </Checkbox.Group>
        </Form.Item>
        <Form.Item>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
            <Button onClick={handleDeny} disabled={submitting}>
              拒绝
            </Button>
            <Button type="primary" htmlType="submit" loading={submitting}>
              同意授权
            </Button>
          </div>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default Consent;