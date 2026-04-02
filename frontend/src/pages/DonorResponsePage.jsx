import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Droplet, CheckCircle, XCircle } from 'lucide-react';

export default function DonorResponsePage() {
  const { requestId, donorId } = useParams();
  const [status, setStatus] = useState('pending'); // pending, accepted, declined, expired
  const [loading, setLoading] = useState(true);

  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const checkCurrentStatus = async () => {
      try {
        const res = await axios.get(`/api/request/check-status?requestId=${requestId}&donorId=${donorId}`);
        const { requestStatus, donorResponse } = res.data;
        
        if (donorResponse === 'YES') {
          setStatus('accepted');
        } else if (donorResponse === 'NO') {
          setStatus('declined');
        } else if (requestStatus === 'FULFILLED' || requestStatus === 'FAILED') {
          setStatus('expired');
        } else {
          setStatus('pending');
        }
      } catch (err) {
        console.error("Error checking status", err);
        setStatus('pending'); 
      } finally {
        setLoading(false);
      }
    };
    checkCurrentStatus();
  }, [requestId, donorId]);

  const handleResponse = async (responseType) => {
    if (isSubmitting) return;
    setIsSubmitting(true);
    try {
      await axios.post(`/api/request/respond?requestId=${requestId}&donorId=${donorId}&responseStatus=${responseType}`);
      setStatus(responseType === 'YES' ? 'accepted' : 'declined');
    } catch (err) {
      if (err.response?.status === 400 && (err.response?.data?.includes("already") || err.response?.data?.includes("active"))) {
        setStatus('expired');
      } else {
        setStatus('declined'); // Fallback for error if already recorded as NO
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-[70vh] flex items-center justify-center">
      <div className="bg-white p-8 rounded-2xl shadow-xl max-w-lg w-full text-center border border-gray-100">
        
        {loading && <div className="text-gray-500 animate-pulse font-medium">Verifying request secure link...</div>}

        {!loading && status === 'pending' && (
          <>
            <div className="bg-red-50 p-4 rounded-full mx-auto w-20 h-20 flex items-center justify-center mb-6 animate-pulse">
              <Droplet className="h-10 w-10 text-red-600" />
            </div>
            <h2 className="text-3xl font-bold text-gray-900 mb-2 tracking-tight">Emergency Request</h2>
            <p className="text-gray-600 mb-8 text-lg font-medium">
              A life is at risk. Are you available to donate blood immediately?
            </p>
            
            <div className="flex space-x-4">
              <button 
                onClick={() => handleResponse('YES')}
                disabled={isSubmitting}
                className={`flex-1 ${isSubmitting ? 'bg-gray-400' : 'bg-green-600 hover:bg-green-700'} text-white py-4 rounded-xl font-bold text-xl transition-all shadow-lg flex items-center justify-center gap-2 hover:scale-[1.02] active:scale-[0.98]`}>
                <CheckCircle /> {isSubmitting ? '...' : 'YES'}
              </button>
              <button 
                onClick={() => handleResponse('NO')}
                disabled={isSubmitting}
                className={`flex-1 ${isSubmitting ? 'bg-gray-300' : 'bg-slate-100 hover:bg-slate-200'} text-slate-700 py-4 rounded-xl font-bold text-xl transition-all flex items-center justify-center gap-2 hover:scale-[1.02] active:scale-[0.98]`}>
                <XCircle /> NO
              </button>
            </div>
            <p className="text-xs text-gray-400 mt-6 font-medium">
              Accepting will securely share your verified information with the hospital.
            </p>
          </>
        )}

        {status === 'accepted' && (
          <div className="animate-in fade-in zoom-in duration-500">
            <div className="bg-green-100 p-4 rounded-full mx-auto w-20 h-20 flex items-center justify-center mb-6">
              <CheckCircle className="h-10 w-10 text-green-600" />
            </div>
            <h2 className="text-3xl font-bold text-green-800 mb-2">Thank you!</h2>
            <p className="text-gray-600 text-lg font-medium">
              Critical acceptance recorded. The patient and hospital have been notified. Please head to the location as soon as possible.
            </p>
          </div>
        )}

        {status === 'declined' && (
          <div className="animate-in fade-in zoom-in duration-500">
            <div className="bg-slate-100 p-4 rounded-full mx-auto w-20 h-20 flex items-center justify-center mb-6">
              <XCircle className="h-10 w-10 text-slate-500" />
            </div>
            <h2 className="text-3xl font-bold text-slate-800 mb-2">Response Recorded</h2>
            <p className="text-gray-600 text-lg font-medium">
              You have declined this request. Your preference has been saved securely, and we will not contact you for this specific emergency again.
            </p>
          </div>
        )}

        {status === 'expired' && (
          <div className="animate-in fade-in zoom-in duration-500">
            <div className="bg-red-50 p-4 rounded-full mx-auto w-20 h-20 flex items-center justify-center mb-6">
              <XCircle className="h-10 w-10 text-red-400" />
            </div>
            <h2 className="text-3xl font-bold text-gray-800 mb-2 tracking-tight">Request No Longer Active</h2>
            <p className="text-gray-600 text-lg font-medium">
              This emergency request has already been fulfilled or the time window has passed. Thank you for your willingness to help.
            </p>
          </div>
        )}
        
      </div>
    </div>
  );
}
