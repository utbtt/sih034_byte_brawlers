package ocr;

import org.opencv.core.*;

public class OpenCVTest {
    public static void main(String[] args) {
        try {
            // Try WITHOUT System.loadLibrary() first
            System.out.println("OpenCV version: " + Core.VERSION);
            System.out.println("✓ OpenCV loaded successfully!");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("✗ Native library not found: " + e.getMessage());
            System.out.println("\nTrying manual load...");
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                System.out.println("✓ Manual load worked");
            } catch (Exception e2) {
                System.err.println("✗ Manual load failed: " + e2.getMessage());
            }
        }
    }
}