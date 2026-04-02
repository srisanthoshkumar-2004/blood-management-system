import { useState, useEffect } from 'react';
import axios from 'axios';
import { Clock, History as HistoryIcon, Droplet, User, MapPin, Activity, CheckCircle2, XCircle } from 'lucide-react';

export default function History() {
  const [donations, setDonations] = useState([]);
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role') || 'DONOR';

  useEffect(() => {
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      fetchData();
    }
  }, [userId, token]);

  const fetchData = async () => {
    try {
      const [donationsRes, requestsRes] = await Promise.all([
        axios.get(`/api/donor/${userId}/history`).catch(() => ({ data: [] })),
        axios.get(`/api/patient/${userId}/history`).catch(() => ({ data: [] }))
      ]);
      setDonations(donationsRes.data);
      setRequests(requestsRes.data); 
      setLoading(false);
    } catch (err) {
      console.error("Error fetching history", err);
      setLoading(false);
    }
  };

  if (loading) return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
    </div>
  );

  return (
    <div className="space-y-10 max-w-6xl mx-auto py-4">
      
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-4xl font-extrabold text-slate-800 tracking-tight flex items-center gap-3">
            <div className="bg-red-50 p-2.5 rounded-2xl">
              <HistoryIcon className="text-red-600 h-8 w-8" />
            </div>
            Activity History
          </h1>
          <p className="text-slate-500 mt-2 font-medium">Track your life-saving contributions and requests</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
        
        {/* Donation History Section */}
        <section className="space-y-6">
          <div className="flex items-center gap-2 border-b border-slate-100 pb-4">
            <Droplet className="text-red-500 h-6 w-6" />
            <h2 className="text-2xl font-bold text-slate-800">Donation Logs</h2>
          </div>

          {donations.length > 0 ? (
            <div className="space-y-4">
              {donations.map((donation) => (
                <div key={donation.id} className="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 hover:shadow-md transition group overflow-hidden relative">
                  <div className="absolute top-0 left-0 w-1.5 h-full bg-red-500 opacity-0 group-hover:opacity-100 transition-opacity"></div>
                  <div className="flex justify-between items-start">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2 text-slate-500 text-sm font-bold">
                        <Clock className="h-4 w-4" />
                        {new Date(donation.donationDate).toLocaleDateString('en-US', { 
                          year: 'numeric', month: 'long', day: 'numeric' 
                        })}
                      </div>
                      <h4 className="text-xl font-bold text-slate-800">{donation.location || 'Blood Center'}</h4>
                      <p className="text-slate-500 font-medium">Whole Blood Donation</p>
                    </div>
                    <div className="bg-green-50 text-green-700 px-3 py-1 rounded-full text-xs font-bold border border-green-100">
                      Completed
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="bg-slate-50 border-2 border-dashed border-slate-200 rounded-3xl p-10 text-center">
              <p className="text-slate-400 font-bold">No donations recorded yet.</p>
            </div>
          )}
        </section>

        {/* Requests History Section */}
        <section className="space-y-6">
          <div className="flex items-center gap-2 border-b border-slate-100 pb-4">
            <Activity className="text-orange-500 h-6 w-6" />
            <h2 className="text-2xl font-bold text-slate-800">Your Emergency Requests</h2>
          </div>

          {requests.length > 0 ? (
            <div className="space-y-4">
              {requests.map((request) => (
                <div key={request.id} className="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 hover:shadow-md transition">
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex items-center gap-3">
                      <div className="bg-orange-50 p-2 rounded-xl">
                        <Droplet className="text-orange-600 h-5 w-5" />
                      </div>
                      <div>
                        <h4 className="text-lg font-bold text-slate-800">{request.bloodRequest?.patientName || 'Emergency Patient'}</h4>
                        <div className="flex items-center gap-2 text-slate-400 text-xs font-bold uppercase tracking-wider">
                          <MapPin className="h-3 w-3" /> {request.bloodRequest?.location || 'Unknown Location'}
                        </div>
                      </div>
                    </div>
                    <div className={`px-3 py-1 rounded-full text-xs font-bold border ${
                      request.status === 'FULFILLED' ? 'bg-green-50 text-green-700 border-green-100' : 
                      request.status === 'FAILED' ? 'bg-red-50 text-red-700 border-red-100' :
                      request.status === 'TIMEOUT' ? 'bg-orange-50 text-orange-700 border-orange-100' :
                      'bg-blue-50 text-blue-700 border-blue-100'
                    }`}>
                      {request.status}
                    </div>
                  </div>
                  
                  <div className="grid grid-cols-1 gap-2 text-sm mt-4 pt-4 border-t border-slate-50">
                    <div className="flex items-center text-slate-600 font-medium">
                       {request.requestDetails}
                    </div>
                    <div className="flex items-center text-slate-500 font-medium italic">
                      Requested on: {new Date(request.requestDate).toLocaleDateString()}
                    </div>
                    {request.fulfilledDonor && (
                      <div className="flex items-center gap-2 text-green-600 font-bold mt-1">
                        <User className="h-4 w-4" /> Fulfilled by: {request.fulfilledDonor.username}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="bg-slate-50 border-2 border-dashed border-slate-200 rounded-3xl p-10 text-center">
              <p className="text-slate-400 font-bold">You haven't made any requests yet.</p>
            </div>
          )}
        </section>

      </div>
    </div>
  );
}
