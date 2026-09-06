import { Search, ArrowRight, RefreshCw } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { StatusBadge } from '../components/StatusBadge';
import { useEffect, useState } from 'react';
import { getHistory, HistoryItem } from '../api';

export function History() {
  const navigate = useNavigate(); const [q, setQ] = useState(''); const [items, setItems] = useState<HistoryItem[]>([]); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  async function load() { setLoading(true); setError(''); try { setItems(await getHistory()); } catch (e: any) { setError(e.message || 'Could not load scan history.'); } finally { setLoading(false); } }
  useEffect(() => { load(); }, []);
  const filtered = items.filter(x => `${x.product} ${x.id}`.toLowerCase().includes(q.toLowerCase()));
  return <>
    <div className="page-header simple"><div><p className="eyebrow">COMPLIANCE RECORDS</p><h1>Scan History</h1><p>Review previous packaging compliance scans and their statutory verdicts.</p></div><button className="btn dark-btn" onClick={() => navigate('/scan')}>+ New Scan</button></div>
    <div className="search-row"><div className="search-box"><Search size={18}/><input value={q} onChange={e => setQ(e.target.value)} placeholder="Search product or scan ID..."/></div><button className="btn pale" onClick={load} disabled={loading}><RefreshCw size={16}/> Refresh</button></div>
    {error && <div className="error-box">{error}</div>}
    <section className="panel history-panel"><div className="history-table header"><span>SCAN ID</span><span>PRODUCT</span><span>DATE</span><span>STATUS</span><span>SCORE</span><span/></div>
      {loading ? <div className="empty-state">Loading scan history…</div> : filtered.length === 0 ? <div className="empty-state">No scan records found.</div> : filtered.map(item => <div className="history-table" key={item.id}><span>{item.id}</span><strong>{item.product}</strong><span>{item.date ? new Date(item.date).toLocaleDateString() : '—'}</span><StatusBadge status={item.status as any}/><span>{item.score}%</span><button className="icon-button" onClick={() => navigate('/result/'+item.id)}><ArrowRight size={17}/></button></div>)}
    </section>
  </>;
}
