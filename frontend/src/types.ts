export type Verdict = "COMPLIANT" | "NON-COMPLIANT" | "WARNING";

export type Check = {
  rule: string;
  title: string;
  status: "PASS" | "FAIL" | "WARNING";
  evidence: string;
  recommendation?: string;
};

export type ScanResult = {
  id: string;
  productName: string;
  manufacturer: string;
  netQuantity: string;
  mrp: string;
  verdict: Verdict;
  score: number;
  checks: Check[];
};
