import java.util.regex.Pattern;

public class RegexPatterns {


    // =====================================================
    // INGREDIENTS HEADING
    // =====================================================

    /*
     * Matches:
     *
     * INGREDIENTS:
     * INGREDIENT:
     * GREDIENTS:
     *
     * "GREDIENTS" handles OCR missing the first letter.
     */

    public static final Pattern INGREDIENTS_HEADING =
            Pattern.compile(
                    "(?i)\\b(?:INGREDIENTS|INGREDIENT|GREDIENTS)\\b\\s*[:\\-]?"
            );


    // =====================================================
    // NUTRITION HEADING
    // =====================================================

    /*
     * Matches:
     *
     * NUTRITIONAL INFORMATION
     * NUTRITION INFORMATION
     * NUTRITION FACTS
     * UTRITIONAL INFORMATION
     *
     * "UTRITIONAL" handles OCR missing N.
     */

    public static final Pattern NUTRITION_HEADING =
            Pattern.compile(
                    "(?i)\\b(?:NUTRITIONAL|NUTRITION|UTRITIONAL)\\s+(?:INFORMATION|FACTS?)\\b"
            );


    // =====================================================
    // MRP
    // =====================================================

    /*
     * Matches examples such as:
     *
     * MRP ₹ 20
     * MRP Rs. 20
     * MRP: 20
     * M.R.P. 20
     *
     * group(1) = numeric value
     */

    public static final Pattern MRP_VALUE =
            Pattern.compile(
                    "(?i)\\b(?:MRP|M\\.R\\.P)\\s*(?:₹|RS\\.?|INR)?\\s*[:\\-]?\\s*(\\d+(?:\\.\\d{1,2})?)"
            );


    // =====================================================
    // NET QUANTITY
    // =====================================================

    /*
     * Matches:
     *
     * Net Quantity: 100 g
     * Net Qty 100g
     * Net Weight: 50 g
     * Net: 500 ml
     */

    public static final Pattern NET_QUANTITY =
            Pattern.compile(
                    "(?i)\\b(?:NET\\s*(?:QTY|QUANTITY|WT|WEIGHT)|NET)\\s*[:\\-]?\\s*(\\d+(?:\\.\\d+)?)\\s*(kg|kgs|g|gm|gms|mg|l|ltr|litre|liter|ml)"
            );


    // =====================================================
    // MANUFACTURING DATE
    // =====================================================

    /*
     * Matches:
     *
     * MFD: 26/01/2026
     * MFD 26-01-2026
     * MFG: 01/2026
     * MANUFACTURED: 26/01/2026
     *
     * group(1) = date
     */

    public static final Pattern MANUFACTURING_DATE =
            Pattern.compile(
                    "(?i)\\b(?:MFD|MFG|MANUFACTURED|MANUFACTURING|PKD|PACKED)\\s*(?:DATE)?\\s*[:\\-]?\\s*(\\d{1,2}[./\\-]\\d{1,2}[./\\-]\\d{2,4}|\\d{1,2}[./\\-]\\d{2,4})"
            );


    // =====================================================
    // EXPIRY DATE
    // =====================================================

    /*
     * Matches:
     *
     * EXP: 25/05/2026
     * EXPIRY: 25-05-2026
     * USE BY: 25/05/2026
     * BEST BEFORE: 25/05/2026
     *
     * group(1) = date
     */

    public static final Pattern EXPIRY_DATE =
            Pattern.compile(
                    "(?i)\\b(?:EXP|EXPIRY|EXPIRES|USE\\s*BY|BEST\\s*BEFORE)\\s*(?:DATE)?\\s*[:\\-]?\\s*(\\d{1,2}[./\\-]\\d{1,2}[./\\-]\\d{2,4}|\\d{1,2}[./\\-]\\d{2,4})"
            );


    // =====================================================
    // MANUFACTURER / MFD BY
    // =====================================================

    public static final Pattern MANUFACTURER =
            Pattern.compile(
                    "(?i)\\b(?:MFD\\.?\\s*BY|MANUFACTURED\\s*BY|MANUFACTURER)\\s*:?"
            );


    // =====================================================
    // MARKETED BY
    // =====================================================

    public static final Pattern MARKETED_BY =
            Pattern.compile(
                    "(?i)\\bMARKETED\\s*BY\\s*:?"
            );


    // =====================================================
    // PACKED BY
    // =====================================================

    public static final Pattern PACKED_BY =
            Pattern.compile(
                    "(?i)\\bPACKED\\s*BY\\s*:?"
            );


    // =====================================================
    // EMAIL
    // =====================================================

    public static final Pattern EMAIL =
            Pattern.compile(
                    "\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b"
            );


    // =====================================================
    // PHONE
    // =====================================================

    /*
     * Handles:
     *
     * 0124-4653250
     * 1800 22 4020
     * 1800224020
     * +91 9876543210
     */

    public static final Pattern PHONE =
            Pattern.compile(
                    "(?<!\\d)(?:\\+91[\\s\\-]?)?(?:\\d[\\s\\-]?){10,12}(?!\\d)"
            );


    // =====================================================
    // WEBSITE
    // =====================================================

    public static final Pattern WEBSITE =
            Pattern.compile(
                    "(?i)\\b(?:https?://)?(?:www\\.)?[a-z0-9-]+(?:\\.[a-z0-9-]+)+\\b"
            );


    // =====================================================
    // FSSAI LICENSE
    // =====================================================

    /*
     * FSSAI licenses are generally 14 digits.
     *
     * Matches:
     *
     * Lic. No. 10012063000110
     * Lic No 10014064000435
     *
     * group(1) = license number
     */

    public static final Pattern FSSAI_LICENSE =
            Pattern.compile(
                    "(?i)\\b(?:LIC\\.?\\s*NO\\.?|LICENSE\\s*NO\\.?|LICENCE\\s*NO\\.?)\\s*[:\\-]?\\s*(\\d{14})"
            );


    // =====================================================
    // BARCODE
    // =====================================================

    /*
     * Handles spaces between barcode digits.
     *
     * Example:
     *
     * 8 901491 366052
     *
     * becomes:
     *
     * 8901491366052
     */

    public static final Pattern BARCODE =
            Pattern.compile(
                    "(?<!\\d)(?:\\d[\\s]*){13}(?!\\d)"
            );


    // =====================================================
    // COMPLAINT / CUSTOMER CARE
    // =====================================================

    public static final Pattern COMPLAINT =
            Pattern.compile(
                    "(?i)\\b(?:FEEDBACK|QUERIES|COMPLAINT|CONSUMER\\s+SERVICES|CONSUMER\\s+CARE|CUSTOMER\\s+CARE)\\b"
            );


    // =====================================================
    // STORAGE
    // =====================================================

    public static final Pattern STORAGE =
            Pattern.compile(
                    "(?i)\\b(?:STORE|STORAGE|REFRIGERATE|REFRIGERATION|KEEP|DO\\s+NOT\\s+FREEZE|COOL\\s+AND\\s+DRY)\\b"
            );
}
