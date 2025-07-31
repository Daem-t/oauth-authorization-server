import { configureStore } from '@reduxjs/toolkit';
import userManagementReducer from './slices/userManagementSlice';

export const store = configureStore({
  reducer: {
    userManagement: userManagementReducer,
    // Add other reducers here as your app grows
  },
});

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
