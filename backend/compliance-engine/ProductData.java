import java.util.List;

public class ProductData {

    // ============================================
    // SOURCE
    // ============================================

    private Source source;
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
    // ============================================

    public static class ExtractedText {

        private String brand;
        private String product_name;
        private String ingredients;

        private NutritionInformation nutrition_information;

        private Manufacturer manufacturer;

        private Contact contact;

        private List<String> claims_and_storage;

        private NetQuantity net_quantity;

        private Fssai fssai;

        private String barcode;

        private String pack_declaration_text;

        private Double mrp_value;

        private String manufacturing_date;

        private String expiry_date;

        private String batch_number;

        private List<String> other_visible_text;


        // Getters

        public String getBrand() {
            return brand;
        }

        public String getProduct_name() {
            return product_name;
        }

        public String getIngredients() {
            return ingredients;
        }

        public NutritionInformation getNutrition_information() {
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

        public List<String> getOther_visible_text() {
            return other_visible_text;
        }
    }


    // ============================================
    // NUTRITION INFORMATION
    // ============================================

    public static class NutritionInformation {

        private int servings_per_pack;

        private String serving_size;

        private Per100g per_100g;

        private PercentRdaPerServing percent_rda_per_serving;

        private String rda_note;


        public int getServings_per_pack() {
            return servings_per_pack;
        }

        public String getServing_size() {
            return serving_size;
        }

        public Per100g getPer_100g() {
            return per_100g;
        }

        public PercentRdaPerServing getPercent_rda_per_serving() {
            return percent_rda_per_serving;
        }

        public String getRda_note() {
            return rda_note;
        }
    }


    // ============================================
    // NUTRITION - PER 100g
    // ============================================

    public static class Per100g {

        private double energy_kcal;

        private double protein_g;

        private double carbohydrate_g;

        private double total_sugars_g;

        private double added_sugars_g;

        private double total_fat_g;

        private double sodium_mg;


        public double getEnergy_kcal() {
            return energy_kcal;
        }

        public double getProtein_g() {
            return protein_g;
        }

        public double getCarbohydrate_g() {
            return carbohydrate_g;
        }

        public double getTotal_sugars_g() {
            return total_sugars_g;
        }

        public double getAdded_sugars_g() {
            return added_sugars_g;
        }

        public double getTotal_fat_g() {
            return total_fat_g;
        }

        public double getSodium_mg() {
            return sodium_mg;
        }
    }


    // ============================================
    // % RDA PER SERVING
    // ============================================

    public static class PercentRdaPerServing {

        private String energy;

        private String added_sugars;

        private String total_fat;

        private String sodium;


        public String getEnergy() {
            return energy;
        }

        public String getAdded_sugars() {
            return added_sugars;
        }

        public String getTotal_fat() {
            return total_fat;
        }

        public String getSodium() {
            return sodium;
        }
    }


    // ============================================
    // MANUFACTURER
    // ============================================

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
    // ============================================

    public static class Contact {

        private String email;

        private String phone;

        private String website_text;


        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getWebsite_text() {
            return website_text;
        }
    }


    // ============================================
    // NET QUANTITY
    // ============================================

    public static class NetQuantity {

        private double value;

        private String unit;


        public double getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }
    }


    // ============================================
    // FSSAI
    // ============================================

    public static class Fssai {

        private String license_number;


        public String getLicense_number() {
            return license_number;
        }
    }
}