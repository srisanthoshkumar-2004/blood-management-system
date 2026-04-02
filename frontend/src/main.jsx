import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import axios from 'axios'

// Dynamically use the production backend URL if deployed, otherwise fallback to local proxy
axios.defaults.baseURL = import.meta.env.VITE_API_URL || '';
axios.defaults.withCredentials = true;

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
