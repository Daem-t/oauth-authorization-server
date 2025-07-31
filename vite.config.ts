import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { resolve } from 'path';

export default defineConfig({
  root: 'src/main/webapp',
  plugins: [react()],
  build: {
    outDir: 'dist',
    emptyOutDir: true,
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src/main/webapp'),
    },
  },
  server: {
    proxy: {
      // 当前端请求 /api/auth/login 时，
      // 代理服务器会将其转发到 http://localhost:8080/api/auth/login
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 后端路径已包含 /api，所以不再需要 rewrite
      },
    },
  },
});
