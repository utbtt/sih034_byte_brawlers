import { ArrowLeft, Download, AlertTriangle, CheckCircle2, XCircle, FileText } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { getScan, pdfUrl } from "../api";
import { ScanResult } from "../types";
import { useEffect, useState } from 'react';
import { StatusBadge } from "../components/StatusBadge";

export function Result() {
  const navigate = useNavigate();
  const { id = "" } = useParams();
  const [r, setR] = useState<(ScanResult & { pdfUrl?: string }) | null>(null);
  const [error, setError] = useState('');
  useEffect(() => { if (id) getScan(id).then(setR).catch((e: any) => setError(e.message || 'Could not load result.')); }, [id]);
  if (!r) return <div className="loading-panel">{error || 'Loading compliance result…'}</div>;

  return (
    <>
      <div className="result-top"><button className="text-button" onClick={() => navigate("/scan")}><ArrowLeft size={17}/> New Scan</button><span>SCAN ID: {r.id}</span></div>
      <div className="result-header">
        <div><p className="eyebrow">COMPLIANCE ANALYSIS COMPLETE</p><h1>{r.productName}</h1><p>Automated statutory verification based on the configured Legal Metrology ruleset.</p></div>
        <button className="btn dark-btn" onClick={() => window.open(pdfUrl(r.id, r.pdfUrl), "_blank", "noopener,noreferrer")}><Download size={18}/> Download PDF Report</button>
      </div>

      <section className="verdict-card">
        <div className="score-circle"><strong>{r.score}%</strong><span>SCORE</span></div>
        <div className="verdict-copy"><StatusBadge status={r.verdict}/><h2>Compliance requires corrective action</h2><p>The verdict and rule-level evidence below were returned by the backend compliance engine.</p></div>
      </section>

      <div className="result-grid">
        <section className="panel">
          <div className="panel-title"><h2>Extracted Product Information</h2></div>
          <div className="info-grid">
            <InfoItem label="Commodity name" value={r.productName}/>
            <InfoItem label="Manufacturer" value={r.manufacturer}/>
            <InfoItem label="Net quantity" value={r.netQuantity}/>
            <InfoItem label="MRP" value={r.mrp}/>
          </div>
        </section>

        <section className="panel">
          <div className="panel-title"><h2>Rule-by-Rule Verification</h2><span>{r.checks.filter(x => x.status === "PASS").length} passed</span></div>
          <div className="checks">
            {r.checks.map((c, i) => (
              <article className={`check-card ${c.status.toLowerCase()}`} key={i}>
                <div className="check-icon">{c.status === "PASS" ? <CheckCircle2/> : c.status === "FAIL" ? <XCircle/> : <AlertTriangle/>}</div>
                <div><div className="check-head"><strong>{c.rule} — {c.title}</strong><StatusBadge status={c.status}/></div><p>{c.evidence}</p>{c.recommendation && <div className="recommendation"><FileText size={16}/><b>Recommendation:</b> {c.recommendation}</div>}</div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </>
  );
}

function InfoItem({label, value}: {label: string; value: string}) {
  return <div className="info-item"><span>{label}</span><strong>{value}</strong></div>;
}
