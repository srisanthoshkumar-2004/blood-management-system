import { Link } from 'react-router-dom';
import { Droplet, LogOut, Activity, Flame } from 'lucide-react';

export default function Navbar({ user, onLogout }) {
  return (
    <nav className="bg-white/80 backdrop-blur-md border-b border-gray-100 text-slate-800 shadow-sm sticky top-0 z-50 transition-all duration-300">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-20 items-center">
          <div className="flex items-center space-x-2">
            <div className="bg-gradient-to-br from-red-500 to-blood-700 p-2 rounded-xl shadow-lg shadow-red-200">
              <Droplet className="h-6 w-6 text-white" />
            </div>
            <Link to="/" className="font-extrabold text-2xl tracking-tight text-transparent bg-clip-text bg-gradient-to-r from-blood-700 to-red-500 hover:opacity-80 transition">
              BloodLink
            </Link>
          </div>
          
          <div className="flex items-center space-x-6">
            {user ? (
              <>
                <Link to="/dashboard" className="text-sm font-semibold hover:text-red-600 transition flex items-center gap-1.5 px-2 py-1">
                  <Activity className="h-4 w-4" /> Dashboard
                </Link>
                <Link to="/history" className="text-sm font-semibold hover:text-red-600 transition flex items-center gap-1.5 px-2 py-1">
                  <Droplet className="h-4 w-4" /> History
                </Link>
                <Link to="/request-blood" className="flex items-center gap-1.5 bg-gradient-to-r from-red-500 to-blood-600 text-white px-5 py-2.5 rounded-xl font-bold tracking-wide shadow-md hover:shadow-xl hover:-translate-y-0.5 hover:shadow-red-200 transition-all duration-300">
                  <Flame className="h-4 w-4" /> Request Blood
                </Link>
                <button 
                  onClick={onLogout}
                  className="flex items-center gap-1.5 text-sm font-medium text-slate-500 hover:text-slate-900 transition px-2 py-1"
                >
                  <LogOut className="h-4 w-4 opacity-70" /> Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-sm font-semibold hover:text-red-600 transition px-3 py-2">Login</Link>
                <Link to="/register" className="bg-gradient-to-r from-red-500 to-blood-600 text-white px-6 py-2.5 rounded-xl font-bold shadow-md hover:shadow-xl hover:-translate-y-0.5 hover:shadow-red-200 transition-all duration-300">
                  Register
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
