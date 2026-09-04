import java.util.ArrayList;
import java.util.List;

public class ComplianceEngine {

    public List<Violation> check(ProductData product) {

        List<Violation> violations = new ArrayList<>();

        ProductData.ExtractedText data =
                product.getExtracted_text();

        // ---------------------------------------
        // 6(1)(a) - NAME / COMMON NAME
        // ---------------------------------------

        if (isEmpty(data.getProduct_name())) {

            violations.add(new Violation(
                    "6(1)(a)",
                    "Name/Common Name of Commodity",
                    "HIGH",
                    "Product/commodity name was not detected.",
                    "Verify that the common or generic name of the commodity is declared."
            ));
        }


        // ---------------------------------------
        // 6(1)(b) - MANUFACTURER NAME & ADDRESS
        // ---------------------------------------

        if (data.getManufacturer() == null ||
            isEmpty(data.getManufacturer().getName()) ||
            isEmpty(data.getManufacturer().getAddress())) {

            violations.add(new Violation(
                    "6(1)(b)",
                    "Manufacturer/Packer/Importer Details",
                    "HIGH",
                    "Manufacturer name and/or address was not detected.",
                    "Verify that the required name and address declaration is present."
            ));
        }


        // ---------------------------------------
        // 6(1)(c) - NET QUANTITY
        // ---------------------------------------

        if (data.getNet_quantity() == null ||
            data.getNet_quantity().getValue() <= 0 ||
            isEmpty(data.getNet_quantity().getUnit())) {

            violations.add(new Violation(
                    "6(1)(c)",
                    "Net Quantity",
                    "HIGH",
                    "Valid net quantity was not detected.",
                    "Verify that net quantity is declared with the appropriate unit."
            ));
        }


        // ---------------------------------------
        // 6(1)(d) - MRP
        // ---------------------------------------

        if (data.getMrp_value() == null ||
            data.getMrp_value() <= 0) {

            violations.add(new Violation(
                    "6(1)(d)",
                    "Maximum Retail Price",
                    "HIGH",
                    "MRP value was not detected in the extracted data.",
                    "Verify that MRP inclusive of all taxes is declared on the package."
            ));
        }


        // ---------------------------------------
        // 6(1)(e) - INGREDIENTS
        // ---------------------------------------

        if (isEmpty(data.getIngredients())) {

            violations.add(new Violation(
                    "6(1)(e)",
                    "List of Ingredients",
                    "MEDIUM",
                    "Ingredients list was not detected.",
                    "Verify whether an ingredients declaration is required and present."
            ));
        }


        // ---------------------------------------
        // 6(1)(f) - NUTRITION INFORMATION
        // ---------------------------------------

        if (data.getNutrition_information() == null) {

            violations.add(new Violation(
                    "6(1)(f)",
                    "Nutritional Information",
                    "MEDIUM",
                    "Nutritional information was not detected.",
                    "Verify whether nutritional information is applicable and present."
            ));
        }


        // ---------------------------------------
        // 6(1)(g) - MANUFACTURING DATE
        // ---------------------------------------

        if (isEmpty(data.getManufacturing_date())) {

            violations.add(new Violation(
                    "6(1)(g)",
                    "Manufacturing Date",
                    "HIGH",
                    "Manufacturing date was not detected.",
                    "Verify that manufacturing/packing date in the required format is declared."
            ));
        }


        // ---------------------------------------
        // 6(1)(h) - BEST BEFORE / USE BY
        // ---------------------------------------

        if (isEmpty(data.getExpiry_date())) {

            violations.add(new Violation(
                    "6(1)(h)",
                    "Best Before / Use By Date",
                    "HIGH",
                    "Expiry/best-before/use-by information was not detected.",
                    "Verify that the applicable date declaration is present."
            ));
        }


        // ---------------------------------------
        // 6(1)(i) - CUSTOMER COMPLAINT DEPARTMENT
        // ---------------------------------------

        if (data.getContact() == null ||
            (isEmpty(data.getContact().getEmail()) &&
             isEmpty(data.getContact().getPhone()))) {

            violations.add(new Violation(
                    "6(1)(i)",
                    "Customer Complaint Contact",
                    "MEDIUM",
                    "No customer complaint contact information was detected.",
                    "Verify that the required customer complaint/contact declaration is present."
            ));
        }


        // ---------------------------------------
        // 6(1)(j) - NON-STANDARD PACKAGE MARKING
        // ---------------------------------------

        /*
         * The current JSON does not provide enough information
         * to determine whether the package is a non-standard
         * quantity/package.
         *
         * Therefore, we do NOT automatically mark this as
         * a violation.
         */

        return violations;
    }


    // ---------------------------------------
    // HELPER METHOD
    // ---------------------------------------

    private boolean isEmpty(String value) {

        return value == null ||
               value.trim().isEmpty();
    }
}