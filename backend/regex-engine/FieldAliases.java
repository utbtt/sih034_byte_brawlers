/**
 * Centralized synonym/alias lists for field labels that manufacturers
 * phrase differently on different packaging.
 *
 * This is the "single source of truth" for wording variation, so that
 * adding a new synonym (e.g. a new way of writing "Net Quantity") is a
 * one-line change here instead of a hunt through multiple regexes.
 *
 * Each array is a set of regex ALTERNATIVES (not full patterns) meant
 * to be joined with "|" inside a larger pattern by RegexPatterns.
 */
public class FieldAliases {

    public static final String[] NET_QUANTITY_LABELS = {
            "NET\\s*QTY", "NET\\s*QUANTITY", "NET\\s*WT\\.?", "NET\\s*WEIGHT",
            "NET\\s*CONTENTS?", "CONTENTS", "WEIGHT", "NET"
    };

    public static final String[] MRP_LABELS = {
            "M\\.?R\\.?P\\.?", "MAXIMUM\\s*RETAIL\\s*PRICE", "PRICE"
    };

    public static final String[] MFG_DATE_LABELS = {
            "MFD\\.?", "MFG\\.?", "MANUFACTURED", "MANUFACTURING",
            "PKD\\.?", "PACKED", "PACKAGING\\s*DATE", "DATE\\s*OF\\s*MANUFACTURE",
            "DATE\\s*OF\\s*PACKING", "PACKED\\s*ON", "MANUFACTURED\\s*ON"
    };

    public static final String[] EXPIRY_DATE_LABELS = {
            "EXP\\.?", "EXPIRY", "EXPIRES", "USE\\s*BY", "BEST\\s*BEFORE",
            "BEST\\s*BY", "BB\\.?", "DATE\\s*OF\\s*EXPIRY"
    };

    public static final String[] MANUFACTURER_LABELS = {
            "MFD\\.?\\s*BY", "MANUFACTURED\\s*BY", "MANUFACTURER",
            "MFR\\.?", "MFD\\s*&\\s*MKTD\\s*BY", "MKTD\\s*&\\s*MFD\\s*BY"
    };

    public static final String[] MARKETER_LABELS = {
            "MARKETED\\s*BY", "MARKETER", "MKTD\\.?\\s*BY"
    };

    public static final String[] PACKER_LABELS = {
            "PACKED\\s*BY", "PACKER"
    };

    public static final String[] IMPORTER_LABELS = {
            "IMPORTED\\s*BY", "IMPORTER"
    };

    public static final String[] BATCH_LABELS = {
            "BATCH\\s*NO\\.?", "BATCH\\s*NUMBER", "B\\.?\\s*NO\\.?", "LOT\\s*NO\\.?", "LOT\\s*NUMBER"
    };

    public static final String[] INGREDIENTS_HEADINGS = {
            "INGREDIENTS?", "GREDIENTS", "COMPOSITION", "CONTAINS"
    };

    public static final String[] NUTRITION_HEADINGS = {
            "NUTRITION(?:AL)?\\s*(?:INFORMATION|FACTS?|VALUE)", "UTRITION(?:AL)?\\s*(?:INFORMATION|FACTS?)",
            "APPROXIMATE\\s*NUTRITION(?:AL)?\\s*(?:INFORMATION|VALUE)"
    };

    public static final String[] STORAGE_HEADINGS = {
            "STORAGE\\s*(?:INSTRUCTIONS?|CONDITIONS?)?", "STORE\\s*IN", "STORAGE\\s*INFORMATION",
            "KEEP\\s*(?:IN|AWAY)", "REFRIGERATE", "DO\\s*NOT\\s*FREEZE"
    };

    public static final String[] COMPLAINT_HEADINGS = {
            "CUSTOMER\\s*CARE", "CONSUMER\\s*CARE", "CONSUMER\\s*SERVICES?", "FEEDBACK",
            "QUERIES", "COMPLAINTS?", "FOR\\s*ANY\\s*QUERY"
    };

    public static final String[] FSSAI_LABELS = {
            "LIC\\.?\\s*NO\\.?", "LICENSE\\s*NO\\.?", "LICENCE\\s*NO\\.?",
            "FSSAI\\s*LIC\\.?\\s*NO\\.?", "FSSAI\\s*REG\\.?\\s*NO\\.?"
    };

    /** Any of these headings/labels signal "a new section/field has begun" for bounded slicing. */
    public static final String[] ALL_SECTION_STOPS = concat(
            INGREDIENTS_HEADINGS, NUTRITION_HEADINGS, STORAGE_HEADINGS, COMPLAINT_HEADINGS,
            MANUFACTURER_LABELS, MARKETER_LABELS, PACKER_LABELS, IMPORTER_LABELS,
            BATCH_LABELS, MFG_DATE_LABELS, EXPIRY_DATE_LABELS, MRP_LABELS, NET_QUANTITY_LABELS,
            FSSAI_LABELS
    );

    /**
     * Same as ALL_SECTION_STOPS but WITHOUT storage-heading synonyms.
     * Used when slicing the storage section itself: phrases like
     * "DO NOT FREEZE" or "REFRIGERATE" are storage-instruction CONTENT,
     * not a signal that a different field has started, so they must not
     * truncate the section they're part of.
     */
    public static final String[] STORAGE_SECTION_STOPS = concat(
            INGREDIENTS_HEADINGS, NUTRITION_HEADINGS, COMPLAINT_HEADINGS,
            MANUFACTURER_LABELS, MARKETER_LABELS, PACKER_LABELS, IMPORTER_LABELS,
            BATCH_LABELS, MFG_DATE_LABELS, EXPIRY_DATE_LABELS, MRP_LABELS, NET_QUANTITY_LABELS,
            FSSAI_LABELS
    );

    public static String[] concat(String[]... arrays) {
        int total = 0;
        for (String[] a : arrays) total += a.length;
        String[] result = new String[total];
        int pos = 0;
        for (String[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    public static String alternation(String[] labels) {
        return String.join("|", labels);
    }
}
