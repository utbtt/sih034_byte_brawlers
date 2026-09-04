import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {

            // Step 1: Create ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Step 2: Read the JSON file
            File jsonFile = new File("sample.json");

            // Step 3: Convert JSON into ProductData object
            ProductData product =
                 mapper.readValue(jsonFile, ProductData.class);

            // Step 4: Create Compliance Engine
            ComplianceEngine engine =
                    new ComplianceEngine();

            // Step 5: Check the product against the rules
            List<Violation> violations =
                    engine.check(product);


            // ============================================
            // DISPLAY PRODUCT INFORMATION
            // ============================================

            System.out.println("==============================================");
            System.out.println("     LEGAL METROLOGY COMPLIANCE CHECK");
            System.out.println("==============================================");

            System.out.println();

            System.out.println("Product: "
                    + product.getSource().getProduct());

            System.out.println();


            // ============================================
            // DISPLAY COMPLIANCE RESULT
            // ============================================

            if (violations.isEmpty()) {

                System.out.println("STATUS: COMPLIANT");
                System.out.println();
                System.out.println(
                        "No violations were detected."
                );

            } else {

                System.out.println("STATUS: NON-COMPLIANT");
                System.out.println();

                System.out.println(
                        "Total Violations: "
                        + violations.size()
                );

                System.out.println();


                // ========================================
                // DISPLAY EACH VIOLATION
                // ========================================

                System.out.println("----------------------------------------------");
                System.out.println("VIOLATIONS");
                System.out.println("----------------------------------------------");

                for (Violation violation : violations) {

                    System.out.println();

                    System.out.println(
                            "Rule ID: "
                            + violation.getRuleId()
                    );

                    System.out.println(
                            "Title: "
                            + violation.getTitle()
                    );

                    System.out.println(
                            "Severity: "
                            + violation.getSeverity()
                    );

                    System.out.println(
                            "Evidence: "
                            + violation.getEvidence()
                    );

                    System.out.println(
                            "Recommendation: "
                            + violation.getRecommendation()
                    );

                    System.out.println("----------------------------------------------");
                }
            }


        } catch (Exception e) {

            System.out.println(
                    "Error while processing the compliance check."
            );

            e.printStackTrace();
        }
    }
}