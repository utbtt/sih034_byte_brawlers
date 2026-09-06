import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import type { ReactNode } from 'react';
import { AppLayout } from './components/AppLayout';
import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { Signup } from './pages/Signup';
import { Dashboard } from './pages/Dashboard';
import { NewScan } from './pages/NewScan';
import { Processing } from './pages/Processing';
import { Result } from './pages/Result';
import { History } from './pages/History';
import { Profile } from './pages/Profile';
import { useAuth } from './auth';

function Protected({ children }: { children: ReactNode }) {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading) return <div className="route-loading">Checking secure session…</div>;
  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  return <>{children}</>;
}

function PublicOnly({ children }: { children: ReactNode }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="route-loading">Loading…</div>;
  return user ? <Navigate to="/dashboard" replace /> : <>{children}</>;
}

export default function App() {
  return <Routes>
    <Route path="/" element={<Home />} />
    <Route path="/login" element={<PublicOnly><Login /></PublicOnly>} />
    <Route path="/signup" element={<PublicOnly><Signup /></PublicOnly>} />
    <Route element={<Protected><AppLayout /></Protected>}>
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/scan" element={<NewScan />} />
      <Route path="/processing/:id" element={<Processing />} />
      <Route path="/result/:id" element={<Result />} />
      <Route path="/history" element={<History />} />
      <Route path="/profile" element={<Profile />} />
    </Route>
    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes>;
}
