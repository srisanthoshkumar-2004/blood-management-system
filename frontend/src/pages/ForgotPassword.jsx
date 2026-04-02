import { useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { KeyRound, Eye, EyeOff, ShieldCheck } from 'lucide-react';

export default function ForgotPassword() {
  const [formData, setFormData] = useState({ identifier: '', favoritePlace: '', newPassword: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    try {
      const res = await axios.post('/api/auth/reset-password', formData);
      setSuccess(res.data);
      setTimeout(() => navigate('/login'), 3000);
    } catch (err) {
      setError(err.response?.data || err.response?.data?.message || 'Password reset failed. Please check your answers.');
    }
  };

  const handleChange = (e) => setFormData({...formData, [e.target.name]: e.target.value});

  return (
    <div className="flex items-center justify-center min-h-[85vh]">
      <div className="bg-white p-10 rounded-[2rem] shadow-[0_8px_30px_rgb(0,0,0,0.06)] border border-slate-100 max-w-md w-full relative overflow-hidden group">
        <div className="absolute top-0 inset-x-0 h-2 bg-gradient-to-r from-red-500 to-blood-700"></div>
        
        <div className="flex flex-col items-center mb-10 mt-4">
          <div className="bg-gradient-to-br from-red-50 to-blood-100 p-4 rounded-3xl mb-5 shadow-sm border border-red-100 group-hover:scale-110 transition-transform duration-500">
            <KeyRound className="h-10 w-10 text-red-600" />
          </div>
          <h2 className="text-3xl font-extrabold text-slate-800 tracking-tight">Recovery</h2>
          <p className="text-slate-500 mt-2 font-medium text-center">Answer your security question to reset your password.</p>
        </div>
        
        {error && <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6 text-sm font-bold border border-red-100 flex items-center justify-center animate-fade-in">{error}</div>}
        {success && <div className="bg-green-50 text-green-600 p-4 rounded-xl mb-6 text-sm font-bold border border-green-100 flex items-center justify-center animate-fade-in"><ShieldCheck className="w-5 h-5 mr-2"/> {success}</div>}
        
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-2">Username or Email</label>
            <input
              type="text"
              name="identifier"
              required
              className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800"
              placeholder="Enter your username or email"
              onChange={handleChange}
            />
          </div>
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-2">Security Question: Favorite Place?</label>
            <input
              type="text"
              name="favoritePlace"
              required
              className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800"
              placeholder="e.g. Paris"
              onChange={handleChange}
            />
          </div>
          <div className="relative">
            <label className="block text-sm font-bold text-slate-700 mb-2">New Password</label>
            <input
              type={showPassword ? "text" : "password"}
              name="newPassword"
              required minLength="6"
              className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800 pr-12"
              placeholder="••••••••"
              onChange={handleChange}
            />
            <button type="button" onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-[38px] text-slate-400 hover:text-red-600 transition-colors">
              {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
            </button>
          </div>
          <button
            type="submit"
            className="w-full bg-gradient-to-r from-red-500 to-blood-700 text-white font-bold py-4 rounded-xl hover:shadow-xl hover:shadow-red-200 hover:-translate-y-0.5 transition-all duration-300 text-lg mt-4"
          >
            Reset Password
          </button>
        </form>
        
        <p className="mt-8 text-center text-slate-500 font-medium">
          Remembered your password? <Link to="/login" className="text-red-600 font-bold hover:text-red-700 hover:underline transition">Log in</Link>
        </p>
      </div>
    </div>
  );
}
