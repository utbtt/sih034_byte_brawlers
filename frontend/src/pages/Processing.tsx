import { useEffect, useState } from 'react';
import { ScanLine, CheckCircle2, AlertCircle } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';
import { getScan } from '../api';

export function Processing() {
  const navigate = useNavigate(); const { id = '' } = useParams(); const [error, setError] = useState(''); const [stage, setStage] = useState('OCR extraction');
  useEffect(() => {
    let cancelled = false;
    let timer: number | undefined;
    async function poll() {
      try {
        const result = await getScan(id);
        if (cancelled) return;
        if (result.status === 'completed' || (!result.status && result.verdict !== 'WARNING')) { navigate(`/result/${id}`, { replace: true }); return; }
        setStage(result.status === 'processing' ? 'Rule validation' : 'Analyzing packaging');
        timer = window.setTimeout(poll, 1500);
      } catch (e: any) { if (!cancelled) { setError(e.message || 'Could not check scan status.'); timer = window.setTimeout(poll, 2500); } }
    }
    poll(); return () => { cancelled = true; if (timer) window.clearTimeout(timer); };
  }, [id, navigate]);
  return <div className="processing-page"><div className="processing-card">
    <div className="processing-icon"><ScanLine size={42}/></div><p className="eyebrow">SCAN {id}</p><h1>Analyzing Packaging Artwork</h1><p>OCR, extraction and the statutory rule engine are running on the backend.</p>
    <div className="progress-track"><span/></div><div className="processing-steps"><div><CheckCircle2/> Image received</div><div className="current"><span className="spinner"/> {stage}</div><div>○ Compliance verdict</div></div>
    {error ? <div className="error-box"><AlertCircle size={17}/>{error}</div> : <small>Keep this page open. It will automatically show the result when processing is complete.</small>}
  </div></div>;
}
