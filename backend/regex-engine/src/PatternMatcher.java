import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Field-oriented, product-agnostic extraction engine.
 *
 * Design principles (see accompanying write-up for the full rationale):
 *   - No product name, brand string, or address is hardcoded anywhere.
 *   - Every label lookup goes through FieldAliases (synonym dictionaries)
 *     instead of a single hardcoded phrase.
 *   - Section-style fields (ingredients/nutrition/storage) share one
 *     bounded-slicing implementation (SectionExtractor) instead of each
 *     re-inventing "grab everything after heading X".
 *   - Numeric fields are parsed through OcrNumeric, which tolerates
 *     letter/digit OCR confusion instead of silently failing to parse.
 *   - All Pattern objects are precompiled once in RegexPatterns.
 *   - Extraction never throws on missing fields; a missing field is
 *     just left null (already the existing JSON contract's convention).
 */
public class PatternMatcher {

    private static final SectionExtractor SECTION_EXTRACTOR =
            new SectionExtractor(FieldAliases.ALL_SECTION_STOPS);

    private static final SectionExtractor STORAGE_EXTRACTOR =
            new SectionExtractor(FieldAliases.STORAGE_SECTION_STOPS);

    // =====================================================
    // MAIN EXTRACTION FUNCTION
    // =====================================================

    public ExtractedProduct extract(String rawText) {

        ExtractedProduct product = new ExtractedProduct();

        String text = TextNormalizer.normalize(rawText);

        if (text.isEmpty()) {
            // Nothing to extract from. Return an all-null product
            // instead of throwing — an empty/garbage OCR file must
            // never crash the pipeline.
            return product;
        }

        String[] lines = text.split("\\n");

        extractProductIdentity(text, lines, product);
        extractIngredients(text, product);
        extractMRP(text, product);
        extractNetQuantity(text, product);
        extractManufacturingDate(text, product);
        extractExpiryDate(text, product);
        extractManufacturerBlock(lines, product);
        extractComplaintAddress(text, lines, product);
        extractBatchNumber(text, product);
        extractFSSAI(text, product);

        // Barcode is extracted before phone/email so its digit span
        // can be excluded from phone matching (a 13-digit barcode and
        // a 10-12 digit phone number are both "a run of digits" and
        // will otherwise collide).
        String textWithoutBarcode = extractBarcode(text, product);

        extractEmail(text, product);
        extractPhone(textWithoutBarcode, product);
        extractWebsite(text, product);

        extractNutrition(text, product);
        extractStorage(text, product);

        return product;
    }


    // =====================================================
    // PRODUCT IDENTITY (category / product name / brand)
    // =====================================================

    /*
     * There is no reliable way to identify a "brand" or "product name"
     * from flat OCR text using regex alone — those are visual/layout
     * concepts (biggest text on the pack) that get lost once OCR
     * flattens everything into plain lines. What we CAN do reliably is
     * recognize category vocabulary ("KETCHUP", "CHIPS", "NOODLES", ...)
     * and use it as an anchor. See Step 8 (Remaining Limitations) in
     * the write-up for the honest version of this tradeoff.
     */
    private void extractProductIdentity(
            String text,
            String[] lines,
            ExtractedProduct product) {

        // Restrict the search to the "header zone" — everything before
        // the ingredients/nutrition section starts. Ingredient lists are
        // full of words like "syrup", "milk", "spice" that collide with
        // category vocabulary; scanning the whole document turns those
        // into false-positive product-name/category anchors.
        String[] headerLines = headerZone(lines);
        String headerText = String.join("\n", headerLines);

        String category = CategoryKeywords.detectCategory(headerText);
        if (category != null) {
            product.setProductCategory(category);
        }

        String productNameLine = CategoryKeywords.findLikelyProductNameLine(headerLines);
        if (productNameLine != null) {
            product.setProductName(productNameLine);
        }

        if (productNameLine == null) {
            return;
        }

        // Brand heuristic: the short, label-free line immediately above
        // the detected product-name line is frequently the brand on
        // Indian packaged-food labels (brand sits above the variant/
        // product description). This is a heuristic, not a certainty —
        // it is intentionally NOT applied if no clean candidate exists.
        for (int i = 0; i < headerLines.length; i++) {

            if (!headerLines[i].trim().equals(productNameLine)) {
                continue;
            }

            if (i == 0) {
                return;
            }

            String candidate = headerLines[i - 1].trim();

            if (isPlausibleBrandLine(candidate)) {
                product.setBrand(candidate);
            }

            return;
        }
    }

    /** Lines before the first ingredients/nutrition heading, or all lines if neither appears. */
    private String[] headerZone(String[] lines) {

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].trim();

            if (RegexPatterns.INGREDIENTS_HEADING.matcher(line).find()
                    || RegexPatterns.NUTRITION_HEADING.matcher(line).find()) {

                String[] zone = new String[i];
                System.arraycopy(lines, 0, zone, 0, i);
                return zone;
            }
        }

        return lines;
    }

    private boolean isPlausibleBrandLine(String candidate) {

        if (candidate.isEmpty() || candidate.length() > 30) {
            return false;
        }

        // Reject lines that are actually a field label (e.g. "MFD BY"
        // sitting directly above a product line by coincidence).
        Matcher stopMatcher =
                Pattern.compile("(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.ALL_SECTION_STOPS) + ")\\b")
                        .matcher(candidate);

        if (stopMatcher.find()) {
            return false;
        }

        // Reject lines that are mostly digits (barcodes, dates, prices).
        long digitCount = candidate.chars().filter(Character::isDigit).count();
        if (digitCount > candidate.length() / 2.0) {
            return false;
        }

        return true;
    }


    // =====================================================
    // INGREDIENTS
    // =====================================================

    private void extractIngredients(String text, ExtractedProduct product) {

        String ingredients = SECTION_EXTRACTOR.extract(text, RegexPatterns.INGREDIENTS_HEADING);

        if (ingredients != null) {
            product.setIngredients(ingredients);
        }
    }


    // =====================================================
    // MRP
    // =====================================================

    private void extractMRP(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.MRP_VALUE.matcher(text);

        if (!matcher.find()) {
            return;
        }

        Double value = OcrNumeric.parseDouble(matcher.group(1));

        // Sanity bound: an MRP of 0 or an absurdly large number is more
        // likely a mis-match than a real price. Reject rather than
        // report a value we don't trust.
        if (value != null && value > 0 && value < 100000) {
            product.setMrp(value);
        }
    }


    // =====================================================
    // NET QUANTITY
    // =====================================================

    private void extractNetQuantity(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.NET_QUANTITY.matcher(text);

        if (!matcher.find()) {
            return;
        }

        Double quantity = OcrNumeric.parseDouble(matcher.group(1));

        if (quantity == null || quantity <= 0) {
            return;
        }

        product.setNetQuantity(quantity);
        product.setNetQuantityUnit(normalizeUnit(matcher.group(2)));
    }

    private String normalizeUnit(String rawUnit) {

        String unit = rawUnit.toLowerCase();

        if (unit.startsWith("kg")) return "kg";
        if (unit.startsWith("mg")) return "mg";
        if (unit.startsWith("g")) return "g";
        if (unit.equals("l") || unit.startsWith("ltr") || unit.startsWith("litre") || unit.startsWith("liter")) return "l";
        if (unit.startsWith("ml")) return "ml";

        return unit;
    }


    // =====================================================
    // MANUFACTURING DATE
    // =====================================================

    private void extractManufacturingDate(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.MANUFACTURING_DATE.matcher(text);

        if (matcher.find()) {
            String repaired = OcrNumeric.repairDigits(matcher.group(1).trim());
            product.setManufacturingDate(repaired);
        }
    }


    // =====================================================
    // EXPIRY DATE
    // =====================================================

    private void extractExpiryDate(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.EXPIRY_DATE.matcher(text);

        if (matcher.find()) {
            String repaired = OcrNumeric.repairDigits(matcher.group(1).trim());
            product.setExpiryDate(repaired);
            return;
        }

        // Fallback: a duration-based expiry statement ("Best Before 4
        // Months From Packaging") instead of an absolute date. We can't
        // compute the real date without the packing date, so the raw
        // statement is stored as-is rather than leaving the field null.
        Matcher durationMatcher = RegexPatterns.EXPIRY_DURATION.matcher(text);

        if (durationMatcher.find()) {
            product.setExpiryDate(durationMatcher.group().trim());
        }
    }


    // =====================================================
    // MANUFACTURER / MARKETER / PACKER / IMPORTER BLOCK
    // =====================================================

    /*
     * Generalized replacement for the old hardcoded "SP3-7 ... INDIA"
     * address lookup. Finds whichever of Manufacturer / Marketer /
     * Packer / Importer actually appears (products vary — some only
     * print "Marketed by"), takes the label's value, and treats the
     * following lines (up to the next recognizable field label or a
     * blank line) as the address block.
     */
    private void extractManufacturerBlock(String[] lines, ExtractedProduct product) {

        Pattern[] labelsByPriority = {
                RegexPatterns.MANUFACTURER_LABEL,
                RegexPatterns.MARKETER_LABEL,
                RegexPatterns.PACKER_LABEL,
                RegexPatterns.IMPORTER_LABEL
        };

        for (Pattern labelPattern : labelsByPriority) {

            LabelBlock block = captureLabelBlock(lines, labelPattern);

            if (block != null) {
                product.setManufacturer(block.value);
                product.setManufacturerAddress(block.addressBlock);
                return;
            }
        }
    }

    /** value + everything text after this label, used for name+address style fields. */
    private static class LabelBlock {
        String value;
        String addressBlock;
    }

    private LabelBlock captureLabelBlock(String[] lines, Pattern labelPattern) {

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].trim();
            Matcher matcher = labelPattern.matcher(line);

            if (!matcher.find()) {
                continue;
            }

            String sameLineValue = line.substring(matcher.end()).trim();
            sameLineValue = sameLineValue.replaceFirst("^[:\\-\\s]+", "");

            StringBuilder addressBuilder = new StringBuilder();
            String value;
            int addressStartIndex;

            if (!sameLineValue.isEmpty()) {
                value = sameLineValue;
                addressStartIndex = i + 1;
            } else if (i + 1 < lines.length && !lines[i + 1].trim().isEmpty()) {
                value = lines[i + 1].trim();
                addressStartIndex = i + 2;
            } else {
                continue;
            }

            addressBuilder.append(value);

            // Collect subsequent lines as the address, stopping at the
            // next recognizable field label, a blank line, or after a
            // reasonable number of lines (an address block is short).
            int linesCollected = 0;
            for (int j = addressStartIndex; j < lines.length && linesCollected < 4; j++) {

                String nextLine = lines[j].trim();

                if (nextLine.isEmpty()) {
                    break;
                }

                if (isAnyFieldLabel(nextLine)) {
                    break;
                }

                addressBuilder.append(" ").append(nextLine);
                linesCollected++;
            }

            LabelBlock block = new LabelBlock();
            block.value = value;
            block.addressBlock = addressBuilder.toString().trim();
            return block;
        }

        return null;
    }

    private boolean isAnyFieldLabel(String line) {

        Matcher matcher =
                Pattern.compile("(?i)\\b(?:" + FieldAliases.alternation(FieldAliases.ALL_SECTION_STOPS) + ")\\b")
                        .matcher(line);

        return matcher.find();
    }


    // =====================================================
    // COMPLAINT / CUSTOMER-CARE ADDRESS
    // =====================================================

    private void extractComplaintAddress(String text, String[] lines, ExtractedProduct product) {

        LabelBlock block = captureLabelBlock(lines, RegexPatterns.COMPLAINT_HEADING);

        if (block != null) {
            product.setComplaintAddress(block.addressBlock);
            return;
        }

        // Many packages list a single address that serves both
        // manufacturing and complaints. Fall back to it rather than
        // leaving a field null that the pack does, in fact, answer.
        if (product.getManufacturerAddress() != null) {
            product.setComplaintAddress(product.getManufacturerAddress());
        }
    }


    // =====================================================
    // BATCH / LOT NUMBER
    // =====================================================

    private void extractBatchNumber(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.BATCH_NUMBER.matcher(text);

        if (matcher.find()) {
            product.setBatchNumber(matcher.group(1).trim());
        }
    }


    // =====================================================
    // EMAIL
    // =====================================================

    private void extractEmail(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.EMAIL.matcher(text);

        if (matcher.find()) {
            product.setEmail(matcher.group());
        }
    }


    // =====================================================
    // PHONE
    // =====================================================

    private void extractPhone(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.PHONE.matcher(text);

        if (matcher.find()) {

            String phone = matcher.group().trim().replaceAll("\\s+", " ");
            product.setPhone(phone);
        }
    }


    // =====================================================
    // WEBSITE
    // =====================================================

    private void extractWebsite(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.WEBSITE.matcher(text);

        if (matcher.find()) {
            product.setWebsite(matcher.group());
        }
    }


    // =====================================================
    // FSSAI
    // =====================================================

    private void extractFSSAI(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.FSSAI_LICENSE.matcher(text);

        if (matcher.find()) {

            String repaired = OcrNumeric.repairDigits(matcher.group(1));

            // FSSAI central licenses are 14 digits; state/temporary
            // registrations can be shorter. Accept 8-14 digit results,
            // but only once OCR noise has been repaired into clean
            // digits — a token that still has non-digit leftovers
            // after repair is not a trustworthy license number.
            if (repaired != null && repaired.matches("\\d{8,14}")) {
                product.setFssaiLicense(repaired);
            }
        }
    }


    // =====================================================
    // BARCODE
    // =====================================================

    /**
     * Scans ALL 13-digit candidates (not just the first) and keeps the
     * first one that passes the EAN-13 check-digit test, which is a
     * real validation rather than "13 digits in a row happened to
     * appear here" (the old code accepted any 13-digit run, which is a
     * false-positive magnet against phone numbers, license numbers,
     * and batch codes).
     *
     * @return the input text with the accepted barcode's digits blanked
     *         out, so downstream phone-number matching doesn't re-match
     *         the same digits.
     */
    private String extractBarcode(String text, ExtractedProduct product) {

        Matcher matcher = RegexPatterns.BARCODE.matcher(text);

        while (matcher.find()) {

            String candidate = matcher.group().replaceAll("\\D", "");

            if (candidate.length() == 13 && isValidEan13(candidate)) {

                product.setBarcode(candidate);

                return text.substring(0, matcher.start())
                        + " ".repeat(matcher.end() - matcher.start())
                        + text.substring(matcher.end());
            }
        }

        return text;
    }

    private boolean isValidEan13(String digits) {

        int sum = 0;

        for (int i = 0; i < 12; i++) {

            int digit = digits.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int checkDigit = (10 - (sum % 10)) % 10;

        return checkDigit == (digits.charAt(12) - '0');
    }


    // =====================================================
    // NUTRITION
    // =====================================================

    private void extractNutrition(String text, ExtractedProduct product) {

        String nutrition = SECTION_EXTRACTOR.extract(text, RegexPatterns.NUTRITION_HEADING);

        if (nutrition != null) {
            product.setNutritionInformation(nutrition);
        }
    }


    // =====================================================
    // STORAGE
    // =====================================================

    private void extractStorage(String text, ExtractedProduct product) {

        String storage = STORAGE_EXTRACTOR.extract(text, RegexPatterns.STORAGE_HEADING);

        if (storage != null) {
            product.setStorageInformation(storage);
            return;
        }

        // Fallback for packs that give a storage instruction inline
        // without a dedicated heading (e.g. a single sentence printed
        // near the bottom of the label).
        String lower = text.toLowerCase();
        StringBuilder fallback = new StringBuilder();

        if (lower.contains("refrigerate")) {
            fallback.append("Refrigerate after opening. ");
        }
        if (lower.contains("do not freeze")) {
            fallback.append("Do not freeze. ");
        }
        if (lower.contains("cool") && lower.contains("dry")) {
            fallback.append("Store in a cool, dry place. ");
        }

        if (fallback.length() > 0) {
            product.setStorageInformation(fallback.toString().trim());
        }
    }
}
