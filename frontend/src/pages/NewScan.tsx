import { useRef, useState, DragEvent, ReactNode } from "react";
import { Camera, UploadCloud, ScanLine, Info, Maximize, Type, CircleHelp, ExternalLink, Layers3, X, ArrowRight } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { uploadScan } from "../api";

export function NewScan() {
  const inputRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState("");
  const [dragging, setDragging] = useState(false);
  const [cameraOpen, setCameraOpen] = useState(false);
  const videoRef = useRef<HTMLVideoElement>(null);
  const streamRef = useRef<MediaStream | null>(null);

  function selectFile(f?: File) {
    if (!f) return;
    if (!["image/jpeg", "image/png"].includes(f.type)) {
      alert("Please select a JPG or PNG image.");
      return;
    }
    if (f.size > 5 * 1024 * 1024) {
      alert("Maximum file size is 5 MB.");
      return;
    }
    setFile(f);
    setPreview(URL.createObjectURL(f));
  }

  function drop(e: DragEvent<HTMLDivElement>) {
    e.preventDefault();
    setDragging(false);
    selectFile(e.dataTransfer.files[0]);
  }

  async function openCamera() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: "environment" } });
      streamRef.current = stream;
      setCameraOpen(true);
      setTimeout(() => {
        if (videoRef.current) videoRef.current.srcObject = stream;
      }, 50);
    } catch {
      alert("Camera permission was denied or camera is unavailable.");
    }
  }

  function closeCamera() {
    streamRef.current?.getTracks().forEach(track => track.stop());
    streamRef.current = null;
    setCameraOpen(false);
  }

  function capture() {
    if (!videoRef.current) return;
    const canvas = document.createElement("canvas");
    canvas.width = videoRef.current.videoWidth;
    canvas.height = videoRef.current.videoHeight;
    canvas.getContext("2d")?.drawImage(videoRef.current, 0, 0);
    canvas.toBlob(blob => {
      if (blob) selectFile(new File([blob], "camera-capture.jpg", { type: "image/jpeg" }));
      closeCamera();
    }, "image/jpeg", 0.92);
  }

  async function startScan() {
    if (!file) { alert("Please upload or capture an image first."); return; }
    try {
      const scanId = await uploadScan(file);
      if (!scanId) throw new Error("Backend did not return a scan ID.");
      navigate(`/processing/${scanId}`);
    } catch (e: any) {
      alert(e.message || "Upload failed. Please check that the backend is running.");
    }
  }

  return (
    <>
      <div className="breadcrumbs">COMPLIANCE NAVIGATION <span>›</span> <b>+ NEW SCAN</b></div>
      <div className="scan-heading">
        <div><h1>New Product Compliance Scan</h1><p>Upload packaging proofs, label artwork, or take a direct high-resolution photo for sub-millimeter statutory verification under Legal Metrology Rules.</p></div>
        <div className="sync-card"><span className="green-dot"/> REGULATORY MODE<strong>PCR 2024 / LM Act Synchronized</strong>⚙</div>
      </div>

      <div className="scan-layout">
        <section>
          <div className={`upload-panel ${dragging ? "dragging" : ""}`} onDragOver={e => {e.preventDefault(); setDragging(true)}} onDragLeave={() => setDragging(false)} onDrop={drop}>
            {preview ? (
              <div className="preview-box">
                <img src={preview} alt="Selected packaging" />
                <button className="remove-preview" onClick={() => {setFile(null); setPreview("")}}><X size={18}/></button>
                <div className="preview-actions">
                  <button className="btn pale" onClick={() => inputRef.current?.click()}><UploadCloud size={17}/> Replace</button>
                  <button className="btn dark-btn" onClick={startScan}>Analyze Artwork <ArrowRight size={17}/></button>
                </div>
              </div>
            ) : (
              <>
                <div className="scan-icon"><ScanLine size={42}/></div>
                <h2>Take a photo or upload from gallery</h2>
                <p>Drag and drop your packaging artwork or front/back PDP label files<br/>directly here</p>
                <div className="format-note"><Info size={16}/> Supported formats: JPG, PNG (Max 5MB per file • High-res 300+ DPI recommended for font measurement)</div>
                <div className="upload-buttons">
                  <button className="btn pale wide" onClick={openCamera}><Camera size={19}/> Take Photo</button>
                  <button className="btn dark-btn wide" onClick={() => inputRef.current?.click()}><UploadCloud size={19}/> Upload Image</button>
                </div>
              </>
            )}
            <input ref={inputRef} type="file" accept="image/jpeg,image/png" hidden onChange={e => selectFile(e.target.files?.[0])}/>
          </div>
          <div className="asset-sync">Enterprise asset synchronization <button>Import from SKU Catalog / DAM Integration <ExternalLink size={15}/></button></div>

          <div className="staged-heading"><h2><Layers3 size={22}/> Selected Artwork Staged for Inspection</h2><span>● Ready for Compliance Analysis</span></div>
          {file && <div className="staged-card"><img src={preview} alt="Staged artwork"/><div><strong>{file.name}</strong><p>{(file.size / 1024 / 1024).toFixed(2)} MB • Ready to analyze</p></div><button className="btn dark-btn" onClick={startScan}>Start Scan</button></div>}
        </section>

        <aside className="best-practices">
          <h2>💡 Scanning Best Practices</h2><p className="small-cap">FOR HIGH PRECISION METROLOGY</p>
          <Practice icon={<Maximize/>} title="Ensure PDP is fully flat & glare-free">Principal Display Panels wrapped on curved cylinders distort aspect ratio. Capture planar flat artwork proofs whenever possible.</Practice>
          <Practice icon={<Type/>} title="Keep Unit Sale Price and MRP legible">Font size of numerals must comply with Schedule II. Blurry or pixelated anti-aliasing may flag false-positive minimum height warnings.</Practice>
          <Practice icon={<CircleHelp/>} title="Include manufacturer / packer pin code & email">Complete contact details with official jurisdiction postal pin code and designated consumer helpline email are mandatory on BOP.</Practice>
          <div className="font-standard"><p>STATUTORY FONT HEIGHT STANDARDS</p><code>Net Wt ≤ 200g/ml:   Min 2.0 mm (6 pt)<br/>Net Wt 200g–1kg:     Min 4.0 mm (12 pt)<br/>Net Wt &gt; 1kg:        Min 6.0 mm (18 pt)</code></div>
        </aside>
      </div>

      {cameraOpen && <div className="camera-modal"><div className="camera-card"><video ref={videoRef} autoPlay playsInline/><div><button className="btn pale" onClick={closeCamera}>Cancel</button><button className="btn dark-btn" onClick={capture}><Camera size={18}/> Capture</button></div></div></div>}
    </>
  );
}

function Practice({icon, title, children}: {icon: ReactNode; title: string; children: ReactNode}) {
  return <div className="practice"><span>{icon}</span><div><h3>{title}</h3><p>{children}</p></div></div>;
}
