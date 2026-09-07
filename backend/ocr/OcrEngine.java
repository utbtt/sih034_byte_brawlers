package ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class OcrEngine {

    private final ITesseract tesseract;

    public OcrEngine() {

        tesseract = new Tesseract();

        // Tesseract expects eng.traineddata inside this directory
        String tessDataPath =
                new File("src/main/resources/tessdata")
                        .getAbsolutePath();

        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("eng");

        // OCR configuration
        tesseract.setPageSegMode(6);
        tesseract.setOcrEngineMode(1);

        // Preserve spaces reasonably well
        tesseract.setVariable(
                "preserve_interword_spaces",
                "1"
        );
    }

    public String recognize(String imagePath)
            throws TesseractException {

        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            throw new IllegalArgumentException(
                    "Image does not exist: " + imagePath
            );
        }

        return tesseract.doOCR(imageFile);
    }
}