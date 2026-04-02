import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
import Dashboard from './pages/Dashboard';
import RequestBlood from './pages/RequestBlood';
import DonorResponsePage from './pages/DonorResponsePage';
import History from './pages/History';

import Home from './pages/Home';
import ChatWidget from './components/ChatWidget';

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setUser({
        token,
        username: localStorage.getItem('username'),
        id: localStorage.getItem('userId'),
        role: localStorage.getItem('role')
      });
    }
  }, []);

  const handleLogin = (userData) => {
    localStorage.setItem('token', userData.token);
    localStorage.setItem('username', userData.username);
    localStorage.setItem('userId', userData.id);
    localStorage.setItem('role', userData.role);
    setUser(userData);
  };

  const handleLogout = () => {
    localStorage.clear();
    setUser(null);
  };

  return (
    <Router>
      <Navbar user={user} onLogout={handleLogout} />
      <div className="container mx-auto px-4 py-8 max-w-7xl">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={!user ? <Login onLogin={handleLogin} /> : <Navigate to="/dashboard" />} />
          <Route path="/register" element={!user ? <Register onRegister={handleLogin} /> : <Navigate to="/dashboard" />} />
          <Route path="/forgot-password" element={!user ? <ForgotPassword /> : <Navigate to="/dashboard" />} />
          <Route path="/dashboard" element={user ? <Dashboard user={user} /> : <Navigate to="/login" />} />
          <Route path="/history" element={user ? <History /> : <Navigate to="/login" />} />
          <Route path="/request-blood" element={user ? <RequestBlood /> : <Navigate to="/login" />} />
          {/* Public response link for SMS */}
          <Route path="/respond/:requestId/donor/:donorId" element={<DonorResponsePage />} />
        </Routes>
      </div>
      <ChatWidget />
    </Router>
  );
}

export default App;
