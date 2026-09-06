import { UserCircle, ShieldCheck, Bell, Database } from 'lucide-react';
import { useAuth } from '../auth';

export function Profile() {
  const { user } = useAuth();
  return <><div className="page-header simple"><div><p className="eyebrow">ACCOUNT</p><h1>Profile / Settings</h1><p>Account information is loaded from your authenticated session.</p></div></div>
    <div className="settings-grid"><section className="panel settings-card"><div className="settings-icon"><UserCircle/></div><h2>{user?.name || 'User'}</h2><p>{user?.role || 'Compliance User'}</p><label>Work email</label><input value={user?.email || ''} readOnly/><label>Organization</label><input value={user?.organization || '—'} readOnly/></section>
      <section className="panel settings-card"><div className="setting-row"><ShieldCheck/><div><strong>Statutory Engine</strong><p>Backend compliance rules are used for scan decisions.</p></div><span className="toggle on">ACTIVE</span></div><div className="setting-row"><Bell/><div><strong>Compliance notifications</strong><p>Notification preferences are controlled by the backend.</p></div><span className="toggle">API</span></div><div className="setting-row"><Database/><div><strong>Scan storage</strong><p>Scan metadata and reports are stored server-side.</p></div><span className="toggle">SERVER</span></div></section></div></>;
}
