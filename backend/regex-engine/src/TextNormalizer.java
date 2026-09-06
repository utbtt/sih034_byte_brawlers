public class TextNormalizer {

    public static String normalize(String text) {

        if (text == null) {
            return "";
        }

        // ==========================================
        // 1. Normalize line endings
        // ==========================================

        text = text.replace("\r\n", "\n");
        text = text.replace("\r", "\n");


        // ==========================================
        // 2. Remove markdown/code formatting
        // ==========================================

        text = text.replace("```text", "");
        text = text.replace("```", "");


        // ==========================================
        // 3. Remove unnecessary escape characters
        // ==========================================

        text = text.replace("\\@", "@");
        text = text.replace("\\.", ".");
        text = text.replace("\\(", "(");
        text = text.replace("\\)", ")");
        text = text.replace("\\#", "#");


        // ==========================================
        // 4. Normalize OCR punctuation
        // ==========================================

        text = text.replace("—", "-");
        text = text.replace("–", "-");
        text = text.replace("~", "-");


        // ==========================================
        // 5. Normalize common OCR mistakes
        // ==========================================

        text = text.replaceAll(
                "(?i)\\bFSSAI\\b",
                "FSSAI"
        );


        // ==========================================
        // 6. Normalize website punctuation
        // ==========================================

        text = text.replace(
                "www,",
                "www."
        );


        // ==========================================
        // 7. Normalize spaces
        // ==========================================

        text = text.replaceAll(
                "[ \\t]+",
                " "
        );


        // ==========================================
        // 8. Normalize excessive newlines
        // ==========================================

        text = text.replaceAll(
                "\\n{3,}",
                "\n\n"
        );


        // ==========================================
        // 9. Trim every line
        // ==========================================

        String[] lines =
                text.split("\\n");

        StringBuilder result =
                new StringBuilder();


        for (String line : lines) {

            line = line.trim();

            if (!line.isEmpty()) {

                result.append(line);
                result.append("\n");
            }
        }


        return result
                .toString()
                .trim();
    }
}
