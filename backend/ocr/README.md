# OCR Engine

## Purpose

This folder contains the OCR subsystem of the SIH 2026 project.

The OCR subsystem is responsible for:

1. receiving product/package images
2. preprocessing images for better OCR accuracy
3. extracting text from images using Tesseract OCR
4. providing OCR text to downstream modules such as region detection, regex/pattern matching, and the compliance engine

The OCR subsystem should remain modular so that preprocessing, OCR extraction, and downstream compliance processing can be developed and tested independently.

---

## Folder Layout

```text
backend/
├── app/
│   └── ...
│
├── compliance-engine/
│   └── ...
│
├── ocr/
│   ├── README.md
│   │
│   ├── ImagePreprocessor.java
│   ├── Main.java
│   ├── OcrEngine.java
│   │
│   ├── OpenCVTest.java
│   ├── RegionDetector.java
│   └── RegionDetectorTest.java
│
├── regex-engine/
│   └── ...
│
├── tests/
│   └── ...
│
└── pom.xml


