import java.util.regex.Pattern;

/**
 * All regex patterns used by the extraction engine, precompiled exactly
 * once as static final fields (Pattern.compile is expensive; doing it
 * per-call, per-file was the old design's main performance risk).
 *
 * Patterns are now built from FieldAliases so that adding a synonym for
 * a label (e.g. a new way of writing "Net Quantity") means editing one
 * array in FieldAliases.java, not hunting through regex literals.
 *
 * A generic numeric-with-OCR-noise fragment is reused across every
 * pattern that expects a number, so that a "0" that OCR read as "O" is
 * captured by the regex (OcrNumeric then repairs it before parsing).
 */
public class RegexPatterns {

    // Digits, but tolerant of common OCR letter/digit confusion.
    // Kept intentionally narrow (used only where a number is expected).
    private static final String NUMERIC_OCR_TOLERANT = "[0-9OoIlSBGZ]";

    private static String number(String decimals) {
        return NUMERIC_OCR_TOLERANT + "+(?:\\.[0-9OoIlSBGZ]{1," + decimals + "})?";
    }

    // =====================================================
    // INGREDIENTS HEADING
    // =====================================================

    public static final Pattern INGREDIENTS_HEADING = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.INGREDIENTS_HEADINGS) + ")\\b\\s*[:\\-]?"
    );

    // =====================================================
    // NUTRITION HEADING
    // =====================================================

    public static final Pattern NUTRITION_HEADING = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.NUTRITION_HEADINGS) + ")\\b"
    );

    // =====================================================
    // STORAGE HEADING
    // =====================================================

    public static final Pattern STORAGE_HEADING = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.STORAGE_HEADINGS) + ")\\b\\s*[:\\-]?"
    );

    // =====================================================
    // COMPLAINT / CUSTOMER CARE HEADING
    // =====================================================

    public static final Pattern COMPLAINT_HEADING = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.COMPLAINT_HEADINGS) + ")\\b\\s*[:\\-]?"
    );

    // =====================================================
    // MRP
    // =====================================================

    /* group(1) = numeric value (may still contain OCR noise chars; caller repairs it) */
    public static final Pattern MRP_VALUE = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.MRP_LABELS) + ")\\.?\\s*"
                    + "(?:₹|RS\\.?|INR|[<>|])?\\s*[:\\-]?\\s*(?:₹|RS\\.?|INR|[<>|])?\\s*(" + number("2") + ")"
    );

    // =====================================================
    // NET QUANTITY
    // =====================================================

    /* group(1) = numeric value, group(2) = unit */
    public static final Pattern NET_QUANTITY = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.NET_QUANTITY_LABELS) + ")\\.?\\s*"
                    + "[:\\-]?\\s*(" + number("3") + ")\\s*"
                    + "(kgs?|gm?s?|mgs?|l|ltr|litres?|liters?|ml)\\b"
    );

    // =====================================================
    // DATE (shared fragment)
    // =====================================================

    /*
     * Handles numeric dates (26/01/2026, 26-01-26) AND textual-month
     * dates (26 JAN 2026, JAN 2026), since real packaging uses both.
     */
    private static final String DATE_FRAGMENT =
            "(" + NUMERIC_OCR_TOLERANT + "{1,2}[./\\-]" + NUMERIC_OCR_TOLERANT + "{1,2}[./\\-]" + NUMERIC_OCR_TOLERANT + "{2,4}"
                    + "|" + NUMERIC_OCR_TOLERANT + "{1,2}[./\\-]" + NUMERIC_OCR_TOLERANT + "{2,4}"
                    + "|" + NUMERIC_OCR_TOLERANT + "{1,2}\\s*(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|SEPT|OCT|NOV|DEC)[A-Z]*\\s*" + NUMERIC_OCR_TOLERANT + "{2,4}"
                    + "|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|SEPT|OCT|NOV|DEC)[A-Z]*\\s*" + NUMERIC_OCR_TOLERANT + "{2,4})";

    // =====================================================
    // MANUFACTURING DATE
    // =====================================================

    /* group(1) = date */
    public static final Pattern MANUFACTURING_DATE = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.MFG_DATE_LABELS) + ")\\b\\.?"
                    + "\\s*(?:DATE)?\\s*[:\\-]?\\s*" + DATE_FRAGMENT
    );

    // =====================================================
    // EXPIRY DATE
    // =====================================================

    /* group(1) = date */
    public static final Pattern EXPIRY_DATE = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.EXPIRY_DATE_LABELS) + ")\\b\\.?"
                    + "\\s*(?:DATE)?\\s*[:\\-]?\\s*" + DATE_FRAGMENT
    );

    /**
     * Fallback for expiry phrased as a duration rather than an absolute
     * date, e.g. "Best Before 4 Months From Packaging". This is common
     * on Indian snack packaging. Regex cannot compute an actual date
     * from this (it would need the packing date plus arithmetic), so
     * the raw duration text is captured instead of silently returning
     * null. group(0) is used whole rather than a sub-group.
     */
    public static final Pattern EXPIRY_DURATION = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.EXPIRY_DATE_LABELS) + ")\\b\\.?\\s*"
                    + "\\d+\\s*(?:DAYS?|WEEKS?|MONTHS?|YEARS?)\\s*FROM\\s*"
                    + "(?:PACKAGING|PACKING|MFG|MANUFACTURE|DATE\\s*OF\\s*PACKING)"
    );

    // =====================================================
    // MANUFACTURER / MARKETER / PACKER / IMPORTER labels
    // (used for line-based lookup, not single-shot regex capture)
    // =====================================================

    public static final Pattern MANUFACTURER_LABEL = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.MANUFACTURER_LABELS) + ")\\s*:?"
    );

    public static final Pattern MARKETER_LABEL = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.MARKETER_LABELS) + ")\\s*:?"
    );

    public static final Pattern PACKER_LABEL = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.PACKER_LABELS) + ")\\s*:?"
    );

    public static final Pattern IMPORTER_LABEL = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.IMPORTER_LABELS) + ")\\s*:?"
    );

    // =====================================================
    // BATCH / LOT NUMBER
    // =====================================================

    /* group(1) = batch code (alphanumeric, packaging batch codes are not purely numeric) */
    public static final Pattern BATCH_NUMBER = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.BATCH_LABELS) + ")\\.?\\s*[:\\-]?\\s*"
                    + "([A-Z0-9\\-/]{3,20})"
    );

    // =====================================================
    // EMAIL
    // =====================================================

    public static final Pattern EMAIL = Pattern.compile(
            "\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b"
    );

    // =====================================================
    // PHONE
    // =====================================================

    public static final Pattern PHONE = Pattern.compile(
            "(?<!\\d)(?:\\+91[\\s\\-]?)?(?:\\d[\\s\\-]?){10,12}(?!\\d)"
    );

    // =====================================================
    // WEBSITE
    // =====================================================

    public static final Pattern WEBSITE = Pattern.compile(
            "(?i)\\b(?:https?://)?(?:www\\.)?[a-z0-9-]{2,}(?:\\.[a-z0-9-]{2,})*\\.[a-z]{2,10}\\b"
    );

    // =====================================================
    // FSSAI LICENSE
    // =====================================================

    /* group(1) = license number. FSSAI central licenses are 14 digits;
       state/temporary registrations can be shorter, so we accept both
       and let validation flag anything suspicious rather than reject it. */
    public static final Pattern FSSAI_LICENSE = Pattern.compile(
            "(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.FSSAI_LABELS) + ")\\.?\\s*[:\\-]?\\s*"
                    + "(" + NUMERIC_OCR_TOLERANT + "{8,14})"
    );

    // =====================================================
    // BARCODE (EAN-13 candidate)
    // =====================================================

    public static final Pattern BARCODE = Pattern.compile(
            "(?<!\\d)(?:\\d[\\s]*){13}(?!\\d)"
    );
}