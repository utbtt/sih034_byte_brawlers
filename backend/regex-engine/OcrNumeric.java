import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for recovering numeric values from OCR text that has
 * digit/letter confusion (O/0, I/1, l/1, S/5, B/8, G/6, Z/2).
 *
 * IMPORTANT: this is only ever applied to a token that regex has
 * already isolated as "this should be a number" (e.g. group(1) of
 * an MRP or date pattern). It is never applied to free-flowing
 * text, because blindly swapping letters for digits across prose
 * or ingredient names would corrupt real words (e.g. "SALT",
 * "OIL"). Scope discipline is what makes this safe.
 */
public class OcrNumeric {

    private static final Pattern NON_NUMERIC_LEFTOVER =
            Pattern.compile("[^0-9.]");

    /**
     * Attempts to repair a token that regex captured as a probable
     * number but that may contain OCR letter/digit confusion.
     * Returns null if, even after repair, the token still isn't a
     * clean number (better to report "not detected" than to guess).
     */
    public static String repairDigits(String token) {

        if (token == null) {
            return null;
        }

        String repaired = token
                .replace('O', '0')
                .replace('o', '0')
                .replace('I', '1')
                .replace('l', '1')
                .replace('S', '5')
                .replace('B', '8')
                .replace('G', '6')
                .replace('Z', '2');

        return repaired;
    }

    /**
     * Parses a numeric token, first trying it as-is (clean OCR),
     * then falling back to digit-repair. Returns null rather than
     * throwing/guessing if no reliable numeric value can be formed.
     */
    public static Double parseDouble(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        String cleaned = token.trim();

        Double direct = tryParse(cleaned);
        if (direct != null) {
            return direct;
        }

        String repaired = repairDigits(cleaned);
        Double fromRepair = tryParse(repaired);
        if (fromRepair != null) {
            return fromRepair;
        }

        return null;
    }

    private static Double tryParse(String value) {

        if (value == null) {
            return null;
        }

        Matcher leftover = NON_NUMERIC_LEFTOVER.matcher(value);
        if (leftover.find()) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}