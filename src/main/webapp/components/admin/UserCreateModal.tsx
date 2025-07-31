// src/main/webapp/components/admin/UserCreateModal.tsx
import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Modal,
  Form,
  Input,
  Select,
  Switch,
  Button,
  message,
  Space,
  Divider
} from 'antd';
import { UserOutlined, MailOutlined, LockOutlined, TeamOutlined } from '@ant-design/icons';
import adminService from '../../services/adminService';
import { UserCreateRequest } from '../../types/admin';

const { Option } = Select;

export interface UserCreateModalProps {
  visible: boolean;
  onClose: () => void;
  onUserCreated: () => void;
}

const UserCreateModal: React.FC<UserCreateModalProps> = ({
  visible,
  onClose,
  onUserCreated
}) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [roles, setRoles] = useState<string[]>([]);

  useEffect(() => {
    if (visible) {
      loadRoles();
      form.resetFields();
    }
  }, [visible, form]);

  const loadRoles = async () => {
    try {
      const availableRoles = await adminService.getRoles();
      setRoles(availableRoles);
    } catch (error) {
      console.error('Failed to load roles:', error);
      message.error(t('error.unknownError', '加载角色失败'));
    }
  };

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      const userData: UserCreateRequest = {
        username: values.username,
        email: values.email,
        password: values.password,
        roles: values.roles || [],
        enabled: values.enabled !== false
      };

      await adminService.createUser(userData);
      message.success(t('admin.users.messages.createSuccess', '用户创建成功'));
      onUserCreated();
      onClose();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || t('error.unknownError', '创建用户失败');
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onClose();
  };

  return (
    <Modal
      title={t('admin.users.create', '创建用户')}
      open={visible}
      onCancel={handleCancel}
      footer={null}
      width={600}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{
          enabled: true,
          roles: []
        }}
      >
        <Form.Item
          name="username"
          label={t('user.username', '用户名')}
          rules={[
            { required: true, message: t('validation.required.username', '请输入用户名') },
            { min: 3, max: 20, message: t('validation.format.usernameLength', '用户名长度必须在3到20个字符之间') },
            { pattern: /^[a-zA-Z0-9_]+$/, message: t('validation.format.usernamePattern', '用户名只能包含字母、数字和下划线') }
          ]}
        >
          <Input
            prefix={<UserOutlined />}
            placeholder={t('user.username', '用户名')}
            maxLength={20}
          />
        </Form.Item>

        <Form.Item
          name="email"
          label={t('user.email', '邮箱')}
          rules={[
            { required: true, message: t('validation.required.email', '请输入邮箱') },
            { type: 'email', message: t('validation.format.email', '请输入有效的邮箱地址') }
          ]}
        >
          <Input
            prefix={<MailOutlined />}
            placeholder={t('user.email', '邮箱')}
            type="email"
          />
        </Form.Item>

        <Form.Item
          name="password"
          label={t('user.password', '密码')}
          rules={[
            { required: true, message: t('validation.required.password', '请输入密码') },
            { min: 6, max: 30, message: t('validation.format.passwordLength', '密码长度必须在6到30个字符之间') }
          ]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder={t('user.password', '密码')}
            maxLength={30}
          />
        </Form.Item>

        <Form.Item
          name="confirmPassword"
          label={t('user.confirmPassword', '确认密码')}
          dependencies={['password']}
          rules={[
            { required: true, message: t('validation.required.confirmPassword', '请确认密码') },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('password') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error(t('validation.format.passwordMismatch', '两次输入的密码不匹配')));
              },
            }),
          ]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder={t('user.confirmPassword', '确认密码')}
            maxLength={30}
          />
        </Form.Item>

        <Divider />

        <Form.Item
          name="roles"
          label={t('user.roles', '角色')}
        >
          <Select
            mode="multiple"
            placeholder={t('user.roles', '选择角色')}
            prefix={<TeamOutlined />}
            loading={roles.length === 0}
            optionFilterProp="children"
          >
            {roles.map(role => (
              <Option key={role} value={role}>
                {role}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          name="enabled"
          label={t('user.enabled', '启用状态')}
          valuePropName="checked"
        >
          <Switch
            checkedChildren={t('user.enabled', '启用')}
            unCheckedChildren={t('user.disabled', '禁用')}
          />
        </Form.Item>

        <Form.Item>
          <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
            <Button onClick={handleCancel}>
              {t('common.cancel', '取消')}
            </Button>
            <Button type="primary" htmlType="submit" loading={loading}>
              {t('common.submit', '创建')}
            </Button>
          </Space>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default UserCreateModal;
