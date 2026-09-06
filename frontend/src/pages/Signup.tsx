import { FormEvent, useState } from 'react';
import { ShieldCheck, ArrowRight, AlertCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth';

export function Signup() {
  const navigate = useNavigate();
  const { signUp } = useAuth();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  async function submit(e: FormEvent) {
    e.preventDefault(); setError('');
    if (password.length < 6) return setError('Password must be at least 6 characters.');
    if (password !== confirm) return setError('Passwords do not match.');
    setBusy(true);
    try { await signUp(name, email, password); navigate('/dashboard', { replace: true }); }
    catch (err: any) { setError(err.message || 'Unable to create account.'); }
    finally { setBusy(false); }
  }

  return <div className="auth-page"><div className="auth-card">
    <button className="auth-brand" onClick={() => navigate('/')}><span className="auth-logo"><ShieldCheck size={30}/></span><strong>MetroCheck</strong></button>
    <h1>Create your account</h1><p>Create an account before using scans, results and compliance records.</p>
    {error && <div className="error-box"><AlertCircle size={17}/>{error}</div>}
    <form onSubmit={submit}>
      <label>Name</label><input value={name} onChange={e=>setName(e.target.value)} placeholder="Your name" required />
      <label>Email</label><input value={email} onChange={e=>setEmail(e.target.value)} type="email" placeholder="you@company.com" required />
      <label>Password</label><input value={password} onChange={e=>setPassword(e.target.value)} type="password" placeholder="At least 6 characters" required />
      <label>Confirm password</label><input value={confirm} onChange={e=>setConfirm(e.target.value)} type="password" placeholder="Repeat password" required />
      <button className="btn dark-btn full" disabled={busy} type="submit">{busy ? 'Creating account…' : 'Create account'} <ArrowRight size={17}/></button>
    </form>
    <button className="text-button" onClick={() => navigate('/login')}>Already have an account? Login</button>
  </div></div>;
}
