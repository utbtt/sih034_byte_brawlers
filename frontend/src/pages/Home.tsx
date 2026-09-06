import {
  ArrowRight,
  ScanLine,
  ShieldCheck,
  FileCheck2,
  PackageSearch,
  FileText,
  CircleCheck,
  AlertTriangle,
  Factory,
  IndianRupee,
  Scale,
  CalendarDays,
  Phone,
  ChevronDown,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

const ruleChecks = [
  {
    rule: "Rule 6(1)(a)",
    title: "Name / Common Name of Commodity",
    description: "Checks whether the name or common/generic name of the packaged commodity is declared.",
    icon: PackageSearch,
  },
  {
    rule: "Rule 6(1)(b)",
    title: "Manufacturer / Packer / Importer",
    description: "Checks for the required name and address details of the manufacturer, packer or importer.",
    icon: Factory,
  },
  {
    rule: "Rule 6(1)(c)",
    title: "Net Quantity",
    description: "Checks whether a valid net quantity and appropriate unit are declared.",
    icon: Scale,
  },
  {
    rule: "Rule 6(1)(d)",
    title: "Maximum Retail Price",
    description: "Checks whether the MRP declaration is present and includes the required tax declaration.",
    icon: IndianRupee,
  },
  {
    rule: "Rule 6(1)(e)",
    title: "List of Ingredients",
    description: "Checks for an ingredients declaration where it is applicable to the product.",
    icon: FileText,
  },
  {
    rule: "Rule 6(1)(f)",
    title: "Nutritional Information",
    description: "Checks for nutritional information where applicable to the packaged commodity.",
    icon: FileCheck2,
  },
  {
    rule: "Rule 6(1)(g)",
    title: "Manufacturing / Packing Date",
    description: "Checks whether the applicable manufacturing or packing date is declared.",
    icon: CalendarDays,
  },
  {
    rule: "Rule 6(1)(h)",
    title: "Best Before / Use By",
    description: "Checks whether the applicable expiry, best-before or use-by declaration is present.",
    icon: CircleCheck,
  },
  {
    rule: "Rule 6(1)(i)",
    title: "Customer Complaint Contact",
    description: "Checks for customer complaint/contact information such as phone or email.",
    icon: Phone,
  },
];

export function Home() {
  const navigate = useNavigate();

  return (
    <div className="landing">
      <header className="landing-nav">
        <button className="brand dark" onClick={() => navigate("/")}>
          <span className="brand-mark"><ShieldCheck size={20} /></span>
          <span><strong>MetroCheck</strong><small>LEGAL METROLOGY COMPLIANCE CHECKER</small></span>
        </button>

        <nav className="landing-links">
          <a href="#home">Home</a>

          {/* Simple navigation dropdowns: one destination per menu. */}
          <div className="nav-dropdown">
            <button className="nav-dropdown-button" type="button">
              What We Do <ChevronDown size={15} />
            </button>
            <div className="nav-dropdown-menu">
              <a href="#what-we-do">What We Do</a>
            </div>
          </div>

          <div className="nav-dropdown">
            <button className="nav-dropdown-button" type="button">
              Rules <ChevronDown size={15} />
            </button>
            <div className="nav-dropdown-menu">
              <a href="#rules">Metrology Rules</a>
            </div>
          </div>

          <a href="#about">About</a>
        </nav>

        <div className="landing-actions">
          <button className="btn secondary" onClick={() => navigate("/login")}>Login</button>
          <button className="btn secondary" onClick={() => navigate("/signup")}>Sign Up</button>
          <button className="btn dark-btn" onClick={() => navigate("/login")}>Get Started</button>
        </div>
      </header>

      <section className="hero" id="home">
        <div className="hero-badge">● LEGAL METROLOGY (PACKAGED COMMODITIES) RULES, 2011</div>
        <h1>Automate Legal Metrology Compliance for<br />Packaged Goods</h1>
        <p>
          Scan packaging artworks and labels to identify mandatory declarations,
          check statutory requirements, and generate a clear compliance verdict.
        </p>
        <button className="btn primary hero-btn" onClick={() => navigate("/login")}>
          <ScanLine size={18} /> Scan a Product <ArrowRight size={18} />
        </button>
      </section>

      {/* Quick overview of the three main steps. */}
      <section className="landing-cards" id="how">
        <div>
          <FileCheck2 size={28}/>
          <h3>Upload or Capture</h3>
          <p>Use your camera or upload a packaging image for analysis.</p>
        </div>
        <div>
          <ScanLine size={28}/>
          <h3>Automatic Analysis</h3>
          <p>Extract visible declarations and compare them against compliance checks.</p>
        </div>
        <div>
          <ShieldCheck size={28}/>
          <h3>Clear Verdict</h3>
          <p>See passed checks, violations and recommended corrective action.</p>
        </div>
      </section>

      {/* Explains what MetroCheck does from image input to compliance result. */}
      <section className="landing-section what-we-do" id="what-we-do">
        <div className="section-heading">
          <span className="section-eyebrow">WHAT WE DO</span>
          <h2>From package image to compliance decision</h2>
          <p>
            MetroCheck is designed to simplify the compliance review of packaged
            commodity labels by combining information extraction with statutory checks.
          </p>
        </div>

        <div className="what-we-do-grid">
          <div className="what-we-do-card">
            <span className="step-number">01</span>
            <PackageSearch size={24}/>
            <h3>Read the package</h3>
            <p>Product and label information is collected from the uploaded package image.</p>
          </div>
          <div className="what-we-do-card">
            <span className="step-number">02</span>
            <FileText size={24}/>
            <h3>Identify declarations</h3>
            <p>Important fields such as product name, quantity, MRP, dates and contact details are identified.</p>
          </div>
          <div className="what-we-do-card">
            <span className="step-number">03</span>
            <Scale size={24}/>
            <h3>Apply compliance checks</h3>
            <p>The extracted information is evaluated against the configured Legal Metrology compliance rules.</p>
          </div>
          <div className="what-we-do-card">
            <span className="step-number">04</span>
            <CircleCheck size={24}/>
            <h3>Generate the result</h3>
            <p>The system presents a compliance verdict, detected violations and recommendations.</p>
          </div>
        </div>
      </section>

      {/* Simple vertical list of the compliance checks currently represented in the engine. */}
      <section className="landing-section rules-section" id="rules">
        <div className="section-heading">
          <span className="section-eyebrow">METROLOGY RULES</span>
          <h2>Key declarations checked by MetroCheck</h2>
          <p>
            The following checks are represented in the current compliance engine.
            Applicability can vary depending on the commodity and applicable provisions.
          </p>
        </div>

        <div className="rules-list">
          {ruleChecks.map(({ rule, title, description, icon: Icon }) => (
            <article className="rule-card" key={rule}>
              <div className="rule-card-top">
                <span className="rule-number">{rule}</span>
                <Icon size={21}/>
              </div>
              <h3>{title}</h3>
              <p>{description}</p>
            </article>
          ))}
        </div>

        <div className="rules-note" id="rules-note">
          <AlertTriangle size={20}/>
          <div>
            <strong>Important</strong>
            <p>
              MetroCheck uses these declarations as compliance checks. The actual
              applicability of a requirement should be verified against the relevant
              commodity-specific provisions and the latest applicable legal requirements.
            </p>
          </div>
        </div>
      </section>

      <section className="landing-section about-section" id="about">
        <div className="section-heading">
          <span className="section-eyebrow">ABOUT METROCHECK</span>
          <h2>Making package compliance easier to review</h2>
          <p>
            MetroCheck provides a structured way to review packaged commodity labels,
            highlight missing declarations and make compliance findings easier to understand.
          </p>
        </div>
      </section>
    </div>
  );
}
