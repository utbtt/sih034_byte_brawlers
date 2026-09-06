import type { ReactNode } from "react";

export function StatCard({ label, value, note, tone = "blue", icon }: {
  label: string; value: string; note: string; tone?: "blue" | "green" | "red"; icon: ReactNode;
}) {
  return (
    <div className="stat-card">
      <div className="stat-head">
        <span>{label}</span>
        <div className={`stat-icon ${tone}`}>{icon}</div>
      </div>
      <strong className={tone === "red" ? "red-text" : ""}>{value}</strong>
      <p className={`stat-note ${tone}`}>{note}</p>
      <div className="stat-line"><span className={tone} /></div>
    </div>
  );
}
