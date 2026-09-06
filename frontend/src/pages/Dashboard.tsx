import { AlertCircle, CheckCircle2, FileScan, Plus, ArrowRight, TriangleAlert, RefreshCw } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { StatCard } from '../components/StatCard';
import { useAuth } from '../auth';
import { getDashboardStats, DashboardStats } from '../api';
import { useEffect, useState } from 'react';

export function Dashboard() {
  const navigate = useNavigate(); const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats | null>(null); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  async function load() { setLoading(true); setError(''); try { setStats(await getDashboardStats()); } catch (e: any) { setError(e.message || 'Could not load dashboard data.'); } finally { setLoading(false); } }
  useEffect(() => { load(); }, []);
  const s = stats || { totalScans: 0, compliantSkus: 0, violations: 0, passRate: 0, queued: 0, defects: [], priorities: [] };
  return <>
    <div className="page-header"><div><p className="eyebrow">PCR 2011 REGULATORY WORKSPACE</p><h1>Welcome back, {user?.name?.split(' ')[0] || 'User'}</h1><p>Live compliance data from your backend. Use New Scan to inspect a packaged commodity.</p></div><div className="header-actions"><button className="btn pale" onClick={load} disabled={loading}><RefreshCw size={16}/> Refresh</button><button className="btn dark-btn" onClick={() => navigate('/scan')}><Plus size={18}/> New Scan</button></div></div>
    {error && <div className="error-box">{error} Make sure the backend is running and VITE_API_URL points to it.</div>}
    <div className="stats-grid">
      <StatCard label="TOTAL SCANS AUDITED" value={loading ? '—' : s.totalScans.toLocaleString()} note="Live from database" icon={<FileScan/>}/>
      <StatCard label="FULLY COMPLIANT SKUS" value={loading ? '—' : s.compliantSkus.toLocaleString()} note={`${s.passRate ?? 0}% pass rate`} tone="green" icon={<CheckCircle2/>}/>
      <StatCard label="STATUTORY VIOLATIONS" value={loading ? '—' : s.violations.toLocaleString()} note="Requires review" tone="red" icon={<AlertCircle/>}/>
    </div>
    <div className="dashboard-grid">
      <section className="panel defect-panel"><div className="panel-title"><div><h2>Defect Classification</h2><p>Live recurring infractions from completed scans.</p></div><span>Backend data</span></div>
        {(s.defects || []).length === 0 ? <div className="empty-state">No defect data available yet.</div> : (s.defects || []).map(d => <div className="defect-row" key={d.label}><span><i className={`dot ${d.tone}`}/>{d.label}</span><b>{d.count} items</b></div>)}
      </section>
      <section className="panel priority-panel"><div className="panel-title"><div><h2><TriangleAlert size={20}/> Priority Action Required</h2></div><span className="risk-pill">{(s.priorities || []).length} risks</span></div>
        <div className="priority-cards">{(s.priorities || []).length === 0 ? <div className="empty-state">No priority actions returned by the backend.</div> : (s.priorities || []).map(p => <article className="priority-card" key={p.id}><div className="priority-top"><span>{p.id}</span><b>{p.severity}</b></div><h3>{p.product}</h3><p>{p.description}</p><footer><button onClick={() => navigate('/result/'+p.id)}>View result <ArrowRight size={16}/></button></footer></article>)}</div>
      </section>
    </div>
  </>;
}
