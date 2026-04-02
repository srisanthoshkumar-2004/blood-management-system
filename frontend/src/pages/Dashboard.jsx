import { useState, useEffect } from 'react';
import axios from '../api/axios';
import { Activity, Clock, ShieldCheck, HeartPulse, X, Stethoscope } from 'lucide-react';
export default function Dashboard() {
  const [healthData, setHealthData] = useState(null);
  const [isAvailable, setIsAvailable] = useState(true);
  const [managedRequests, setManagedRequests] = useState([]);
  const [isLive, setIsLive] = useState(false);
  const [showHealthModal, setShowHealthModal] = useState(false);
  const [healthForm, setHealthForm] = useState({
    weight: '', hemoglobinLevel: '', bloodPressure: '', isEligible: true, lastCheckupDate: new Date().toISOString().split('T')[0], medicalConditions: 'Healthy'
  });
  
  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role') || 'DONOR';

  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

  useEffect(() => {
    if (role === 'DONOR') {
      fetchHealthData();
      fetchUserData();
    }
    if (role === 'BLOOD_BANK_MANAGER') {
      fetchManagedRequests();
      setIsLive(true);
      const interval = setInterval(fetchManagedRequests, 5000); // Increased frequency: 5s
      return () => clearInterval(interval);
    }
  }, [role]);

  const fetchManagedRequests = async () => {
    try {
      const res = await axios.get('/request/managed');
      setManagedRequests(res.data);
    } catch (err) {
      console.error("Error fetching managed requests", err);
    }
  };

  const fetchUserData = async () => {
    try {
      const res = await axios.get(`/users/${userId}`);
      setIsAvailable(res.data.available);
    } catch (err) {
      console.error("Error fetching user data", err);
    }
  };

  const fetchHealthData = async () => {
    try {
      const res = await axios.get(`/health/${userId}`);
      setHealthData(res.data);
    } catch (err) {
      console.log('No health data found (404 is expected for new users)');
    }
  };

  const handleUpdateStatus = async (available) => {
    try {
      await axios.put(`/users/${userId}/availability?available=${available}`);
      setIsAvailable(available);
      alert("Availability updated!");
    } catch(err) {
      console.error(err);
    }
  };

  const handleHealthSubmit = async (e) => {
    e.preventDefault();
    try {
      // isEligible needs to be a boolean
      const payload = {
        ...healthForm,
        isEligible: healthForm.isEligible === 'true' || healthForm.isEligible === true
      };
      await axios.post(`/health/update?userId=${userId}`, payload);
      setShowHealthModal(false);
      fetchHealthData(); // Refresh data
    } catch(err) {
      alert("Failed to update health profile");
    }
  };

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 relative">
      {/* Main Content Area */}
      <div className="lg:col-span-2 space-y-8">
        
        {role === 'DONOR' && (
          <>
            {/* Availability Card */}
        <div className="bg-white p-8 rounded-3xl shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-slate-100 relative overflow-hidden group hover:shadow-[0_8px_30px_rgb(239,68,68,0.08)] transition-all duration-300">
          <div className="absolute -right-10 -top-10 w-40 h-40 bg-red-50 rounded-full blur-3xl opacity-50 group-hover:bg-red-100 transition-all duration-500"></div>
          
          <div className="flex items-center justify-between mb-6 relative z-10">
            <h2 className="text-3xl font-extrabold text-slate-800 flex items-center gap-3">
              <div className="bg-red-50 p-3 rounded-2xl">
                <Activity className="text-red-500 h-8 w-8" />
              </div>
              Donor Hub
            </h2>
            <div className={`flex items-center gap-2 px-4 py-1.5 rounded-full text-sm font-bold shadow-sm ${isAvailable ? 'bg-green-100 text-green-700 border border-green-200' : 'bg-slate-100 text-slate-600 border border-slate-200'}`}>
              <div className={`w-2.5 h-2.5 rounded-full ${isAvailable ? 'bg-green-500 animate-pulse' : 'bg-slate-400'}`}></div>
              {isAvailable ? 'Active Mode' : 'Paused'}
            </div>
          </div>
          
          <p className="text-slate-500 text-lg mb-8 max-w-xl relative z-10 leading-relaxed">
            Your availability dictates if the Emergency Matching Radar can ping you for urgent requests. Keep it active to save lives!
          </p>
          
          <div className="flex flex-wrap gap-4 relative z-10">
            <button 
              onClick={() => handleUpdateStatus(true)} 
              className={`px-6 py-3 rounded-xl font-bold transition-all duration-300 shadow-md ${isAvailable ? 'bg-gradient-to-r from-red-500 to-blood-600 text-white shadow-red-200 hover:-translate-y-1 hover:shadow-xl hover:shadow-red-200' : 'bg-white text-slate-700 border border-slate-200 hover:border-red-200 hover:text-red-600'}`}>
              Available to Donate
            </button>
            <button 
              onClick={() => handleUpdateStatus(false)} 
              className={`px-6 py-3 rounded-xl font-bold transition-all duration-300 shadow-sm ${!isAvailable ? 'bg-slate-800 text-white shadow-slate-200' : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'}`}>
              Pause Availability
            </button>
          </div>
        </div>

        {/* Health Data Block */}
        <div className="bg-white p-8 rounded-3xl shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-slate-100 hover:shadow-[0_8px_30px_rgb(239,68,68,0.05)] transition-all duration-300">
          <div className="flex items-center justify-between mb-8 border-b border-slate-100 pb-4">
            <div className="flex items-center gap-3">
              <HeartPulse className="text-red-500 h-7 w-7" />
              <h3 className="text-2xl font-bold text-slate-800">Your Health Profile</h3>
            </div>
            {healthData && (
              <button onClick={() => setShowHealthModal(true)} className="text-sm font-bold text-red-600 hover:text-red-700 underline">
                Update Profile
              </button>
            )}
          </div>
          
          {healthData ? (
            <div className="grid grid-cols-2 sm:grid-cols-5 gap-6">
              <div className="p-5 bg-gradient-to-b from-slate-50 to-white border border-slate-100 rounded-2xl hover:shadow-md transition duration-300">
                <span className="block text-sm font-semibold text-slate-500 mb-1">Weight</span>
                <span className="font-extrabold text-2xl text-slate-800">{healthData.weight} <span className="text-base font-medium text-slate-400">kg</span></span>
              </div>
              <div className="p-5 bg-gradient-to-b from-slate-50 to-white border border-slate-100 rounded-2xl hover:shadow-md transition duration-300">
                <span className="block text-sm font-semibold text-slate-500 mb-1">Hemoglobin</span>
                <span className="font-extrabold text-2xl text-slate-800">{healthData.hemoglobinLevel} <span className="text-base font-medium text-slate-400">g/dL</span></span>
              </div>
              <div className="p-5 bg-gradient-to-b from-slate-50 to-white border border-slate-100 rounded-2xl hover:shadow-md transition duration-300">
                <span className="block text-sm font-semibold text-slate-500 mb-1">Pressure</span>
                <span className="font-extrabold text-2xl text-slate-800">{healthData.bloodPressure}</span>
              </div>
              <div className="p-5 bg-gradient-to-b from-slate-50 to-white border border-slate-100 rounded-2xl hover:shadow-md transition duration-300 flex flex-col justify-center">
                <span className="block text-sm font-semibold text-slate-500 mb-1">Status</span>
                <span className="font-extrabold text-lg text-slate-800 truncate" title={healthData.medicalConditions || 'Healthy'}>
                  {healthData.medicalConditions || 'Healthy'}
                </span>
              </div>
              <div className={`p-5 rounded-2xl border transition duration-300 flex flex-col justify-center items-center ${healthData.isEligible ? 'bg-green-50 border-green-100' : 'bg-red-50 border-red-100'}`}>
                <span className={`block text-xs font-bold tracking-wider uppercase mb-1 ${healthData.isEligible ? 'text-green-600' : 'text-red-500'}`}>Eligibility</span>
                <div className="flex items-center gap-2">
                  {healthData.isEligible && <ShieldCheck className="h-6 w-6 text-green-500" />}
                  <span className={`font-extrabold text-2xl ${healthData.isEligible ? 'text-green-700' : 'text-red-600'}`}>
                    {healthData.isEligible ? 'Cleared' : 'Not Cleared'}
                  </span>
                </div>
              </div>
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-slate-500 text-lg mb-4">You haven't updated your health profile yet.</p>
              <button 
                onClick={() => setShowHealthModal(true)} 
                className="text-red-600 font-bold hover:text-red-700 hover:underline flex items-center justify-center gap-2 mx-auto">
                <HeartPulse className="h-5 w-5" /> Complete Profile Setup
              </button>
            </div>
          )}
        </div>
          </>
        )}

        {role === 'PATIENT' && (
          <div className="bg-white p-8 rounded-3xl shadow-sm border border-slate-100 flex flex-col items-center text-center justify-center min-h-[300px]">
             <div className="bg-red-50 p-4 rounded-full mb-4">
                <HeartPulse className="h-10 w-10 text-red-500" />
             </div>
             <h2 className="text-3xl font-extrabold text-slate-800 mb-2">Patient Dashboard</h2>
             <p className="text-slate-500 w-2/3 mx-auto">Your donor profile is currently disabled because you requested blood. Check your Request History or reach out for more help.</p>
          </div>
        )}

        {role === 'BLOOD_BANK_MANAGER' && (
          <div className="space-y-6">
            <div className="bg-white p-8 rounded-3xl shadow-2xl border border-slate-100 relative overflow-hidden group">
              <div className="absolute top-0 right-0 p-4">
                 <div className="flex items-center gap-2 bg-red-50 px-3 py-1 rounded-full border border-red-100">
                    <div className="w-2 h-2 rounded-full bg-red-500 animate-pulse"></div>
                    <span className="text-[10px] font-black uppercase tracking-widest text-red-600">Emergency Radar Active</span>
                 </div>
              </div>

              <div className="flex items-center gap-4 mb-8">
                <div className="bg-red-500 p-4 rounded-2xl shadow-lg shadow-red-200">
                  <Activity className="h-8 w-8 text-white" />
                </div>
                <div>
                  <h2 className="text-3xl font-extrabold text-slate-800 tracking-tight">Blood Bank Hub</h2>
                  <p className="text-slate-500 font-medium">Monitoring local emergencies in real-time</p>
                </div>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                 <div className="p-4 bg-slate-50 rounded-2xl border border-slate-100 flex flex-col justify-center items-center">
                    <span className="text-sm font-bold text-slate-500 uppercase">Total Requests</span>
                    <span className="text-2xl font-black text-slate-800">{managedRequests.length}</span>
                 </div>
                 <div className="p-4 bg-green-50 rounded-2xl border border-green-100 flex flex-col justify-center items-center">
                    <span className="text-sm font-bold text-green-600 uppercase">Fulfilled</span>
                    <span className="text-2xl font-black text-green-700">{managedRequests.filter(r => r.status === 'FULFILLED').length}</span>
                 </div>
                 <div className="p-4 bg-blue-50 rounded-2xl border border-blue-100 flex flex-col justify-center items-center">
                    <span className="text-sm font-bold text-blue-600 uppercase">Pending/Proc</span>
                    <span className="text-2xl font-black text-blue-700">{managedRequests.filter(r => r.status === 'PENDING' || r.status === 'PROCESSING').length}</span>
                 </div>
                 <div className="p-4 bg-red-50 rounded-2xl border border-red-100 flex flex-col justify-center items-center">
                    <span className="text-sm font-bold text-red-600 uppercase">Failed</span>
                    <span className="text-2xl font-black text-red-700">{managedRequests.filter(r => r.status === 'FAILED').length}</span>
                 </div>
              </div>

              <div className="space-y-4">
                {managedRequests.length > 0 ? (
                  managedRequests.map((req) => (
                    <div key={req.id} className="group/item flex items-center justify-between p-6 bg-slate-50 hover:bg-white rounded-2xl border border-transparent hover:border-red-100 hover:shadow-xl hover:shadow-red-500/5 transition-all duration-300">
                       <div className="flex items-center gap-6">
                          <div className="flex flex-col items-center justify-center w-16 h-16 bg-white rounded-xl border border-slate-200 shadow-sm group-hover/item:border-red-200">
                             <span className="text-2xl font-black text-red-600 leading-none">{req.bloodGroup}</span>
                             <span className="text-[10px] font-bold text-slate-400 uppercase mt-1">Group</span>
                          </div>
                          <div>
                             <h4 className="font-bold text-slate-800 group-hover/item:text-red-700 transition-colors uppercase tracking-tight">{req.patientName} <span className="text-xs font-medium text-slate-400 lowercase ml-2">via {req.hospitalName}</span></h4>
                             <div className="flex items-center gap-4 mt-2">
                                <span className="flex items-center gap-1.5 text-xs font-bold text-slate-500">
                                   <Clock className="h-3.5 w-3.5 text-slate-400" />
                                   {new Date(req.requestDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </span>
                                <span className={`px-2.5 py-0.5 rounded-md text-[10px] font-bold uppercase tracking-wider ${
                                  req.status === 'PENDING' ? 'bg-amber-100 text-amber-700' :
                                  req.status === 'PROCESSING' ? 'bg-blue-100 text-blue-700' :
                                  req.status === 'FULFILLED' ? 'bg-green-100 text-green-700' : 'bg-slate-200 text-slate-600'
                                }`}>
                                   {req.status}
                                </span>
                             </div>
                          </div>
                       </div>
                       <div className="text-right">
                          <div className="text-xs font-bold text-slate-400 mb-1">Emergency Level</div>
                          <div className={`text-sm font-black uppercase ${
                            req.emergencyLevel === 'CRITICAL' ? 'text-red-500' : 'text-amber-500'
                          }`}>{req.emergencyLevel}</div>
                       </div>
                    </div>
                  ))
                ) : (
                  <div className="text-center py-16 bg-slate-50 rounded-3xl border-2 border-dashed border-slate-200">
                    <Clock className="h-12 w-12 text-slate-300 mx-auto mb-4" />
                    <p className="text-slate-500 font-bold">No active emergencies in your location</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

      </div>

      {/* Side Column Removed as per constraints */}

      {/* Health Setup Modal */}
      {showHealthModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-white p-8 rounded-[2rem] shadow-2xl max-w-md w-full relative">
            <button onClick={() => setShowHealthModal(false)} className="absolute top-6 right-6 text-slate-400 hover:text-slate-700">
              <X className="h-6 w-6" />
            </button>
            <h2 className="text-2xl font-extrabold text-slate-800 mb-6">Update Health Profile</h2>
            <form onSubmit={handleHealthSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Weight (kg)</label>
                <input type="number" step="0.1" required onChange={(e) => setHealthForm({...healthForm, weight: e.target.value})} 
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 outline-none" placeholder="e.g. 70" />
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Hemoglobin Level (g/dL)</label>
                <input type="number" step="0.1" required onChange={(e) => setHealthForm({...healthForm, hemoglobinLevel: e.target.value})} 
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 outline-none" placeholder="e.g. 13.5" />
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Blood Pressure</label>
                <input type="text" required onChange={(e) => setHealthForm({...healthForm, bloodPressure: e.target.value})} 
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 outline-none" placeholder="120/80" />
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Health Status</label>
                <input type="text" required onChange={(e) => setHealthForm({...healthForm, medicalConditions: e.target.value})} 
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 outline-none" placeholder="e.g. Healthy, Diabetic, etc." defaultValue="Healthy" />
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Are you currently eligible to donate?</label>
                <select onChange={(e) => setHealthForm({...healthForm, isEligible: e.target.value})} 
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-4 focus:ring-red-500/10 focus:border-red-500 outline-none">
                  <option value="true">Yes, I am cleared</option>
                  <option value="false">No, I am not eligible</option>
                </select>
              </div>
              <button type="submit" className="w-full bg-red-600 text-white font-bold py-4 rounded-xl hover:bg-red-700 hover:shadow-lg transition-all mt-4">
                Save Health Profile
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
