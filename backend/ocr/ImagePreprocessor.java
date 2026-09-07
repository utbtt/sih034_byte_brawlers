package ocr;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImagePreprocessor {

    public static Mat preprocess(String imagePath) {

        Mat original = Imgcodecs.imread(imagePath);

        if (original.empty()) {
            throw new RuntimeException(
                    "Could not load image: " + imagePath
            );
        }

        /*
         * 1. Convert to grayscale
         */
        Mat gray = new Mat();

        Imgproc.cvtColor(
                original,
                gray,
                Imgproc.COLOR_BGR2GRAY
        );

        /*
         * 2. Slight denoising
         *
         * We don't want aggressive filtering because
         * tiny text on food labels can disappear.
         */
        Mat denoised = new Mat();

        Imgproc.GaussianBlur(
                gray,
                denoised,
                new Size(3, 3),
                0
        );

        /*
         * 3. Slight contrast enhancement
         */
        Mat enhanced = new Mat();

        Core.normalize(
                denoised,
                enhanced,
                0,
                255,
                Core.NORM_MINMAX
        );

        /*
         * 4. Mild sharpening
         *
         * This makes printed characters slightly clearer
         * without destroying thin strokes.
         */
        Mat sharpened = new Mat();

        Mat kernel = new Mat(3, 3, CvType.CV_32F);

        float[] kernelData = {
                0, -1, 0,
                -1, 5, -1,
                0, -1, 0
        };

        kernel.put(0, 0, kernelData);

        Imgproc.filter2D(
                enhanced,
                sharpened,
                -1,
                kernel
        );

        /*
         * 5. Small upscale
         *
         * Tesseract generally benefits from readable
         * text being larger.
         */
        Mat resized = new Mat();

        Imgproc.resize(
                sharpened,
                resized,
                new Size(),
                1.5,
                1.5,
                Imgproc.INTER_CUBIC
        );

        /*
         * Cleanup
         */
        original.release();
        gray.release();
        denoised.release();
        enhanced.release();
        sharpened.release();
        kernel.release();

        return resized;
    }


    public static void saveProcessed(
            Mat image,
            String outputPath
    ) {

        boolean success =
                Imgcodecs.imwrite(outputPath, image);

        if (!success) {
            throw new RuntimeException(
                    "Could not save processed image: "
                            + outputPath
            );
        }
    }
}