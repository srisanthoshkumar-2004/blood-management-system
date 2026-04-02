import { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Heart, Eye, EyeOff } from 'lucide-react';

export default function Register({ onRegister }) {
  const [formData, setFormData] = useState({ 
    username: '', email: '', password: '', 
    bloodGroup: 'A+', location: '', phone: '', age: '', role: 'DONOR',
    favoritePlace: ''
  });
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = { ...formData };
      if (payload.role === 'BLOOD_BANK_MANAGER') {
          payload.bloodGroup = null;
      }
      const res = await axios.post('/api/auth/register', payload);
      onRegister(res.data);
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data || 'Registration failed');
    }
  };

  const handleChange = (e) => setFormData({...formData, [e.target.name]: e.target.value});

  return (
    <div className="flex items-center justify-center min-h-[85vh] py-10">
      <div className="bg-white p-10 rounded-[2rem] shadow-[0_8px_30px_rgb(0,0,0,0.06)] border border-slate-100 max-w-xl w-full relative overflow-hidden group">
        <div className="absolute top-0 inset-x-0 h-2 bg-gradient-to-r from-red-500 to-blood-700"></div>
        
        <div className="flex flex-col items-center mb-8 mt-4">
          <div className="bg-gradient-to-br from-red-50 to-blood-100 p-4 rounded-3xl mb-5 shadow-sm border border-red-100 group-hover:scale-110 transition-transform duration-500">
            <Heart className="h-10 w-10 text-red-600" />
          </div>
          <h2 className="text-3xl font-extrabold text-slate-800 tracking-tight">Join BloodLink</h2>
          <p className="text-slate-500 mt-2 font-medium">Create an account to donate or manage blood</p>
        </div>
        
        {error && <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6 text-sm font-bold border border-red-100 flex items-center justify-center animate-fade-in">{error}</div>}
        
        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-2">Username</label>
              <input type="text" name="username" required onChange={handleChange}
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-2">Email</label>
              <input type="email" name="email" required onChange={handleChange}
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
            </div>
            <div className="relative">
              <label className="block text-sm font-bold text-slate-700 mb-2">Password</label>
              <input type={showPassword ? "text" : "password"} name="password" required minLength="6" onChange={handleChange}
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800 pr-12" />
              <button type="button" onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-[38px] text-slate-400 hover:text-red-600 transition-colors">
                {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-2">Phone Number</label>
              <input type="tel" name="phone" required onChange={handleChange} placeholder="+1234567890"
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-2">Account Type</label>
              <select name="role" required onChange={handleChange} value={formData.role}
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800">
                <option value="DONOR">Donor</option>
                <option value="PATIENT">Patient</option>
                <option value="BLOOD_BANK_MANAGER">Blood Bank Manager</option>
              </select>
            </div>
            {formData.role !== 'BLOOD_BANK_MANAGER' && (
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Blood Group</label>
                <select name="bloodGroup" required onChange={handleChange}
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800">
                  {['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'].map(bg => (
                    <option key={bg} value={bg}>{bg}</option>
                  ))}
                </select>
              </div>
            )}
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-2">Age</label>
              <input type="number" name="age" required min="18" max="65" onChange={handleChange}
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-2">Security Question: Favorite Place?</label>
              <input type="text" name="favoritePlace" required onChange={handleChange} placeholder="e.g. Paris"
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-2 mt-2">Location (City)</label>
            <input type="text" name="location" required onChange={handleChange}
              className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
          </div>
          
          <button type="submit" className="w-full bg-gradient-to-r from-red-500 to-blood-700 text-white font-bold py-4 rounded-xl hover:shadow-xl hover:shadow-red-200 hover:-translate-y-0.5 transition-all duration-300 text-lg mt-8">
            Register Account
          </button>
        </form>
        
        <p className="mt-8 text-center text-slate-500 font-medium text-base">
          Already have an account? <Link to="/login" className="text-red-600 font-bold hover:text-red-700 hover:underline transition">Log in</Link>
        </p>
      </div>
    </div>
  );
}
