import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { getCurrentUser, isAuthenticated, login as apiLogin, signup as apiSignup, logout as apiLogout, storedUser, User } from './api';

const AuthContext = createContext<any>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(storedUser());
  const [loading, setLoading] = useState(isAuthenticated());

  useEffect(() => {
    if (!isAuthenticated()) { setLoading(false); return; }
    getCurrentUser().then(setUser).catch(() => { apiLogout(); setUser(null); }).finally(() => setLoading(false));
  }, []);

  async function signUp(name: string, email: string, password: string) {
    const newUser = await apiSignup(name, email, password);
    setUser(newUser || storedUser());
  }

  async function signIn(email: string, password: string) {
    const loggedInUser = await apiLogin(email, password);
    setUser(loggedInUser || storedUser());
  }

  function signOut() { apiLogout(); setUser(null); }

  return <AuthContext.Provider value={{ user, loading, signIn, signUp, signOut }}>{children}</AuthContext.Provider>;
}

export function useAuth() { return useContext(AuthContext); }
