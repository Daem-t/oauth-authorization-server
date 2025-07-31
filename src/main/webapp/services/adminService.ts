// src/main/webapp/services/adminService.ts

import axios from '../utils/axiosConfig';
import { 
  UserListRequest, 
  UserListResponse, 
  UserInfo, 
  UserCreateRequest, 
  UserUpdateRequest,
  ApiResponse 
} from '../types/admin';

class AdminService {
  private static instance: AdminService;

  private constructor() {}

  public static getInstance(): AdminService {
    if (!AdminService.instance) {
      AdminService.instance = new AdminService();
    }
    return AdminService.instance;
  }

  /**
   * 获取用户列表
   */
  async getUsers(params: UserListRequest): Promise<UserListResponse> {
    const response = await axios.get<UserListResponse>('/api/admin/users', { params });
    return response.data;
  }

  /**
   * 创建新用户
   */
  async createUser(data: UserCreateRequest): Promise<UserInfo> {
    const response = await axios.post<UserInfo>('/api/admin/users', data);
    return response.data;
  }

  /**
   * 更新用户信息
   */
  async updateUser(userId: number, data: Partial<UserUpdateRequest>): Promise<UserInfo> {
    const response = await axios.put<UserInfo>(`/api/admin/users/${userId}`, data);
    return response.data;
  }

  /**
   * 删除用户
   */
  async deleteUser(userId: number): Promise<ApiResponse> {
    const response = await axios.delete<ApiResponse>(`/api/admin/users/${userId}`);
    return response.data;
  }

  /**
   * 更新用户状态
   */
  async updateUserStatus(userId: number, enabled: boolean): Promise<ApiResponse> {
    const response = await axios.put<ApiResponse>(`/api/admin/users/${userId}/status`, {
      status: enabled ? 'ACTIVE' : 'INACTIVE',
      reason: enabled ? 'Activated by admin' : 'Deactivated by admin'
    });
    return response.data;
  }

  /**
   * 获取所有角色
   */
  async getRoles(): Promise<string[]> {
    // 这里暂时返回硬编码的角色，后续可以从后端API获取
    return ['ADMIN', 'USER', 'MODERATOR'];
  }

  /**
   * 重置用户密码
   */
  async resetUserPassword(userId: number, data: { reason: string }): Promise<ApiResponse> {
    const response = await axios.post<ApiResponse>(`/api/admin/users/${userId}/reset-password`, data);
    return response.data;
  }
}

export default AdminService.getInstance();
