import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class main {

    public static void main(String args[]) {

        System.out.println(
                "================================================="
        );

        System.out.println(
                "        LEGAL METROLOGY REGEX ENGINE"
        );

        System.out.println(
                "================================================="
        );


        try {

            // =================================================
            // STEP 1: READ OCR FILE
            // =================================================

            Path inputPath =
                    Path.of(
                            "input",
                            "ocr-output.txt"
                    );


            if (!Files.exists(inputPath)) {

                System.out.println(
                        "\nERROR: OCR input file not found."
                );

                System.out.println(
                        "Expected location:"
                );

                System.out.println(
                        inputPath.toAbsolutePath()
                );

                return;
            }


            String ocrText =
                    Files.readString(
                            inputPath
                    );


            System.out.println(
                    "\nOCR file loaded successfully."
            );


            // =================================================
            // STEP 2: CREATE PATTERN MATCHER
            // =================================================

            PatternMatcher matcher =
                    new PatternMatcher();


            // =================================================
            // STEP 3: EXTRACT PRODUCT DATA
            // =================================================

            ExtractedProduct product =
                    matcher.extract(
                            ocrText
                    );


            // =================================================
            // STEP 4: DISPLAY RESULTS
            // =================================================

            System.out.println(
                    "\n================================================="
            );

            System.out.println(
                    "             EXTRACTED INFORMATION"
            );

            System.out.println(
                    "================================================="
            );


            printField(
                    "Brand",
                    product.getBrand()
            );


            printField(
                    "Product Name",
                    product.getProductName()
            );


            printField(
                    "Product Category",
                    product.getProductCategory()
            );


            printField(
                    "Batch Number",
                    product.getBatchNumber()
            );


            printField(
                    "Ingredients",
                    product.getIngredients()
            );


            if (product.getNetQuantity() != null) {

                printField(
                        "Net Quantity",
                        product.getNetQuantity()
                                + " "
                                + product.getNetQuantityUnit()
                );

            } else {

                printField(
                        "Net Quantity",
                        "NOT DETECTED"
                );
            }


            if (product.getMrp() != null) {

                printField(
                        "MRP",
                        "₹" + product.getMrp()
                );

            } else {

                printField(
                        "MRP",
                        "VALUE NOT DETECTED"
                );
            }


            printField(
                    "Manufacturing Date",
                    product.getManufacturingDate()
            );


            printField(
                    "Expiry Date",
                    product.getExpiryDate()
            );


            printField(
                    "Manufacturer",
                    product.getManufacturer()
            );


            printField(
                    "Manufacturer Address",
                    product.getManufacturerAddress()
            );


            printField(
                    "Complaint Address",
                    product.getComplaintAddress()
            );


            printField(
                    "Email",
                    product.getEmail()
            );


            printField(
                    "Phone",
                    product.getPhone()
            );


            printField(
                    "Website",
                    product.getWebsite()
            );


            printField(
                    "FSSAI License",
                    product.getFssaiLicense()
            );


            printField(
                    "Barcode",
                    product.getBarcode()
            );


            printField(
                    "Storage",
                    product.getStorageInformation()
            );


            // =================================================
            // STEP 5: CREATE JSON
            // =================================================

            String json =
                    createJson(product);


            Path outputPath =
                    Path.of(
                            "output",
                            "extracted-product.json"
                    );


            Files.createDirectories(
                    outputPath.getParent()
            );


            Files.writeString(
                    outputPath,
                    json
            );


            System.out.println(
                    "\n================================================="
            );

            System.out.println(
                    "JSON output created successfully."
            );

            System.out.println(
                    "Location: "
                    + outputPath.toAbsolutePath()
            );

            System.out.println(
                    "================================================="
            );


        } catch (IOException e) {

            System.out.println(
                    "\nERROR while reading/writing files."
            );

            e.printStackTrace();

        } catch (Exception e) {

            System.out.println(
                    "\nUnexpected error."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // PRINT FIELD
    // =====================================================

    private static void printField(
            String field,
            Object value) {

        if (value == null) {

            value = "NOT DETECTED";
        }


        System.out.println(
                "\n" + field + ":"
        );

        System.out.println(
                value
        );
    }


    // =====================================================
    // JSON CREATION
    // =====================================================

    private static String createJson(
            ExtractedProduct product) {

        StringBuilder json =
                new StringBuilder();


        json.append("{\n");


        appendJsonString(
                json,
                "brand",
                product.getBrand(),
                true
        );


        appendJsonString(
                json,
                "product_name",
                product.getProductName(),
                true
        );


        appendJsonString(
                json,
                "product_category",
                product.getProductCategory(),
                true
        );


        appendJsonString(
                json,
                "batch_number",
                product.getBatchNumber(),
                true
        );


        appendJsonString(
                json,
                "ingredients",
                product.getIngredients(),
                true
        );


        // -------------------------------------------------
        // Net Quantity
        // -------------------------------------------------

        json.append(
                "  \"net_quantity\": {\n"
        );


        if (product.getNetQuantity() != null) {

            json.append(
                    "    \"value\": "
            );

            json.append(
                    product.getNetQuantity()
            );

            json.append(",\n");

            json.append(
                    "    \"unit\": "
            );

            json.append(
                    quote(
                            product.getNetQuantityUnit()
                    )
            );

            json.append("\n");

        } else {

            json.append(
                    "    \"value\": null,\n"
            );

            json.append(
                    "    \"unit\": null\n"
            );
        }


        json.append(
                "  },\n"
        );


        // -------------------------------------------------
        // MRP
        // -------------------------------------------------

        json.append(
                "  \"mrp_value\": "
        );


        if (product.getMrp() != null) {

            json.append(
                    product.getMrp()
            );

        } else {

            json.append(
                    "null"
            );
        }


        json.append(",\n");


        // -------------------------------------------------
        // Dates
        // -------------------------------------------------

        appendJsonString(
                json,
                "manufacturing_date",
                product.getManufacturingDate(),
                true
        );


        appendJsonString(
                json,
                "expiry_date",
                product.getExpiryDate(),
                true
        );


        // -------------------------------------------------
        // Manufacturer
        // -------------------------------------------------

        json.append(
                "  \"manufacturer\": {\n"
        );


        json.append(
                "    \"name\": "
        );

        json.append(
                quote(
                        product.getManufacturer()
                )
        );

        json.append(",\n");


        json.append(
                "    \"address\": "
        );

        json.append(
                quote(
                        product.getManufacturerAddress()
                )
        );

        json.append("\n");


        json.append(
                "  },\n"
        );


        // -------------------------------------------------
        // Contact
        // -------------------------------------------------

        json.append(
                "  \"contact\": {\n"
        );


        json.append(
                "    \"complaint_address\": "
        );

        json.append(
                quote(
                        product.getComplaintAddress()
                )
        );

        json.append(",\n");


        json.append(
                "    \"email\": "
        );

        json.append(
                quote(
                        product.getEmail()
                )
        );

        json.append(",\n");


        json.append(
                "    \"phone\": "
        );

        json.append(
                quote(
                        product.getPhone()
                )
        );

        json.append(",\n");


        json.append(
                "    \"website\": "
        );

        json.append(
                quote(
                        product.getWebsite()
                )
        );


        json.append("\n");

        json.append(
                "  },\n"
        );


        // -------------------------------------------------
        // FSSAI
        // -------------------------------------------------

        json.append(
                "  \"fssai_license\": "
        );

        json.append(
                quote(
                        product.getFssaiLicense()
                )
        );

        json.append(",\n");


        // -------------------------------------------------
        // Barcode
        // -------------------------------------------------

        json.append(
                "  \"barcode\": "
        );

        json.append(
                quote(
                        product.getBarcode()
                )
        );

        json.append(",\n");


        // -------------------------------------------------
        // Nutrition
        // -------------------------------------------------

        json.append(
                "  \"nutrition_information\": "
        );

        json.append(
                quote(
                        product.getNutritionInformation()
                )
        );

        json.append(",\n");


        // -------------------------------------------------
        // Storage
        // -------------------------------------------------

        json.append(
                "  \"storage_information\": "
        );

        json.append(
                quote(
                        product.getStorageInformation()
                )
        );


        json.append("\n");


        json.append("}");


        return json.toString();
    }


    // =====================================================
    // JSON STRING
    // =====================================================

    private static void appendJsonString(
            StringBuilder json,
            String key,
            String value,
            boolean comma) {

        json.append(
                "  \""
        );

        json.append(key);

        json.append(
                "\": "
        );

        json.append(
                quote(value)
        );


        if (comma) {

            json.append(",");
        }


        json.append("\n");
    }


    // =====================================================
    // QUOTE / ESCAPE JSON
    // =====================================================

    private static String quote(
            String value) {

        if (value == null) {

            return "null";
        }


        value =
                value.replace(
                        "\\",
                        "\\\\"
                );


        value =
                value.replace(
                        "\"",
                        "\\\""
                );


        value =
                value.replace(
                        "\n",
                        "\\n"
                );


        value =
                value.replace(
                        "\r",
                        "\\r"
                );


        return "\"" + value + "\"";
    }
}
