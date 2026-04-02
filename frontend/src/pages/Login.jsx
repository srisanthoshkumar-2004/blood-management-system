import { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Flame, Eye, EyeOff } from 'lucide-react';

export default function Login({ onLogin }) {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post('/api/auth/login', formData);
      onLogin(res.data);
    } catch (err) {
      setError('Invalid username or password');
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[85vh]">
      <div className="bg-white p-10 rounded-[2rem] shadow-[0_8px_30px_rgb(0,0,0,0.06)] border border-slate-100 max-w-md w-full relative overflow-hidden group">
        <div className="absolute top-0 inset-x-0 h-2 bg-gradient-to-r from-red-500 to-blood-700"></div>
        
        <div className="flex flex-col items-center mb-10 mt-4">
          <div className="bg-gradient-to-br from-red-50 to-blood-100 p-4 rounded-3xl mb-5 shadow-sm border border-red-100 group-hover:scale-110 transition-transform duration-500">
            <Flame className="h-10 w-10 text-red-600" />
          </div>
          <h2 className="text-3xl font-extrabold text-slate-800 tracking-tight">Welcome Back</h2>
          <p className="text-slate-500 mt-2 font-medium">Access your BloodLink dashboard</p>
        </div>
        
        {error && <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6 text-sm font-bold border border-red-100 flex items-center justify-center animate-fade-in">{error}</div>}
        
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-2">Username</label>
            <input
              type="text"
              required
              className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800"
              placeholder="Enter your username"
              onChange={(e) => setFormData({...formData, username: e.target.value})}
            />
          </div>
          <div className="relative">
            <div className="flex justify-between mb-2">
              <label className="block text-sm font-bold text-slate-700">Password</label>
              <Link to="/forgot-password" className="text-sm font-bold text-red-600 hover:text-red-700 transition-colors">Forgot password?</Link>
            </div>
            <input
              type={showPassword ? "text" : "password"}
              required
              className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800 pr-12"
              placeholder="••••••••"
              onChange={(e) => setFormData({...formData, password: e.target.value})}
            />
            <button type="button" onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-[46px] text-slate-400 hover:text-red-600 transition-colors">
              {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
            </button>
          </div>
          <button
            type="submit"
            className="w-full bg-gradient-to-r from-red-500 to-blood-700 text-white font-bold py-4 rounded-xl hover:shadow-xl hover:shadow-red-200 hover:-translate-y-0.5 transition-all duration-300 text-lg mt-4"
          >
            Sign In
          </button>
        </form>
        
        <p className="mt-8 text-center text-slate-500 font-medium">
          New to BloodLink? <Link to="/register" className="text-red-600 font-bold hover:text-red-700 hover:underline transition">Create an account</Link>
        </p>
      </div>
    </div>
  );
}
