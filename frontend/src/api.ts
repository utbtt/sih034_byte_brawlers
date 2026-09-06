import { ScanResult } from './types';

const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:3000/api/v1').replace(/\/$/, '');

export type User = {
  id?: string;
  name: string;
  email: string;
  role?: string;
  organization?: string;
};

export type DashboardStats = {
  totalScans: number;
  compliantSkus: number;
  violations: number;
  passRate?: number;
  queued?: number;
  defects?: { label: string; count: number; tone: 'red' | 'blue' | 'orange' }[];
  priorities?: { id: string; product: string; severity: string; description: string }[];
};

export type HistoryItem = {
  id: string;
  product: string;
  date: string;
  status: string;
  score: number;
};

function authHeaders() {
  const token = localStorage.getItem('metrocheck_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);
  const auth = authHeaders();
  Object.entries(auth).forEach(([key, value]) => headers.set(key, value));

  const response = await fetch(`${API_URL}${path}`, { ...options, headers });
  const contentType = response.headers.get('content-type') || '';
  const body = contentType.includes('application/json') ? await response.json() : await response.text();

  if (!response.ok) {
    const message = typeof body === 'object' && body?.message ? body.message : `Request failed (${response.status})`;
    throw new Error(message);
  }
  return body as T;
}

export async function signup(name: string, email: string, password: string) {
  const data = await request<any>('/auth/signup', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, password }),
  });
  const token = data.token || data.accessToken;
  if (!token) throw new Error('Backend signup response did not contain a token.');
  localStorage.setItem('metrocheck_token', token);
  const user = data.user || data.data?.user;
  if (user) localStorage.setItem('metrocheck_user', JSON.stringify(user));
  return user as User | undefined;
}

export async function login(email: string, password: string) {
  const data = await request<any>('/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  const token = data.token || data.accessToken;
  if (!token) throw new Error('Backend login response did not contain a token.');
  localStorage.setItem('metrocheck_token', token);
  const user = data.user || data.data?.user;
  if (user) localStorage.setItem('metrocheck_user', JSON.stringify(user));
  return user as User | undefined;
}

export function logout() {
  localStorage.removeItem('metrocheck_token');
  localStorage.removeItem('metrocheck_user');
}

export function isAuthenticated() {
  return Boolean(localStorage.getItem('metrocheck_token'));
}

export function storedUser(): User | null {
  try { return JSON.parse(localStorage.getItem('metrocheck_user') || 'null'); } catch { return null; }
}

export async function getCurrentUser() {
  const data = await request<any>('/auth/me');
  return (data.user || data.data?.user || data) as User;
}

export async function getDashboardStats() {
  const data = await request<any>('/dashboard/stats');
  return (data.data || data) as DashboardStats;
}

export async function getHistory() {
  const data = await request<any>('/scans');
  const rows = data.data || data.scans || data;
  return (Array.isArray(rows) ? rows : []).map((x: any): HistoryItem => ({
    id: x.id || x.scan_id,
    product: x.product || x.productName || x.commodity_name || 'Unnamed product',
    date: x.date || x.created_at || x.createdAt || '',
    status: x.status || x.verdict || 'PROCESSING',
    score: Number(x.score ?? x.compliance_score ?? 0),
  }));
}

export async function uploadScan(file: File) {
  const form = new FormData();
  form.append('image', file);
  const data = await request<any>('/scans/upload', { method: 'POST', body: form });
  return data.scanId || data.scan_id || data.id || data.data?.scanId;
}

export async function getScan(id: string) {
  const data = await request<any>(`/scans/${encodeURIComponent(id)}`);
  const x = data.data || data;
  return normalizeScan(x);
}

export function pdfUrl(id: string, existing?: string) {
  return existing || `${API_URL}/scans/${encodeURIComponent(id)}/download`;
}

function normalizeScan(x: any): ScanResult & { pdfUrl?: string; status?: string } {
  const checks = x.checks || x.violations || x.rules || [];
  return {
    id: x.id || x.scan_id,
    productName: x.productName || x.product_name || x.commodity_name || 'Not extracted',
    manufacturer: x.manufacturer || x.manufacturer_name || 'Not extracted',
    netQuantity: x.netQuantity || x.net_quantity || 'Not extracted',
    mrp: x.mrp || 'Not extracted',
    verdict: x.verdict || 'WARNING',
    score: Number(x.score ?? x.compliance_score ?? 0),
    checks: checks.map((c: any) => ({
      rule: c.rule || c.rule_id || 'Rule',
      title: c.title || c.name || 'Compliance check',
      status: c.status || (c.passed === false ? 'FAIL' : c.passed === true ? 'PASS' : 'WARNING'),
      evidence: c.evidence || c.reason || 'No evidence provided by backend.',
      recommendation: c.recommendation,
    })),
    pdfUrl: x.pdf_url || x.pdfUrl,
    status: x.status,
  };
}
