package ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.*;

public class RegionDetector {

    private static final int MIN_REGION_AREA = 50;
    private static final int MAX_REGION_AREA = 500000;
    private static final int MIN_REGION_WIDTH = 15;
    private static final int MIN_REGION_HEIGHT = 10;

    public static List<TextRegion> detectRegions(String imagePath) {

        Mat image = Imgcodecs.imread(imagePath);

        if (image.empty()) {
            System.err.println("Could not load image: " + imagePath);
            return new ArrayList<>();
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        Mat binary = new Mat();
        Imgproc.threshold(
                gray,
                binary,
                0,
                255,
                Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
        );

        Mat kernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT,
                new Size(2, 2)
        );

        Imgproc.erode(
                binary,
                binary,
                kernel,
                new Point(-1, -1),
                1
        );

        Mat labels = new Mat();
        Mat stats = new Mat();
        Mat centroids = new Mat();

        int numLabels = Imgproc.connectedComponentsWithStats(
                binary,
                labels,
                stats,
                centroids
        );

        List<TextRegion> regions = new ArrayList<>();

        for (int i = 1; i < numLabels; i++) {

            int x = (int) stats.get(i, Imgproc.CC_STAT_LEFT)[0];
            int y = (int) stats.get(i, Imgproc.CC_STAT_TOP)[0];
            int width = (int) stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
            int height = (int) stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
            int area = (int) stats.get(i, Imgproc.CC_STAT_AREA)[0];

            if (area >= MIN_REGION_AREA &&
                    area <= MAX_REGION_AREA &&
                    width >= MIN_REGION_WIDTH &&
                    height >= MIN_REGION_HEIGHT) {

                regions.add(
                        new TextRegion(x, y, width, height, area)
                );
            }
        }

        regions.sort(
                Comparator.comparingInt((TextRegion r) -> r.y)
                        .thenComparingInt(r -> r.x)
        );

        image.release();
        gray.release();
        binary.release();
        kernel.release();
        labels.release();
        stats.release();
        centroids.release();

        return regions;
    }


    // =========================================================
    // CLUSTERING
    // =========================================================

    public static List<TextRegion> clusterRegions(
            List<TextRegion> rawRegions,
            int threshold) {

        if (rawRegions.isEmpty()) {
            return new ArrayList<>();
        }

        List<TextRegion> clusters = new ArrayList<>();
        List<Boolean> merged = new ArrayList<>();

        for (int i = 0; i < rawRegions.size(); i++) {
            merged.add(false);
        }

        for (int i = 0; i < rawRegions.size(); i++) {

            if (merged.get(i)) {
                continue;
            }

            TextRegion current = rawRegions.get(i);

            int minX = current.x;
            int minY = current.y;
            int maxX = current.x + current.width;
            int maxY = current.y + current.height;

            boolean changed = true;

            while (changed) {

                changed = false;

                for (int j = 0; j < rawRegions.size(); j++) {

                    if (merged.get(j) || j == i) {
                        continue;
                    }

                    TextRegion other = rawRegions.get(j);

                    TextRegion temp = new TextRegion(
                            minX,
                            minY,
                            maxX - minX,
                            maxY - minY,
                            0
                    );

                    if (isNear(temp, other, threshold)) {

                        merged.set(j, true);

                        minX = Math.min(minX, other.x);
                        minY = Math.min(minY, other.y);
                        maxX = Math.max(
                                maxX,
                                other.x + other.width
                        );
                        maxY = Math.max(
                                maxY,
                                other.y + other.height
                        );

                        changed = true;
                    }
                }
            }

            clusters.add(
                    new TextRegion(
                            minX,
                            minY,
                            maxX - minX,
                            maxY - minY,
                            0
                    )
            );

            merged.set(i, true);
        }

        clusters.sort(
                Comparator.comparingInt((TextRegion r) -> r.y)
                        .thenComparingInt(r -> r.x)
        );

        return clusters;
    }


    private static boolean isNear(
            TextRegion r1,
            TextRegion r2,
            int threshold) {

        int x1Min = r1.x;
        int x1Max = r1.x + r1.width;
        int y1Min = r1.y;
        int y1Max = r1.y + r1.height;

        int x2Min = r2.x;
        int x2Max = r2.x + r2.width;
        int y2Min = r2.y;
        int y2Max = r2.y + r2.height;

        int hDist = Math.max(
                0,
                Math.max(x1Min - x2Max, x2Min - x1Max)
        );

        int vDist = Math.max(
                0,
                Math.max(y1Min - y2Max, y2Min - y1Max)
        );

        return hDist <= threshold && vDist <= threshold;
    }


    // =========================================================
    // OCR
    // =========================================================

    public static String ocrRegion(
            String imagePath,
            TextRegion region,
            String tessDataPath,
            String language)
            throws TesseractException {

        Mat image = Imgcodecs.imread(imagePath);

        if (image.empty()) {
            return "";
        }

        // Add padding around the region
        int padding = 15;

        int x = Math.max(0, region.x - padding);
        int y = Math.max(0, region.y - padding);

        int right = Math.min(
                image.cols(),
                region.x + region.width + padding
        );

        int bottom = Math.min(
                image.rows(),
                region.y + region.height + padding
        );

        int width = right - x;
        int height = bottom - y;

        if (width <= 0 || height <= 0) {
            image.release();
            return "";
        }

        Rect cropArea = new Rect(
                x,
                y,
                width,
                height
        );

        Mat cropped = new Mat(image, cropArea);

        // Upscale for Tesseract
        Mat enlarged = new Mat();

        Imgproc.resize(
                cropped,
                enlarged,
                new Size(
                        cropped.cols() * 2.0,
                        cropped.rows() * 2.0
                ),
                0,
                0,
                Imgproc.INTER_CUBIC
        );

        // Save temporary image
        String tempPath =
                "/tmp/ocr_region_" +
                        System.nanoTime() +
                        ".png";

        Imgcodecs.imwrite(
                tempPath,
                enlarged
        );

        // SAME TESSERACT CONFIG AS MAIN.JAVA
        Tesseract tesseract = new Tesseract();

        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage(language);

        tesseract.setPageSegMode(6);
        tesseract.setOcrEngineMode(1);

        String text =
                tesseract.doOCR(new File(tempPath));

        // Cleanup
        image.release();
        cropped.release();
        enlarged.release();

        new File(tempPath).delete();

        return text.trim();
    }


    public static List<RegionOCRResult> ocrAllRegions(
            String imagePath,
            List<TextRegion> regions,
            String tessDataPath,
            String language)
            throws TesseractException {

        List<RegionOCRResult> results =
                new ArrayList<>();

        for (int i = 0; i < regions.size(); i++) {

            TextRegion region = regions.get(i);

            String text = ocrRegion(
                    imagePath,
                    region,
                    tessDataPath,
                    language
            );

            if (!text.isEmpty()) {

                results.add(
                        new RegionOCRResult(
                                i + 1,
                                region.x,
                                region.y,
                                region.width,
                                region.height,
                                text
                        )
                );
            }

            System.out.println(
                    "Processed region " +
                            (i + 1) +
                            "/" +
                            regions.size()
            );
        }

        return results;
    }


    // =========================================================
    // DATA CLASSES
    // =========================================================

    public static class TextRegion {

        public int x;
        public int y;
        public int width;
        public int height;
        public int area;

        public TextRegion(
                int x,
                int y,
                int width,
                int height,
                int area) {

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.area = area;
        }
    }


    public static class RegionOCRResult {

        public int id;
        public int x;
        public int y;
        public int width;
        public int height;
        public String text;

        public RegionOCRResult(
                int id,
                int x,
                int y,
                int width,
                int height,
                String text) {

            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.text = text;
        }
    }
}