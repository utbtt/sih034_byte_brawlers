import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A dictionary of common packaged-food category words, mapped to a
 * normalized category label. This is deliberately broad and shallow
 * (a vocabulary list, not per-product logic) so that adding a new
 * category is a one-line addition, not a new regex engine.
 *
 * It is used for two purposes:
 *   1. Best-effort product category detection.
 *   2. Locating the line most likely to contain the product name,
 *      by finding where category vocabulary appears in the text.
 *
 * This deliberately does NOT try to identify the brand from a fixed
 * list of known brand names — see the "Remaining Limitations" notes
 * in the accompanying write-up for why brand detection from flat OCR
 * text is a fundamentally different (and harder) problem.
 */
public class CategoryKeywords {

    private static final Map<String, String> KEYWORD_TO_CATEGORY = new LinkedHashMap<>();

    static {
        put("KETCHUP", "Ketchup/Sauce");
        put("SAUCE", "Ketchup/Sauce");
        put("CHIPS", "Chips/Snacks");
        put("WAFERS", "Chips/Snacks");
        put("NAMKEEN", "Chips/Snacks");
        put("BISCUIT", "Biscuits/Cookies");
        put("COOKIE", "Biscuits/Cookies");
        put("CRACKER", "Biscuits/Cookies");
        put("NOODLE", "Noodles/Instant Food");
        put("PASTA", "Noodles/Instant Food");
        put("INSTANT", "Noodles/Instant Food");
        put("CHOCOLATE", "Chocolate/Confectionery");
        put("CANDY", "Chocolate/Confectionery");
        put("CONFECTIONERY", "Chocolate/Confectionery");
        put("JUICE", "Beverages");
        put("BEVERAGE", "Beverages");
        put("SOFT\\s*DRINK", "Beverages");
        put("SQUASH", "Beverages");
        put("CEREAL", "Cereals/Breakfast");
        put("MUESLI", "Cereals/Breakfast");
        put("OATS", "Cereals/Breakfast");
        put("SPICE", "Spices/Masala");
        put("MASALA", "Spices/Masala");
        put("MILK", "Dairy");
        put("CHEESE", "Dairy");
        put("YOGURT", "Dairy");
        put("YOGHURT", "Dairy");
        put("CURD", "Dairy");
        put("PANEER", "Dairy");
        put("FROZEN", "Frozen/Processed Food");
        put("READY\\s*TO\\s*EAT", "Frozen/Processed Food");
        put("JAM", "Spreads");
        put("SPREAD", "Spreads");
        put("PICKLE", "Pickles");
        put("PAPAD", "Snacks/Papad");
        put("HONEY", "Honey/Syrups");
        put("SYRUP", "Honey/Syrups");
    }

    private static void put(String keywordPattern, String category) {
        KEYWORD_TO_CATEGORY.put(keywordPattern, category);
    }

    /** Returns the normalized category for the first keyword hit, or null. */
    public static String detectCategory(String text) {

        for (Map.Entry<String, String> entry : KEYWORD_TO_CATEGORY.entrySet()) {

            Pattern p = Pattern.compile("(?i)\\b" + entry.getKey() + "S?\\b");
            Matcher m = p.matcher(text);

            if (m.find()) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * Finds the line most likely to contain the product name: the first
     * line (outside of an ingredients/nutrition section) that contains a
     * category keyword. Returns null if no category vocabulary is found
     * anywhere, since guessing a "product name" with zero anchor is not
     * meaningfully better than leaving the field empty.
     */
    public static String findLikelyProductNameLine(String[] lines) {

        for (String line : lines) {

            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            for (String keywordPattern : KEYWORD_TO_CATEGORY.keySet()) {

                Pattern p = Pattern.compile("(?i)\\b" + keywordPattern + "S?\\b");

                if (p.matcher(trimmed).find()) {
                    // Ingredient list lines tend to be long and comma-heavy;
                    // a genuine product-name line is short.
                    if (trimmed.length() <= 60 && countCommas(trimmed) <= 2) {
                        return trimmed;
                    }
                }
            }
        }

        return null;
    }

    private static int countCommas(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == ',') count++;
        }
        return count;
    }
}
