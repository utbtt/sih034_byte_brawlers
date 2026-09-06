import type { Verdict } from "../types";

export function StatusBadge({ status }: { status: Verdict | "PASS" | "FAIL" | "WARNING" }) {
  const cls = status.toLowerCase().replace("-", "-");
  return <span className={`status-badge ${cls}`}>{status}</span>;
}
