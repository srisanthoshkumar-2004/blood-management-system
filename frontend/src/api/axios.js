import axios from 'axios';

// 🌍 Dynamic API URL Environment Mapping
// 1. In Production (Render/Vercel): Use the VITE_API_URL environment variable.
// 2. In Local Development: Fallback to '/api' (handled by Vite proxy).
const instance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Intercept responses for global error handling
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle unauthorized or server errors here
    return Promise.reject(error);
  }
);

export default instance;
