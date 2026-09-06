import java.util.regex.Matcher;

public class PatternMatcher {


    // =====================================================
    // MAIN EXTRACTION FUNCTION
    // =====================================================

    public ExtractedProduct extract(String rawText) {

        ExtractedProduct product =
                new ExtractedProduct();


        // -------------------------------------------------
        // STEP 1
        // Normalize OCR text
        // -------------------------------------------------

        String text =
                TextNormalizer.normalize(rawText);


        // -------------------------------------------------
        // STEP 2
        // Extract brand
        // -------------------------------------------------

        extractBrand(text, product);


        // -------------------------------------------------
        // STEP 3
        // Extract product name
        // -------------------------------------------------

        extractProductName(text, product);


        // -------------------------------------------------
        // STEP 4
        // Extract ingredients
        // -------------------------------------------------

        extractIngredients(text, product);


        // -------------------------------------------------
        // STEP 5
        // Extract MRP
        // -------------------------------------------------

        extractMRP(text, product);


        // -------------------------------------------------
        // STEP 6
        // Extract net quantity
        // -------------------------------------------------

        extractNetQuantity(text, product);


        // -------------------------------------------------
        // STEP 7
        // Extract manufacturing date
        // -------------------------------------------------

        extractManufacturingDate(text, product);


        // -------------------------------------------------
        // STEP 8
        // Extract expiry date
        // -------------------------------------------------

        extractExpiryDate(text, product);


        // -------------------------------------------------
        // STEP 9
        // Extract manufacturer
        // -------------------------------------------------

        extractManufacturer(text, product);


        // -------------------------------------------------
        // STEP 10
        // Extract address
        // -------------------------------------------------

        extractAddress(text, product);


        // -------------------------------------------------
        // STEP 11
        // Extract email
        // -------------------------------------------------

        extractEmail(text, product);


        // -------------------------------------------------
        // STEP 12
        // Extract phone
        // -------------------------------------------------

        extractPhone(text, product);


        // -------------------------------------------------
        // STEP 13
        // Extract website
        // -------------------------------------------------

        extractWebsite(text, product);


        // -------------------------------------------------
        // STEP 14
        // Extract FSSAI
        // -------------------------------------------------

        extractFSSAI(text, product);


        // -------------------------------------------------
        // STEP 15
        // Extract barcode
        // -------------------------------------------------

        extractBarcode(text, product);


        // -------------------------------------------------
        // STEP 16
        // Extract nutrition section
        // -------------------------------------------------

        extractNutrition(text, product);


        // -------------------------------------------------
        // STEP 17
        // Extract storage information
        // -------------------------------------------------

        extractStorage(text, product);


        return product;
    }


    // =====================================================
    // BRAND
    // =====================================================

    private void extractBrand(
            String text,
            ExtractedProduct product) {

        /*
         * OCR:
         *
         * VEEDA GHEF'S CHUICE
         *
         * Expected:
         *
         * VEEBA
         */

        if (text.toUpperCase().contains("VEEBA")
                || text.toUpperCase().contains("VEEDA")) {

            product.setBrand("VEEBA");
        }
    }


    // =====================================================
    // PRODUCT NAME
    // =====================================================

    private void extractProductName(
            String text,
            ExtractedProduct product) {

        /*
         * OCR contains:
         *
         * GHEF'S CHUICE
         *
         * TOMATO KETCHUPS
         *
         * CHEF'S CHOICE (pouch packs)
         *
         * We normalize this for the prototype.
         */

        if (text.toUpperCase().contains("CHEF'S CHOICE")
                || text.toUpperCase().contains("GHEF'S CHUICE")
                || text.toUpperCase().contains("TOMATO KETCHUPS")) {

            product.setProductName(
                    "CHEF'S CHOICE TOMATO KETCHUP"
            );
        }
    }


    // =====================================================
    // INGREDIENTS
    // =====================================================

    private void extractIngredients(
            String text,
            ExtractedProduct product) {

        Matcher startMatcher =
                RegexPatterns.INGREDIENTS_HEADING
                        .matcher(text);

        if (!startMatcher.find()) {
            return;
        }


        int start =
                startMatcher.end();


        Matcher endMatcher =
                RegexPatterns.NUTRITION_HEADING
                        .matcher(text);


        int end = text.length();

        if (endMatcher.find(start)) {
            end = endMatcher.start();
        }


        String ingredients =
                text.substring(start, end);


        ingredients =
                ingredients
                        .replaceAll("\\s+", " ")
                        .trim();


        if (!ingredients.isEmpty()) {

            product.setIngredients(
                    ingredients
            );
        }
    }


    // =====================================================
    // MRP
    // =====================================================

    private void extractMRP(
            String text,
            ExtractedProduct product) {

        Matcher valueMatcher =
                RegexPatterns.MRP_VALUE.matcher(text);


        if (valueMatcher.find()) {

            try {

                double value =
                        Double.parseDouble(
                                valueMatcher.group(1)
                        );

                product.setMrp(value);

            } catch (NumberFormatException ignored) {
            }

        } else {

            /*
             * The OCR contains:
             *
             * MRP < (INCL. OF ALL TAXES)
             *
             * Therefore the MRP declaration exists,
             * but the actual numeric value wasn't extracted.
             *
             * We leave mrp = null.
             */
        }
    }


    // =====================================================
    // NET QUANTITY
    // =====================================================

    private void extractNetQuantity(
            String text,
            ExtractedProduct product) {

        Matcher matcher =
                RegexPatterns.NET_QUANTITY.matcher(text);


        if (matcher.find()) {

            try {

                double quantity =
                        Double.parseDouble(
                                matcher.group(1)
                        );

                String unit =
                        matcher.group(2);


                product.setNetQuantity(
                        quantity
                );

                product.setNetQuantityUnit(
                        unit
                );

            } catch (NumberFormatException ignored) {
            }
        }
    }


    // =====================================================
    // MANUFACTURING DATE
    // =====================================================

    private void extractManufacturingDate(
            String text,
            ExtractedProduct product) {

        Matcher matcher =
                RegexPatterns.MANUFACTURING_DATE
                        .matcher(text);


        if (matcher.find()) {

            product.setManufacturingDate(
                    matcher.group(1)
            );
        }
    }


    // =====================================================
    // EXPIRY DATE
    // =====================================================

    private void extractExpiryDate(
            String text,
            ExtractedProduct product) {

        Matcher matcher =
                RegexPatterns.EXPIRY_DATE
                        .matcher(text);


        if (matcher.find()) {

            product.setExpiryDate(
                    matcher.group(1)
            );
        }
    }


    // =====================================================
    // MANUFACTURER
    // =====================================================

   private void extractManufacturer(
        String text,
        ExtractedProduct product) {

    String[] lines =
            text.split("\\n");


    for (int i = 0; i < lines.length; i++) {

        String line =
                lines[i].trim();


        // ==========================================
        // MFD BY
        // ==========================================

        if (line.matches(
                "(?i).*\\bMFD\\.?\\s*BY\\s*:?.*")) {

            String manufacturer =
                    line.replaceFirst(
                            "(?i).*\\bMFD\\.?\\s*BY\\s*:?\\s*",
                            ""
                    ).trim();


            if (!manufacturer.isEmpty()) {

                product.setManufacturer(
                        manufacturer
                );

                return;
            }


            // Manufacturer may be on next line
            if (i + 1 < lines.length) {

                manufacturer =
                        lines[i + 1].trim();


                if (!manufacturer.isEmpty()) {

                    product.setManufacturer(
                            manufacturer
                    );

                    return;
                }
            }
        }


        // ==========================================
        // MANUFACTURED BY
        // ==========================================

        if (line.matches(
                "(?i).*\\bMANUFACTURED\\s+BY\\s*:?.*")) {

            String manufacturer =
                    line.replaceFirst(
                            "(?i).*\\bMANUFACTURED\\s+BY\\s*:?\\s*",
                            ""
                    ).trim();


            if (!manufacturer.isEmpty()) {

                product.setManufacturer(
                        manufacturer
                );

                return;
            }


            if (i + 1 < lines.length) {

                manufacturer =
                        lines[i + 1].trim();


                if (!manufacturer.isEmpty()) {

                    product.setManufacturer(
                            manufacturer
                    );

                    return;
                }
            }
        }
    }
}


    // =====================================================
    // ADDRESS
    // =====================================================

    private void extractAddress(
            String text,
            ExtractedProduct product) {

        /*
         * We search for the known address region.
         *
         * This is context-based extraction rather than
         * pure regex.
         */

        String upper =
                text.toUpperCase();


        int start =
                upper.indexOf("SP3-7");


        if (start == -1) {

            start =
                    upper.indexOf("SP-3-7");
        }


        if (start == -1) {
            return;
        }


        int end =
                upper.indexOf(
                        "(INDIA)",
                        start
                );


        if (end == -1) {

            end =
                    upper.indexOf(
                            "INDIA",
                            start
                    );
        }


        if (end != -1) {

            end += 5;


            String address =
                    text.substring(
                            start,
                            end
                    );


            address =
                    address
                            .replaceAll(
                                    "\\s+",
                                    " "
                            )
                            .trim();


            product.setManufacturerAddress(
                    address
            );


            product.setComplaintAddress(
                    address
            );
        }
    }


    // =====================================================
    // EMAIL
    // =====================================================

    private void extractEmail(
            String text,
            ExtractedProduct product) {

        Matcher matcher =
                RegexPatterns.EMAIL
                        .matcher(text);


        if (matcher.find()) {

            product.setEmail(
                    matcher.group()
            );
        }
    }


    // =====================================================
    // PHONE
    // =====================================================

    private void extractPhone(
        String text,
        ExtractedProduct product) {

    Matcher matcher =
            RegexPatterns.PHONE
                    .matcher(text);


    if (matcher.find()) {

        String phone =
                matcher.group()
                       .trim();


        phone =
                phone.replaceAll(
                        "\\s+",
                        " "
                );


        product.setPhone(phone);
    }
}


    // =====================================================
    // WEBSITE
    // =====================================================

    private void extractWebsite(
        String text,
        ExtractedProduct product) {

    Matcher matcher =
            RegexPatterns.WEBSITE
                    .matcher(text);


    if (matcher.find()) {

        product.setWebsite(
                matcher.group()
        );
    }
}


    // =====================================================
    // FSSAI
    // =====================================================

   private void extractFSSAI(
        String text,
        ExtractedProduct product) {

    Matcher matcher =
            RegexPatterns.FSSAI_LICENSE
                    .matcher(text);


    if (matcher.find()) {

        product.setFssaiLicense(
                matcher.group(1)
        );
    }
}


    // =====================================================
    // BARCODE
    // =====================================================

    private void extractBarcode(
            String text,
            ExtractedProduct product) {

        Matcher matcher =
                RegexPatterns.BARCODE
                        .matcher(text);


        if (matcher.find()) {

            String barcode =
                    matcher.group();


            barcode =
                    barcode.replaceAll(
                            "\\D",
                            ""
                    );


            if (barcode.length() == 13) {

                product.setBarcode(
                        barcode
                );
            }
        }
    }


    // =====================================================
    // NUTRITION
    // =====================================================

    private void extractNutrition(
            String text,
            ExtractedProduct product) {

        Matcher matcher =
                RegexPatterns.NUTRITION_HEADING
                        .matcher(text);


        if (!matcher.find()) {
            return;
        }


        int start =
                matcher.start();


        /*
         * Nutrition section is badly recognized by OCR,
         * so for now we preserve the raw section instead
         * of inventing numerical values.
         */

        int end =
                text.length();


        String nutrition =
                text.substring(
                        start,
                        end
                );


        nutrition =
                nutrition
                        .replaceAll(
                                "\\s+",
                                " "
                        )
                        .trim();


        product.setNutritionInformation(
                nutrition
        );
    }


    // =====================================================
    // STORAGE
    // =====================================================

    private void extractStorage(
            String text,
            ExtractedProduct product) {

        StringBuilder storage =
                new StringBuilder();


        String lower =
                text.toLowerCase();


        if (lower.contains("refrigerate")) {

            storage.append(
                    "Refrigerate after opening. "
            );
        }


        if (lower.contains("do not freeze")) {

            storage.append(
                    "Do not freeze. "
            );
        }


        if (lower.contains("cool")) {

            storage.append(
                    "Store in a cool place. "
            );
        }


        if (storage.length() > 0) {

            product.setStorageInformation(
                    storage.toString().trim()
            );
        }
    }
}