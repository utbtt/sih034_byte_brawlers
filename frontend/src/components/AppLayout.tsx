import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { LayoutDashboard, PlusCircle, History as HistoryIcon, Settings, ShieldCheck, UserCircle, LogOut } from 'lucide-react';
import { useAuth } from '../auth';

export function AppLayout() {
  const navigate = useNavigate();
  const { user, signOut } = useAuth();
  const name = user?.name || 'User';
  const role = user?.role || 'Compliance User';

  function logout() { signOut(); navigate('/', { replace: true }); }

  return <div className="app-shell">
    <header className="topbar">
      <button className="brand" onClick={() => navigate('/dashboard')}><span className="brand-mark"><ShieldCheck size={20}/></span><span><strong>MetroCheck</strong><small>LEGAL METROLOGY COMPLIANCE CHECKER</small></span></button>
      <div className="topbar-right">
        <div className="engine-pill"><ShieldCheck size={16}/> Statutory Engine</div>
        <button className="profile-menu" onClick={() => navigate('/profile')}><span className="avatar"><UserCircle size={22}/></span><span className="profile-text"><strong>{name}</strong><small>{role}</small></span></button>
        <button className="logout-button" onClick={logout} title="Log out"><LogOut size={18}/></button>
      </div>
    </header>
    <div className="body-layout">
      <aside className="sidebar">
        <p className="sidebar-title">COMPLIANCE NAVIGATION</p>
        <nav>
          <NavLink to="/dashboard" className={({isActive}) => isActive ? 'side-link active' : 'side-link'}><LayoutDashboard size={20}/> Dashboard</NavLink>
          <NavLink to="/scan" className={({isActive}) => isActive ? 'side-link active' : 'side-link'}><PlusCircle size={20}/> New Scan</NavLink>
          <NavLink to="/history" className={({isActive}) => isActive ? 'side-link active' : 'side-link'}><HistoryIcon size={20}/> Scan History</NavLink>
          <NavLink to="/profile" className={({isActive}) => isActive ? 'side-link active' : 'side-link'}><Settings size={20}/> Profile / Settings</NavLink>
        </nav>
        <div className="rules-active"><div><span className="blue-dot"/> RULES ACTIVE</div><p>PCR 2011 & Legal Metrology Act<br/>standards synchronized.</p></div>
      </aside>
      <main className="main-content"><Outlet /></main>
    </div>
  </div>;
}
