import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import adminService from '../../services/adminService';
import { UserInfo, UserListRequest, UserListResponse } from '../../types/admin';

interface UserManagementState {
  users: UserInfo[];
  loading: 'idle' | 'pending' | 'succeeded' | 'failed';
  error: string | null;
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  keyword: string;
}

const initialState: UserManagementState = {
  users: [],
  loading: 'idle',
  error: null,
  currentPage: 1,
  totalPages: 0,
  totalElements: 0,
  pageSize: 10,
  keyword: '',
};

// Async thunk for fetching users
export const fetchUsers = createAsyncThunk<
  UserListResponse, // Return type of the payload
  UserListRequest,  // Type of the argument
  { rejectValue: string }
>('userManagement/fetchUsers', async (params, { rejectWithValue }) => {
  try {
    const response = await adminService.getUsers(params);
    return response;
  } catch (err: any) {
    return rejectWithValue(err.response?.data?.message || err.message || 'Failed to fetch users');
  }
});

const userManagementSlice = createSlice({
  name: 'userManagement',
  initialState,
  reducers: {
    setCurrentPage: (state, action: PayloadAction<number>) => {
      state.currentPage = action.payload;
    },
    setPageSize: (state, action: PayloadAction<number>) => {
      state.pageSize = action.payload;
    },
    setKeyword: (state, action: PayloadAction<string>) => {
      state.keyword = action.payload;
      state.currentPage = 1; // Reset to first page on new search
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUsers.pending, (state) => {
        state.loading = 'pending';
        state.error = null;
      })
      .addCase(fetchUsers.fulfilled, (state, action: PayloadAction<UserListResponse>) => {
        state.loading = 'succeeded';
        state.users = action.payload.users;
        state.totalPages = action.payload.pagination.total_pages;
        state.totalElements = action.payload.pagination.total;
      })
      .addCase(fetchUsers.rejected, (state, action) => {
        state.loading = 'failed';
        state.error = action.payload as string;
      });
  },
});

export const { setCurrentPage, setPageSize, setKeyword } = userManagementSlice.actions;

export default userManagementSlice.reducer;