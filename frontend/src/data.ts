import { ScanResult } from "./types";

export const demoResult: ScanResult = {
  id: "MC-2024-01428",
  productName: "PureSpice Premium Turmeric Powder",
  manufacturer: "PureSpice Foods Pvt. Ltd.",
  netQuantity: "200 g",
  mrp: "₹148.00",
  verdict: "NON-COMPLIANT",
  score: 78,
  checks: [
    {
      rule: "Rule 6(1)(a)",
      title: "Commodity name",
      status: "PASS",
      evidence: "Product name is clearly declared on the principal display panel."
    },
    {
      rule: "Rule 6(1)(b)",
      title: "Manufacturer details",
      status: "PASS",
      evidence: "Manufacturer name and complete address are present."
    },
    {
      rule: "Rule 6(1)(c)",
      title: "Net quantity",
      status: "PASS",
      evidence: "Net quantity is declared as 200 g."
    },
    {
      rule: "Rule 6(1)(d)",
      title: "MRP inclusive of taxes",
      status: "FAIL",
      evidence: "MRP declaration is present but the required sale-price declaration is missing.",
      recommendation: "Add the required unit sale price declaration on the principal display panel."
    },
    {
      rule: "Rule 7",
      title: "Legibility / font height",
      status: "WARNING",
      evidence: "Some mandatory numerals appear below the recommended minimum height."
    },
    {
      rule: "Rule 9",
      title: "Language",
      status: "PASS",
      evidence: "Mandatory declarations are available in the required language."
    }
  ]
};

export const history = [
  { id: "MC-2024-01428", product: "PureSpice Premium Turmeric Powder 200g", date: "19 Nov 2024", status: "NON-COMPLIANT", score: 78 },
  { id: "MC-2024-01427", product: "ZestFizz Sparkling Lemonade Can 330ml", date: "18 Nov 2024", status: "WARNING", score: 91 },
  { id: "MC-2024-01426", product: "DailyFresh Basmati Rice 5kg", date: "18 Nov 2024", status: "COMPLIANT", score: 100 },
  { id: "MC-2024-01425", product: "SunHarvest Cooking Oil 1L", date: "17 Nov 2024", status: "COMPLIANT", score: 100 }
];
