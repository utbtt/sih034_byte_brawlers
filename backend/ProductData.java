import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

/**
 * Deserialization target for the Compliance Engine's input JSON.
 *
 * IMPORTANT (integration note):
 * The Regex Engine's actual output (backend/regex-engine/src/main.java,
 * createJson()) is a FLAT JSON object - it does not wrap its fields inside
 * a "source" / "extracted_text" object.
 *
 * To keep ComplianceEngine.java completely unchanged (it calls
 * product.getExtracted_text().getXxx() throughout), the "extracted_text"
 * field below is annotated with @JsonUnwrapped. That tells Jackson to bind
 * the flat top-level Regex Engine fields directly into ExtractedText,
 * without requiring an "extracted_text" wrapper key in the JSON.
 *
 * "source" is NOT produced by the Regex Engine and is therefore optional.
 * It is only populated when a hand-authored/legacy-style JSON supplies it.
 * Main.java no longer assumes it is present.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductData {

    // ============================================
    // SOURCE (optional - not produced by the Regex Engine)
    // ============================================

    private Source source;

    @JsonUnwrapped
    private ExtractedText extracted_text;


    // Getters
    public Source getSource() {
        return source;
    }

    public ExtractedText getExtracted_text() {
        return extracted_text;
    }


    // ============================================
    // SOURCE CLASS
    // ============================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {

        private String type;
        private String product;


        public String getType() {
            return type;
        }

        public String getProduct() {
            return product;
        }
    }


    // ============================================
    // EXTRACTED TEXT CLASS
    //
    // Field names below match the Regex Engine's actual JSON keys
    // exactly (see backend/regex-engine/src/main.java -> createJson()).
    // ============================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExtractedText {

        private String brand;
        private String product_name;

        // Regex Engine field (not previously modeled). Not currently
        // used by any compliance rule, kept so no data is lost.
        private String product_category;

        private String ingredients;

        // Regex Engine emits nutrition_information as a plain descriptive
        // string, not a structured object. ComplianceEngine only performs
        // a null-check on this field (rule 6(1)(f)), so the String type
        // is fully compatible with the existing rule logic.
        private String nutrition_information;

        private Manufacturer manufacturer;

        private Contact contact;

        // Legacy-schema-only field (not produced by the Regex Engine).
        // Kept for backward compatibility with hand-authored fixtures.
        private List<String> claims_and_storage;

        private NetQuantity net_quantity;

        // Regex Engine's actual flat field. Not currently used by any
        // compliance rule, kept so no data is lost.
        private String fssai_license;

        // Legacy-schema-only nested object (not produced by the Regex
        // Engine). Kept for backward compatibility with hand-authored
        // fixtures; stays null when reading real Regex Engine output.
        private Fssai fssai;

        private String barcode;

        // Legacy-schema-only field (not produced by the Regex Engine).
        private String pack_declaration_text;

        private Double mrp_value;

        private String manufacturing_date;

        private String expiry_date;

        private String batch_number;

        // Regex Engine field (not previously modeled). Not currently
        // used by any compliance rule, kept so no data is lost.
        private String storage_information;

        // Legacy-schema-only field (not produced by the Regex Engine).
        private List<String> other_visible_text;


        // Getters (all previously-existing getters are unchanged)

        public String getBrand() {
            return brand;
        }

        public String getProduct_name() {
            return product_name;
        }

        public String getProduct_category() {
            return product_category;
        }

        public String getIngredients() {
            return ingredients;
        }

        public String getNutrition_information() {
            return nutrition_information;
        }

        public Manufacturer getManufacturer() {
            return manufacturer;
        }

        public Contact getContact() {
            return contact;
        }

        public List<String> getClaims_and_storage() {
            return claims_and_storage;
        }

        public NetQuantity getNet_quantity() {
            return net_quantity;
        }

        public String getFssai_license() {
            return fssai_license;
        }

        public Fssai getFssai() {
            return fssai;
        }

        public String getBarcode() {
            return barcode;
        }

        public String getPack_declaration_text() {
            return pack_declaration_text;
        }

        public Double getMrp_value() {
            return mrp_value;
        }

        public String getManufacturing_date() {
            return manufacturing_date;
        }

        public String getExpiry_date() {
            return expiry_date;
        }

        public String getBatch_number() {
            return batch_number;
        }

        public String getStorage_information() {
            return storage_information;
        }

        public List<String> getOther_visible_text() {
            return other_visible_text;
        }
    }


    // ============================================
    // MANUFACTURER (unchanged - fields already match)
    // ============================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Manufacturer {

        private String name;

        private String address;


        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }
    }


    // ============================================
    // CONTACT
    //
    // email / phone already matched the Regex Engine's field names.
    // "website" and "complaint_address" are the Regex Engine's actual
    // keys, added alongside the legacy "website_text" key so neither
    // schema loses data.
    // ============================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {

        private String email;

        private String phone;

        // Legacy-schema-only field (not produced by the Regex Engine).
        private String website_text;

        // Regex Engine's actual field.
        private String website;

        // Regex Engine's actual field. Not currently used by any
        // compliance rule, kept so no data is lost.
        private String complaint_address;


        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getWebsite_text() {
            return website_text;
        }

        public String getWebsite() {
            return website;
        }

        public String getComplaint_address() {
            return complaint_address;
        }
    }


    // ============================================
    // NET QUANTITY
    //
    // The Regex Engine writes an explicit JSON null for "value" when it
    // could not detect a net quantity (see createJson() in
    // regex-engine/src/main.java). The field here is therefore Double
    // (nullable) rather than the previous primitive double, so Jackson
    // can bind that null without throwing. getValue() still returns a
    // primitive double - identical signature/behavior to before - by
    // coalescing null to 0, which is exactly the outcome rule 6(1)(c)
    // already relies on ("<= 0" => not detected => violation).
    // ============================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetQuantity {

        private Double value;

        private String unit;


        public double getValue() {
            return value != null ? value : 0.0;
        }

        public String getUnit() {
            return unit;
        }
    }


    // ============================================
    // FSSAI (legacy-schema-only nested object; retained for
    // backward compatibility with hand-authored fixtures)
    // ============================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fssai {

        private String license_number;


        public String getLicense_number() {
            return license_number;
        }
    }
}