// src/main/webapp/types/admin.ts

export interface UserInfo {
  id: number;
  username: string;
  email: string;
  status: string;
  enabled: boolean;
  roles: string[];
  created_at: string;
  updated_at: string;
  last_login_at?: string;
  login_count?: number;
}

export interface UserListRequest {
  page?: number;
  size?: number;
  keyword?: string;
  status?: string;
  role?: string;
  sortBy?: string;
  sortDirection?: string;
}

export interface UserListResponse {
  users: UserInfo[];
  pagination: {
    page: number;
    size: number;
    total: number;
    total_pages: number;
    has_next: boolean;
    has_previous: boolean;
    is_first: boolean;
    is_last: boolean;
  };
}

export interface UserCreateRequest {
  username: string;
  email: string;
  password?: string; // Password is optional on update
  roles: string[];
  enabled?: boolean;
}

export type UserUpdateRequest = Partial<UserCreateRequest> & { id: number };

export interface ApiResponse {
  message: string;
}
