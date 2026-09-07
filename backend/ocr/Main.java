package ocr;

import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

    public static void main(String[] args) {

        /*
         * Load OpenCV
         */
        nu.pattern.OpenCV.loadLocally();

        System.out.println("Starting OCR pipeline...");

        String inputDirectory = "input";
        String processedDirectory = "processed";
        String outputDirectory = "output";

        File inputFolder = new File(inputDirectory);
        File processedFolder = new File(processedDirectory);
        File outputFolder = new File(outputDirectory);

        /*
         * Create folders if they don't exist.
         */
        processedFolder.mkdirs();
        outputFolder.mkdirs();

        /*
         * Find input images.
         */
        File[] images = inputFolder.listFiles(
                (dir, name) -> {

                    String lower =
                            name.toLowerCase();

                    return lower.endsWith(".jpg")
                            || lower.endsWith(".jpeg")
                            || lower.endsWith(".png")
                            || lower.endsWith(".webp");
                }
        );

        if (images == null || images.length == 0) {

            System.out.println(
                    "No images found in: "
                            + inputDirectory
            );

            return;
        }

        /*
         * Maximum 10 images.
         */
        if (images.length > 10) {

            System.out.println(
                    "Maximum 10 images allowed."
            );

            return;
        }

        /*
         * Keep image order predictable.
         */
        Arrays.sort(
                images,
                Comparator.comparing(File::getName)
        );

        System.out.println(
                "Found " + images.length + " image(s)."
        );

        System.out.println();

        OcrEngine ocrEngine =
                new OcrEngine();

        File outputFile =
                new File(
                        outputDirectory
                                + "/ocr_result.txt"
                );

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(outputFile)
                        )
        ) {

            writer.println(
                    "========== PRODUCT OCR =========="
            );

            writer.println();

            /*
             * Process each image.
             */
            for (int i = 0; i < images.length; i++) {

                File image =
                        images[i];

                System.out.println(
                        "Processing image "
                                + (i + 1)
                                + "/"
                                + images.length
                                + ": "
                                + image.getName()
                );

                /*
                 * Preprocess
                 */
                Mat processed =
                        ImagePreprocessor.preprocess(
                                image.getPath()
                        );

                /*
                 * Save processed image.
                 */
                String processedPath =
                        processedDirectory
                                + "/processed_"
                                + (i + 1)
                                + ".png";

                Imgcodecs.imwrite(
                        processedPath,
                        processed
                );

                /*
                 * OCR needs a File.
                 */
                String text;

                try {
                    text = ocrEngine.recognize(processedPath);

                } catch (TesseractException e) {

                    System.err.println(
                            "OCR failed for "
                                    + image.getName()
                    );

                    e.printStackTrace();

                    processed.release();

                    continue;
                }

                /*
                 * Add this image's OCR to
                 * the combined output.
                 */
                writer.println(
                        "========== IMAGE "
                                + (i + 1)
                                + ": "
                                + image.getName()
                                + " =========="
                );

                writer.println();

                writer.println(
                        text.trim()
                );

                writer.println();

                writer.println(
                        "================================"
                );

                writer.println();

                processed.release();

                System.out.println(
                        "OCR complete."
                );
            }

            System.out.println();
            System.out.println(
                    "================================"
            );
            System.out.println(
                    "OCR COMPLETE"
            );
            System.out.println(
                    "Output: "
                            + outputFile.getPath()
            );
            System.out.println(
                    "================================"
            );

        } catch (IOException e) {

            System.err.println(
                    "Could not write OCR output."
            );

            e.printStackTrace();
        }
    }
}