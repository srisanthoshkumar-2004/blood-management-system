import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // Load env variables during build
  const env = loadEnv(mode, process.cwd(), '');
  return {
    plugins: [react()],
    server: {
      port: 5173,
      proxy: {
        '/api': {
          // Dynamic Proxy Target
          // Use BACKEND_URL from your .env if available, or localhost:8080.
          target: env.BACKEND_URL || 'http://localhost:8080',
          changeOrigin: true
        }
      }
    }
  }
})
