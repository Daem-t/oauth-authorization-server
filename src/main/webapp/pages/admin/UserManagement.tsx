import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { 
  Table, 
  Input, 
  Button, 
  Space, 
  Pagination, 
  Spin, 
  Alert, 
  Typography, 
  Layout,
  Modal,
  message,
  Switch,
  Tag,
  Tooltip,
  Popconfirm,
  Card,
  Row,
  Col,
  Statistic
} from 'antd';
import { 
  PlusOutlined, 
  SearchOutlined, 
  EditOutlined, 
  DeleteOutlined,
  UserOutlined,
  MailOutlined,
  CalendarOutlined,
  LoginOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';

import { AppDispatch, RootState } from '../../store';
import { fetchUsers, setCurrentPage, setKeyword } from '../../store/slices/userManagementSlice';
import { UserInfo } from '../../types/admin';
import adminService from '../../services/adminService';
import UserCreateModal from '../../components/admin/UserCreateModal';
import UserEditModal from '../../components/admin/UserEditModal';

const { Content } = Layout;
const { Title } = Typography;
const { Search } = Input;

const UserManagement: React.FC = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch<AppDispatch>();

  const {
    users,
    loading,
    error,
    currentPage,
    totalPages,
    totalElements,
    pageSize,
    keyword,
  } = useSelector((state: RootState) => state.userManagement);

  const [searchTerm, setSearchTerm] = useState(keyword);
  const [isCreateModalOpen, setCreateModalOpen] = useState(false);
  const [isEditModalOpen, setEditModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<UserInfo | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);

  useEffect(() => {
    dispatch(fetchUsers({ page: currentPage, size: pageSize, keyword }));
  }, [dispatch, currentPage, pageSize, keyword]);

  const handleSearch = (value: string) => {
    dispatch(setKeyword(value));
  };

  const handlePageChange = (page: number, size?: number) => {
    dispatch(setCurrentPage(page));
  };

  const handleCreateUser = () => {
    setCreateModalOpen(true);
  };

  const handleEditUser = (user: UserInfo) => {
    setSelectedUser(user);
    setEditModalOpen(true);
  };

  const handleDeleteUser = async (userId: number) => {
    try {
      await adminService.deleteUser(userId);
      message.success(t('admin.users.messages.deleteSuccess', '用户删除成功'));
      dispatch(fetchUsers({ page: currentPage, size: pageSize, keyword }));
    } catch (error: any) {
      message.error(error.response?.data?.message || t('error.unknownError', '删除失败'));
    }
  };

  const handleStatusChange = async (userId: number, enabled: boolean) => {
    try {
      await adminService.updateUserStatus(userId, enabled);
      message.success(t('admin.users.messages.statusUpdateSuccess', '用户状态更新成功'));
      dispatch(fetchUsers({ page: currentPage, size: pageSize, keyword }));
    } catch (error: any) {
      message.error(error.response?.data?.message || t('error.unknownError', '状态更新失败'));
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning(t('admin.users.messages.noSelection', '请选择要删除的用户'));
      return;
    }

    Modal.confirm({
      title: t('admin.users.messages.batchDeleteConfirm', '确认删除'),
      content: t('admin.users.messages.batchDeleteConfirm', '确定要删除选中的 {0} 个用户吗？').replace('{0}', selectedRowKeys.length.toString()),
      onOk: async () => {
        try {
          // 这里可以实现批量删除的API调用
          message.success(t('admin.users.messages.batchDeleteSuccess', '批量删除成功'));
          setSelectedRowKeys([]);
          dispatch(fetchUsers({ page: currentPage, size: pageSize, keyword }));
        } catch (error: any) {
          message.error(error.response?.data?.message || t('error.unknownError', '批量删除失败'));
        }
      },
    });
  };

  const handleUserCreated = () => {
    setCreateModalOpen(false);
    dispatch(fetchUsers({ page: currentPage, size: pageSize, keyword }));
  };

  const handleUserUpdated = () => {
    setEditModalOpen(false);
    setSelectedUser(null);
    dispatch(fetchUsers({ page: currentPage, size: pageSize, keyword }));
  };

  const columns: ColumnsType<UserInfo> = [
    {
      title: t('admin.users.fields.id', 'ID'),
      dataIndex: 'id',
      key: 'id',
      width: 80,
      sorter: (a, b) => a.id - b.id,
    },
    {
      title: t('admin.users.fields.username', '用户名'),
      dataIndex: 'username',
      key: 'username',
      width: 150,
      sorter: (a, b) => a.username.localeCompare(b.username),
      render: (text: string) => (
        <Space>
          <UserOutlined />
          {text}
        </Space>
      ),
    },
    {
      title: t('admin.users.fields.email', '邮箱'),
      dataIndex: 'email',
      key: 'email',
      width: 200,
      render: (text: string) => (
        <Space>
          <MailOutlined />
          {text}
        </Space>
      ),
    },
    {
      title: t('admin.users.fields.status', '状态'),
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => {
        const color = status === 'ACTIVE' ? 'green' : status === 'INACTIVE' ? 'orange' : 'red';
        return <Tag color={color}>{t(`admin.users.status.${status.toLowerCase()}`, status)}</Tag>;
      },
    },
    {
      title: t('admin.users.fields.enabled', '启用状态'),
      dataIndex: 'enabled',
      key: 'enabled',
      width: 120,
      render: (enabled: boolean, record: UserInfo) => (
        <Switch
          checked={enabled}
          onChange={(checked) => handleStatusChange(record.id, checked)}
          checkedChildren="启用"
          unCheckedChildren="禁用"
        />
      ),
    },
    {
      title: t('admin.users.fields.roles', '角色'),
      dataIndex: 'roles',
      key: 'roles',
      width: 200,
      render: (roles: string[]) => (
        <Space wrap>
          {roles.map(role => (
            <Tag key={role} color="blue">{role}</Tag>
          ))}
        </Space>
      ),
    },
    {
      title: t('admin.users.fields.createdAt', '创建时间'),
      dataIndex: 'created_at',
      key: 'created_at',
      width: 150,
      render: (date: string) => (
        <Space>
          <CalendarOutlined />
          {new Date(date).toLocaleDateString()}
        </Space>
      ),
    },
    {
      title: t('admin.users.fields.lastLoginAt', '最后登录'),
      dataIndex: 'last_login_at',
      key: 'last_login_at',
      width: 150,
      render: (date: string) => (
        <Space>
          <LoginOutlined />
          {date ? new Date(date).toLocaleDateString() : '-'}
        </Space>
      ),
    },
    {
      title: t('actions.title', '操作'),
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Tooltip title={t('actions.edit', '编辑')}>
            <Button
              type="text"
              icon={<EditOutlined />}
              onClick={() => handleEditUser(record)}
              size="small"
            />
          </Tooltip>
          <Popconfirm
            title={t('admin.users.messages.deleteConfirm', '确认删除')}
            description={t('admin.users.messages.deleteConfirm', '确定要删除用户 {0} 吗？').replace('{0}', record.username)}
            onConfirm={() => handleDeleteUser(record.id)}
            okText={t('common.confirm', '确认')}
            cancelText={t('common.cancel', '取消')}
          >
            <Tooltip title={t('actions.delete', '删除')}>
              <Button
                type="text"
                danger
                icon={<DeleteOutlined />}
                size="small"
              />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: React.Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
  };

  return (
    <Content style={{ padding: '24px' }}>
      <Title level={2}>{t('admin.userManagement.title', '用户管理')}</Title>
      
      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title={t('admin.users.stats.total', '总用户数')}
              value={totalElements}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title={t('admin.users.stats.active', '活跃用户')}
              value={users.filter(u => u.status === 'ACTIVE').length}
              valueStyle={{ color: '#3f8600' }}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title={t('admin.users.stats.inactive', '未激活用户')}
              value={users.filter(u => u.status === 'INACTIVE').length}
              valueStyle={{ color: '#cf1322' }}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title={t('admin.users.stats.selected', '已选择')}
              value={selectedRowKeys.length}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        <Space direction="vertical" style={{ width: '100%' }} size="large">
          {/* 搜索和操作栏 */}
          <Row justify="space-between" align="middle">
            <Col>
              <Search
                placeholder={t('admin.userManagement.searchPlaceholder', '按用户名或邮箱搜索...')}
                enterButton={
                  <Button type="primary" icon={<SearchOutlined />}>
                    {t('admin.userManagement.searchButton', '搜索')}
                  </Button>
                }
                onSearch={handleSearch}
                onChange={(e) => setSearchTerm(e.target.value)}
                value={searchTerm}
                style={{ width: 400 }}
                loading={loading === 'pending'}
              />
            </Col>
            <Col>
              <Space>
                {selectedRowKeys.length > 0 && (
                  <Popconfirm
                    title={t('admin.users.messages.batchDeleteConfirm', '确认删除')}
                    description={t('admin.users.messages.batchDeleteConfirm', '确定要删除选中的 {0} 个用户吗？').replace('{0}', selectedRowKeys.length.toString())}
                    onConfirm={handleBatchDelete}
                    okText={t('common.confirm', '确认')}
                    cancelText={t('common.cancel', '取消')}
                  >
                    <Button danger icon={<DeleteOutlined />}>
                      {t('admin.users.actions.batchDelete', '批量删除')} ({selectedRowKeys.length})
                    </Button>
                  </Popconfirm>
                )}
                <Button 
                  type="primary" 
                  icon={<PlusOutlined />}
                  onClick={handleCreateUser}
                >
                  {t('admin.userManagement.createUser', '创建用户')}
                </Button>
              </Space>
            </Col>
          </Row>

          {/* 用户表格 */}
          {loading === 'pending' && users.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '50px' }}>
              <Spin size="large" />
            </div>
          ) : error ? (
            <Alert 
              message={t('error.title', '错误')} 
              description={error} 
              type="error" 
              showIcon 
              closable
            />
          ) : (
            <Table
              columns={columns}
              dataSource={users}
              rowKey="id"
              pagination={false}
              loading={loading === 'pending'}
              rowSelection={rowSelection}
              scroll={{ x: 1200 }}
              size="middle"
            />
          )}

          {/* 分页 */}
          <Row justify="end">
            <Col>
              <Pagination
                current={currentPage}
                total={totalElements}
                pageSize={pageSize}
                onChange={handlePageChange}
                showSizeChanger
                showQuickJumper
                showTotal={(total, range) => 
                  t('pagination.showTotal', '第 {0}-{1} 条，共 {2} 条')
                    .replace('{0}', range[0].toString())
                    .replace('{1}', range[1].toString())
                    .replace('{2}', total.toString())
                }
                disabled={loading === 'pending'}
              />
            </Col>
          </Row>
        </Space>
      </Card>

      {/* 创建用户模态框 */}
      <UserCreateModal
        visible={isCreateModalOpen}
        onClose={() => setCreateModalOpen(false)}
        onUserCreated={handleUserCreated}
      />

      {/* 编辑用户模态框 */}
      <UserEditModal
        visible={isEditModalOpen}
        user={selectedUser}
        onClose={() => {
          setEditModalOpen(false);
          setSelectedUser(null);
        }}
        onUserUpdated={handleUserUpdated}
      />
    </Content>
  );
};

export default UserManagement;
