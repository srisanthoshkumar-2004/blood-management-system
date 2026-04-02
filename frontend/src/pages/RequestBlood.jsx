import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../api/axios';
import { ShieldAlert, MapPin, Phone, Building2, Flame, HeartPulse, CheckCircle, ArrowRight } from 'lucide-react';

export default function RequestBlood() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    patientName: '', hospitalName: '', contactNumber: '', bloodGroup: 'A+', location: '', emergencyLevel: 'HIGH'
  });
  const [requestStatus, setRequestStatus] = useState(null);
  const [activeRequestId, setActiveRequestId] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post('/request/blood', formData);
      setActiveRequestId(res.data.id);
      setRequestStatus(res.data.status); // PENDING or PROCESSING
    } catch (err) {
      alert("Failed to create request");
    }
  };

  // Poll for status updates
  useEffect(() => {
    let interval;
    if (activeRequestId && (requestStatus === 'PROCESSING' || requestStatus === 'PENDING')) {
      interval = setInterval(async () => {
        try {
          const res = await axios.get(`/request/${activeRequestId}`);
          setRequestStatus(res.data.status);
          if (res.data.status === 'FULFILLED' || res.data.status === 'FAILED') {
            clearInterval(interval);
          }
        } catch (error) {
          console.error("Error polling status", error);
        }
      }, 2000); // Check every 2 seconds for a better "instant" feel
    }
    return () => clearInterval(interval);
  }, [activeRequestId, requestStatus]);

  // Automatic redirect after match
  useEffect(() => {
    if (requestStatus === 'FULFILLED') {
      const timer = setTimeout(() => {
        navigate('/history');
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [requestStatus, navigate]);

  const handleChange = (e) => setFormData({...formData, [e.target.name]: e.target.value});

  return (
    <div className="max-w-3xl mx-auto py-6">
      <div className="bg-white p-10 rounded-[2rem] shadow-[0_8px_30px_rgb(0,0,0,0.06)] border border-slate-100 relative overflow-hidden group">
        
        {/* Decorative Top Border */}
        <div className="absolute top-0 inset-x-0 h-2 bg-gradient-to-r from-red-600 via-red-500 to-orange-500"></div>

        {!activeRequestId && (
          <div className="flex flex-col items-center mb-10 text-center">
            <div className="bg-red-50 p-5 rounded-3xl mb-6 shadow-sm border border-red-100 relative group-hover:scale-105 transition-transform duration-500">
              <div className="absolute inset-0 bg-red-400 rounded-3xl animate-ping opacity-20"></div>
              <ShieldAlert className="h-10 w-10 text-red-600 relative z-10" />
            </div>
            <h2 className="text-4xl font-extrabold text-slate-800 tracking-tight">Emergency Request</h2>
            <p className="text-slate-500 mt-3 max-w-lg text-lg leading-relaxed">
              This will trigger the smart matching system to instantly find and instantly ping eligible donors nearby.
            </p>
          </div>
        )}

        {!activeRequestId ? (
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Patient Name</label>
                <input type="text" name="patientName" required onChange={handleChange}
                  className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Contact Number</label>
                <div className="relative">
                  <Phone className="absolute left-4 top-4 h-5 w-5 text-slate-400" />
                  <input type="tel" name="contactNumber" required onChange={handleChange} placeholder="+1..."
                    className="w-full pl-12 pr-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Hospital Name</label>
                <div className="relative">
                  <Building2 className="absolute left-4 top-4 h-5 w-5 text-slate-400" />
                  <input type="text" name="hospitalName" required onChange={handleChange}
                    className="w-full pl-12 pr-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">City / Location</label>
                <div className="relative">
                  <MapPin className="absolute left-4 top-4 h-5 w-5 text-slate-400" />
                  <input type="text" name="location" required onChange={handleChange}
                    className="w-full pl-12 pr-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-medium text-slate-800" />
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2 mt-2">Blood Group Needed</label>
                <select name="bloodGroup" required onChange={handleChange}
                  className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-bold text-red-600 text-lg">
                  {['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'].map(bg => (
                    <option key={bg} value={bg}>{bg}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2 mt-2">Emergency Level</label>
                <select name="emergencyLevel" required onChange={handleChange}
                  className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 focus:bg-white transition-all outline-none font-bold text-slate-800 text-lg">
                    <option value="CRITICAL">Critical (Immediate match needed)</option>
                    <option value="HIGH">High (Needed within hours)</option>
                    <option value="MODERATE">Moderate (Needed today)</option>
                </select>
              </div>
            </div>

            <button type="submit" 
              className="w-full bg-gradient-to-r from-red-600 via-red-500 to-orange-500 text-white font-extrabold py-5 rounded-2xl shadow-lg shadow-red-200 hover:shadow-2xl hover:shadow-red-300 hover:-translate-y-1 transition-all duration-300 flex items-center justify-center gap-3 text-lg tracking-wide mt-8">
              <Flame className="h-6 w-6" /> Trigger Emergency Sequence
            </button>
          </form>
        ) : (
          <div className="text-center py-12 px-4">
            
            {requestStatus === 'PROCESSING' && (
              <div className="animate-fade-in flex flex-col items-center">
                {/* Modern Radar Animation */}
                <div className="relative w-48 h-48 mb-12 flex items-center justify-center">
                  <div className="absolute inset-0 border-[3px] border-red-100 rounded-full"></div>
                  <div className="absolute inset-4 border-[2px] border-red-200 rounded-full border-t-red-500 animate-[spin_3s_linear_infinite]"></div>
                  <div className="absolute inset-8 border-[1px] border-red-200 rounded-full border-b-orange-400 animate-[spin_2s_linear_infinite_reverse]"></div>
                  <div className="absolute inset-0 bg-red-500 opacity-10 rounded-full animate-ping-slow"></div>
                  
                  <div className="bg-red-50 p-4 rounded-full shadow-inner relative z-10 border-2 border-white">
                    <HeartPulse className="h-10 w-10 text-red-600 animate-pulse" />
                  </div>
                </div>
                
                <h3 className="text-3xl font-extrabold text-slate-800 mb-3 tracking-tight">Broadcasting Request...</h3>
                <p className="text-slate-500 text-lg max-w-md mx-auto leading-relaxed">
                  We are actively texting batches of eligible <span className="font-bold text-red-600">{formData.bloodGroup}</span> donors in {formData.location}. Please keep this tab open. Process takes up to 3 minutes per batch.
                </p>
              </div>
            )}
            
            {requestStatus === 'FULFILLED' && (
              <div className="bg-gradient-to-br from-green-50 to-emerald-100 rounded-[2rem] p-10 border border-green-200 animate-slide-up shadow-xl shadow-green-100/50">
                <div className="bg-white p-5 rounded-full w-24 h-24 mx-auto mb-6 flex items-center justify-center shadow-lg border border-green-100">
                  <CheckCircle className="h-12 w-12 text-green-500" />
                </div>
                <h3 className="text-4xl font-extrabold text-green-800 tracking-tight">Match Found!</h3>
                <p className="text-green-700 mt-4 text-xl font-medium leading-relaxed max-w-lg mx-auto">
                    A donor has accepted your request. Their contact details have been sent to your phone.
                </p>
                <div className="mt-8 flex flex-col items-center gap-4">
                  <button 
                    onClick={() => navigate('/history')}
                    className="px-8 py-4 bg-green-600 text-white rounded-2xl font-bold text-lg hover:bg-green-700 transition-all shadow-md active:scale-95 flex items-center gap-2">
                    View Details in History <ArrowRight className="h-5 w-5" />
                  </button>
                  <p className="text-green-600 text-sm font-medium animate-pulse">
                    Redirecting to history in 5 seconds...
                  </p>
                </div>
              </div>
            )}
            
            {requestStatus === 'FAILED' && (
              <div className="bg-slate-50 rounded-[2rem] p-10 border border-slate-200 animate-slide-up">
                <div className="bg-slate-200 p-5 rounded-full w-20 h-20 mx-auto mb-6 flex items-center justify-center">
                  <ShieldAlert className="h-10 w-10 text-slate-500" />
                </div>
                <h3 className="text-3xl font-extrabold text-slate-700 tracking-tight">No Donors Available</h3>
                <p className="text-slate-500 mt-4 text-lg font-medium">
                  Unfortunately, none of the contacted donors were able to accept the request at this exact time, or no matches were found in your area.
                </p>
                <button onClick={() => setActiveRequestId(null)} className="mt-8 px-6 py-3 bg-white border border-slate-200 rounded-xl text-slate-700 font-bold hover:bg-slate-100 transition shadow-sm">
                  Acknowledge & Try Again
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
