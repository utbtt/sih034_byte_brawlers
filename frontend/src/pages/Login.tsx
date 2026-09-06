import { FormEvent, useState } from 'react';
import { ShieldCheck, ArrowRight, AlertCircle } from 'lucide-react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth';

export function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const { signIn } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  async function submit(e: FormEvent) {
    e.preventDefault(); setError(''); setBusy(true);
    try {
      await signIn(email, password);
      const from = (location.state as any)?.from;
      navigate(from && from !== '/login' ? from : '/dashboard', { replace: true });
    } catch (err: any) { setError(err.message || 'Unable to sign in.'); }
    finally { setBusy(false); }
  }

  return <div className="auth-page">
    <div className="auth-card">
      <button className="auth-brand" onClick={() => navigate('/')}><span className="auth-logo"><ShieldCheck size={30}/></span><strong>MetroCheck</strong></button>
      <h1>Sign in to MetroCheck</h1>
      <p>Authentication is required before accessing scans, results and compliance records.</p>
      {error && <div className="error-box"><AlertCircle size={17}/>{error}</div>}
      <form onSubmit={submit}>
        <label>Email</label><input value={email} onChange={e => setEmail(e.target.value)} type="email" placeholder="you@company.com" required />
        <label>Password</label><input value={password} onChange={e => setPassword(e.target.value)} type="password" placeholder="Enter your password" required />
        <button className="btn dark-btn full" disabled={busy} type="submit">{busy ? 'Signing in…' : 'Login'} <ArrowRight size={17}/></button>
      </form>
      <button className="text-button" onClick={() => navigate('/signup')}>Create a new account</button><button className="text-button" onClick={() => navigate('/')}>← Back to home</button>
    </div>
  </div>;
}
