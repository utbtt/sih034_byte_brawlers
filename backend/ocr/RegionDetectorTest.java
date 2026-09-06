package ocr;

import nu.pattern.OpenCV;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RegionDetectorTest {

    public static void main(String[] args) {

        OpenCV.loadLocally();

        String detectionImage =
                "src/ocr/input/preprocessed.png";

        String ocrImage =
                "src/ocr/input/test.png";

        String tessDataPath =
                "/opt/homebrew/share/tessdata";

        String language =
                "eng";


        // ==========================================
        // STEP 1: REGION DETECTION
        // ==========================================

        System.out.println(
                "================================="
        );

        System.out.println(
                "STEP 1: REGION DETECTION"
        );

        System.out.println(
                "================================="
        );

        List<RegionDetector.TextRegion> rawRegions =
                RegionDetector.detectRegions(
                        detectionImage
                );

        System.out.println(
                "Raw regions: " +
                        rawRegions.size()
        );


        // ==========================================
        // STEP 2: CLUSTERING
        // ==========================================

        System.out.println();

        System.out.println(
                "================================="
        );

        System.out.println(
                "STEP 2: CLUSTERING"
        );

        System.out.println(
                "================================="
        );

        List<RegionDetector.TextRegion> clustered =
                RegionDetector.clusterRegions(
                        rawRegions,
                        35
                );

        System.out.println(
                "Clustered regions: " +
                        clustered.size()
        );


        // ==========================================
        // STEP 3: OCR
        // ==========================================

        System.out.println();

        System.out.println(
                "================================="
        );

        System.out.println(
                "STEP 3: OCR"
        );

        System.out.println(
                "================================="
        );

        try {

            List<RegionDetector.RegionOCRResult> results =
                    RegionDetector.ocrAllRegions(
                            ocrImage,
                            clustered,
                            tessDataPath,
                            language
                    );


            // ==========================================
            // STEP 4: SPLIT INTO COLUMNS
            // ==========================================

            List<RegionDetector.RegionOCRResult> leftColumn =
                    new ArrayList<>();

            List<RegionDetector.RegionOCRResult> rightColumn =
                    new ArrayList<>();


            for (RegionDetector.RegionOCRResult result :
                    results) {

                if (result.text == null ||
                        result.text.trim().isEmpty()) {
                    continue;
                }

                // Image width is about 2690px.
                // 1345 = halfway point.
                int centerX =
                        result.x + (result.width / 2);

                if (centerX < 1345) {
                    leftColumn.add(result);
                } else {
                    rightColumn.add(result);
                }
            }


            // ==========================================
            // STEP 5: SORT EACH COLUMN TOP -> BOTTOM
            // ==========================================

            Comparator<RegionDetector.RegionOCRResult> topToBottom =
                    Comparator.comparingInt(
                            (RegionDetector.RegionOCRResult r) -> r.y
                    );

            leftColumn.sort(topToBottom);
            rightColumn.sort(topToBottom);


            // ==========================================
            // STEP 6: BUILD FINAL OCR TEXT
            // ==========================================

            StringBuilder fullText =
                    new StringBuilder();


            // LEFT COLUMN
            for (RegionDetector.RegionOCRResult result :
                    leftColumn) {

                fullText.append(
                        result.text.trim()
                );

                fullText.append("\n");
            }


            // RIGHT COLUMN
            for (RegionDetector.RegionOCRResult result :
                    rightColumn) {

                fullText.append(
                        result.text.trim()
                );

                fullText.append("\n");
            }


            // ==========================================
            // FINAL OUTPUT
            // ==========================================

            System.out.println();

            System.out.println(
                    "================================="
            );

            System.out.println(
                    "OCR OUTPUT"
            );

            System.out.println(
                    "================================="
            );

            System.out.println();

            System.out.println(
                    fullText.toString().trim()
            );

            System.out.println();

            System.out.println(
                    "================================="
            );

            System.out.println(
                    "END OCR OUTPUT"
            );

            System.out.println(
                    "================================="
            );

            System.out.println();

            System.out.println(
                    "OCR regions containing text: " +
                            (leftColumn.size() +
                                    rightColumn.size())
            );


        } catch (Exception e) {

            System.out.println(
                    "OCR failed:"
            );

            e.printStackTrace();
        }
    }
}