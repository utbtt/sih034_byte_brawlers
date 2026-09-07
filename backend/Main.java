import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Main {

    // ================================================================
    // DEFAULT INPUT PATH
    //
    // The Regex Engine (backend/regex-engine) writes its output to
    // "output/extracted-product.json", relative to the directory it is
    // run from. Since regex-engine/ and compliance-engine/ are sibling
    // folders under backend/, that resolves - by default, when this
    // program is run from backend/compliance-engine - to the relative
    // path below. This is only a fallback: pass the real path
    // explicitly as a command-line argument whenever you know it
    // (e.g. when an orchestrator script runs both engines back to
    // back). No machine-specific absolute paths are used anywhere.
    // ================================================================

    private static final String DEFAULT_INPUT_PATH =
            "../regex-engine/output/extracted-product.json";


    public static void main(String[] args) {

        // Step 1: Resolve the input JSON path.
        //
        // Production usage:
        //   java Main path/to/regex-engine/output/extracted-product.json
        //
        // Falls back to DEFAULT_INPUT_PATH only if no argument is given,
        // e.g. for quick manual/regression runs against a hand-placed
        // test fixture such as sample.json.

        String inputPathArg =
                (args.length > 0) ? args[0] : DEFAULT_INPUT_PATH;

        File jsonFile = new File(inputPathArg);


        // Step 2: Verify the file actually exists before attempting
        // to parse it, so the failure is clear instead of an obscure
        // stack trace.

        if (!jsonFile.exists()) {

            System.out.println(
                    "==============================================");
            System.out.println(
                    "     LEGAL METROLOGY COMPLIANCE CHECK");
            System.out.println(
                    "==============================================");
            System.out.println();
            System.out.println(
                    "ERROR: Regex Engine output JSON not found.");
            System.out.println();
            System.out.println(
                    "Expected location:");
            System.out.println(
                    jsonFile.getAbsolutePath());
            System.out.println();
            System.out.println(
                    "Run the Regex Engine first so it produces this "
                    + "file, or pass the correct path explicitly:");
            System.out.println(
                    "    java Main <path-to-extracted-product.json>");

            return;
        }


        try {

            // Step 3: Create ObjectMapper.
            ObjectMapper mapper = new ObjectMapper();

            // Step 4: Convert JSON into a ProductData object. The
            // Regex Engine's flat JSON binds directly into ProductData
            // via @JsonUnwrapped on ProductData.extracted_text - see
            // ProductData.java for details.
            ProductData product =
                    mapper.readValue(jsonFile, ProductData.class);

            if (product.getExtracted_text() == null) {

                System.out.println(
                        "ERROR: The JSON file was read but did not "
                        + "contain any recognizable product fields.");
                System.out.println(
                        "File: " + jsonFile.getAbsolutePath());

                return;
            }

            // Step 5: Create Compliance Engine.
            ComplianceEngine engine =
                    new ComplianceEngine();

            // Step 6: Check the product against the rules.
            List<Violation> violations =
                    engine.check(product);


            // ============================================
            // DISPLAY PRODUCT INFORMATION
            // ============================================

            System.out.println("==============================================");
            System.out.println("     LEGAL METROLOGY COMPLIANCE CHECK");
            System.out.println("==============================================");

            System.out.println();

            System.out.println("Input file: " + jsonFile.getAbsolutePath());

            System.out.println();

            // "source" is a legacy/optional field the Regex Engine does
            // not produce, so this display falls back to the product
            // name from extracted_text whenever source is absent.
            String displayName =
                    (product.getSource() != null
                            && product.getSource().getProduct() != null)
                            ? product.getSource().getProduct()
                            : product.getExtracted_text().getProduct_name();

            System.out.println(
                    "Product: "
                    + (displayName != null ? displayName : "UNKNOWN"));

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


        } catch (JsonParseException | JsonMappingException e) {

            // Malformed JSON, or a value that doesn't match the
            // expected shape (e.g. a string where a number was
            // expected). This is distinct from a missing optional
            // field, which Jackson simply leaves null.

            System.out.println(
                    "ERROR: The input JSON is invalid or does not "
                    + "match the expected structure.");
            System.out.println(
                    "File: " + jsonFile.getAbsolutePath());
            System.out.println(
                    "Details: " + e.getOriginalMessage());

        } catch (FileNotFoundException e) {

            System.out.println(
                    "ERROR: Regex Engine output JSON not found.");
            System.out.println(
                    "File: " + jsonFile.getAbsolutePath());

        } catch (Exception e) {

            System.out.println(
                    "Error while processing the compliance check."
            );

            e.printStackTrace();
        }
    }
}