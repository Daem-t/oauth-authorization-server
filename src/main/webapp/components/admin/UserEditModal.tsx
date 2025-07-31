// src/main/webapp/components/admin/UserEditModal.tsx
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
  Divider,
  Alert
} from 'antd';
import { UserOutlined, MailOutlined, TeamOutlined, LockOutlined } from '@ant-design/icons';
import adminService from '../../services/adminService';
import { UserInfo, UserUpdateRequest } from '../../types/admin';

const { Option } = Select;

export interface UserEditModalProps {
  visible: boolean;
  user: UserInfo | null;
  onClose: () => void;
  onUserUpdated: () => void;
}

const UserEditModal: React.FC<UserEditModalProps> = ({
  visible,
  user,
  onClose,
  onUserUpdated
}) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [roles, setRoles] = useState<string[]>([]);
  const [showPasswordFields, setShowPasswordFields] = useState(false);

  useEffect(() => {
    if (visible && user) {
      loadRoles();
      form.setFieldsValue({
        username: user.username,
        email: user.email,
        roles: user.roles,
        enabled: user.enabled,
        password: '',
        confirmPassword: ''
      });
      setShowPasswordFields(false);
    }
  }, [visible, user, form]);

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
    if (!user) return;

    setLoading(true);
    try {
      const updateData: Partial<UserUpdateRequest> = {
        id: user.id,
        username: values.username,
        email: values.email,
        roles: values.roles || [],
        enabled: values.enabled !== false
      };

      // 只有在用户输入密码时才包含密码字段
      if (showPasswordFields && values.password) {
        updateData.password = values.password;
      }

      await adminService.updateUser(user.id, updateData);
      message.success(t('admin.users.messages.updateSuccess', '用户信息更新成功'));
      onUserUpdated();
      onClose();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || t('error.unknownError', '更新用户失败');
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    setShowPasswordFields(false);
    onClose();
  };

  const handleResetPassword = async () => {
    if (!user) return;

    Modal.confirm({
      title: t('admin.users.actions.resetPassword', '重置密码'),
      content: t('admin.users.messages.resetPasswordConfirm', '确定要重置用户 {0} 的密码吗？').replace('{0}', user.username),
      onOk: async () => {
        try {
          await adminService.resetUserPassword(user.id, { reason: 'Reset by admin' });
          message.success(t('admin.users.messages.passwordResetSuccess', '密码重置成功'));
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || t('error.unknownError', '密码重置失败');
          message.error(errorMessage);
        }
      },
    });
  };

  if (!user) return null;

  return (
    <Modal
      title={t('admin.users.edit', '编辑用户')}
      open={visible}
      onCancel={handleCancel}
      footer={null}
      width={600}
      destroyOnClose
    >
      <Alert
        message={t('admin.users.edit.info', '编辑用户信息')}
        description={t('admin.users.edit.description', '您可以修改用户的基本信息和角色分配。密码字段为可选，留空则不修改密码。')}
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
      />

      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{
          enabled: user.enabled,
          roles: user.roles
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

        <Divider />

        <Form.Item>
          <Space direction="vertical" style={{ width: '100%' }}>
            <Button 
              type="dashed" 
              onClick={() => setShowPasswordFields(!showPasswordFields)}
              icon={<LockOutlined />}
            >
              {showPasswordFields 
                ? t('admin.users.edit.hidePassword', '隐藏密码字段')
                : t('admin.users.edit.showPassword', '修改密码')
              }
            </Button>
            
            {showPasswordFields && (
              <>
                <Form.Item
                  name="password"
                  label={t('user.password', '新密码')}
                  rules={[
                    { min: 6, max: 30, message: t('validation.format.passwordLength', '密码长度必须在6到30个字符之间') }
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder={t('user.password', '新密码')}
                    maxLength={30}
                  />
                </Form.Item>

                <Form.Item
                  name="confirmPassword"
                  label={t('user.confirmPassword', '确认新密码')}
                  dependencies={['password']}
                  rules={[
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
                    placeholder={t('user.confirmPassword', '确认新密码')}
                    maxLength={30}
                  />
                </Form.Item>
              </>
            )}
          </Space>
        </Form.Item>

        <Form.Item>
          <Space style={{ width: '100%', justifyContent: 'space-between' }}>
            <Button onClick={handleResetPassword} danger>
              {t('admin.users.actions.resetPassword', '重置密码')}
            </Button>
            <Space>
              <Button onClick={handleCancel}>
                {t('common.cancel', '取消')}
              </Button>
              <Button type="primary" htmlType="submit" loading={loading}>
                {t('common.save', '保存')}
              </Button>
            </Space>
          </Space>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default UserEditModal;
