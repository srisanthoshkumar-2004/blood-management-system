import { Link } from 'react-router-dom';

const features = [
  {
    title: 'Intelligent Matching',
    desc: 'Smart algorithms connect patients with available donors based on proximity and health eligibility instantaneously.',
    icon: '⚡',
  },
  {
    title: 'Automated Dispatch',
    desc: 'Integrated with Twilio for dynamic SMS and Voice Call IVR workflows to guarantee rapid emergency responses.',
    icon: '📞',
  },
  {
    title: 'Enterprise Security',
    desc: 'Secured via Role-Based Access Control and JWT Authentication ensuring 100% data integrity and privacy.',
    icon: '🔐',
  },
  {
    title: 'Health Tracking',
    desc: 'Fully compliant automated eligibility tracking observing 90-day intervals and clinical donor health metrics.',
    icon: '❤️',
  }
];

export default function Home() {
  return (
    <div className="min-h-[85vh] flex flex-col justify-center items-center py-12 px-4 relative overflow-hidden">
      
      {/* Dynamic Background Elements */}
      <div className="absolute top-[-10%] left-[-10%] w-96 h-96 bg-blood-500/20 rounded-full blur-[100px] animate-pulse-glow"></div>
      <div className="absolute bottom-[-10%] right-[-10%] w-96 h-96 bg-rose-500/20 rounded-full blur-[120px] animate-pulse-glow" style={{ animationDelay: '1s' }}></div>

      <div className="glass-panel max-w-5xl w-full rounded-3xl p-8 md:p-16 text-center shadow-glass-dark relative z-10 animate-fade-in-up">
        
        <div className="inline-block px-4 py-1.5 rounded-full bg-blood-500/10 text-blood-700 font-semibold text-sm mb-6 border border-blood-500/20 animate-slide-up">
          Enterprise Healthcare Solution
        </div>

        <h1 className="text-4xl md:text-6xl font-display font-extrabold text-slate-900 tracking-tight leading-tight mb-6">
          The Future of <span className="text-transparent bg-clip-text bg-gradient-to-r from-blood-500 to-rose-600">Blood Management</span>
        </h1>
        
        <p className="text-lg md:text-xl text-slate-600 max-w-2xl mx-auto mb-10 leading-relaxed">
          A production-ready, highly-available intelligent response system engineered to save lives faster. From predictive donor matching to automated IVR escalations, we bridge the gap between emergency needs and available donors instantly.
        </p>

        <div className="flex flex-col sm:flex-row justify-center gap-4 mb-16">
          <Link to="/register" className="px-8 py-4 bg-gradient-to-r from-blood-600 to-rose-500 text-white rounded-xl font-semibold shadow-lg shadow-blood-500/30 hover:shadow-blood-500/50 hover:-translate-y-1 transition-all duration-300">
            Create an Account
          </Link>
          <Link to="/login" className="px-8 py-4 bg-white/80 text-slate-800 rounded-xl font-semibold border border-slate-200/60 shadow-sm hover:bg-white hover:shadow-md transition-all duration-300 backdrop-blur-md">
            Login to Dashboard
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 text-left">
          {features.map((feature, idx) => (
            <div key={idx} className="glass-card p-6" style={{ animationDelay: `${idx * 100}ms` }}>
              <div className="text-3xl mb-4 bg-blood-50 h-12 w-12 flex items-center justify-center rounded-xl border border-blood-100">{feature.icon}</div>
              <h3 className="font-bold text-slate-800 mb-2">{feature.title}</h3>
              <p className="text-sm text-slate-500 leading-relaxed">{feature.desc}</p>
            </div>
          ))}
        </div>
        
      </div>
    </div>
  );
}
