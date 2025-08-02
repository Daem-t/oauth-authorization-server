import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  root: path.resolve(__dirname, 'src/main/webapp'),
  publicDir: path.resolve(__dirname, 'src/main/webapp/public'),
  plugins: [react()],
  server: {
    proxy: {
      '/api': { // You might need to adjust this path based on your backend API prefix
        target: 'http://localhost:9000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // Adjust rewrite if your backend doesn't have /api prefix
      },
      // Add other proxy rules if needed, e.g., for /oauth2 endpoints
      '/oauth2': {
        target: 'http://localhost:9000',
        changeOrigin: true,
      },
      '/login': {
        target: 'http://localhost:9000',
        changeOrigin: true,
      },
      '/error': {
        target: 'http://localhost:9000',
        changeOrigin: true,
      },
      '/userinfo': {
        target: 'http://localhost:9000',
        changeOrigin: true,
      },
      '/connect': { // For the consent page
        target: 'http://localhost:9000',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: path.resolve(__dirname, 'build'), // Output to 'build' directory in the project root
    emptyOutDir: true,
  },
});