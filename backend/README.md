# Backend

This folder contains the backend modules for the project.

## Modules

### OCR
Handles image preprocessing, OCR, and text-region detection.

Technologies:
- Tesseract OCR
- OpenCV
- Sharp

### Compliance Engine
Handles product compliance checks and violation detection.

### Pattern Matching
Handles matching extracted OCR text against required patterns/data.

## OCR Dependencies

The OCR pipeline uses:

- Tesseract — optical character recognition
- OpenCV — image processing and region detection
- Sharp — image preprocessing

## Maven Dependencies

Java dependencies are managed through `pom.xml`.

The main Maven dependencies include:

- Tesseract4J
- Jackson Databind
- OpenCV

## Sharp

Sharp is a Node.js image-processing library used for preprocessing OCR input images.

The preprocessing script is:

`ocr/preprocess.js`

It performs image resizing, grayscale conversion, normalization, gamma adjustment, and sharpening before OCR.