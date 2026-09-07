import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts the text of a "section" that begins at a heading (e.g.
 * "INGREDIENTS:") and ends wherever the NEXT recognizable field
 * heading starts (e.g. "NUTRITION INFORMATION", "MFD BY", "BATCH NO").
 *
 * This replaces the old approach where each field method independently
 * decided its own end boundary (or, in the case of nutrition, used
 * "end of text" as the boundary, which swallows unrelated trailing
 * content). One shared, tested piece of slicing logic instead of N
 * copies of it.
 */
public class SectionExtractor {

    private final Pattern stopPattern;

    public SectionExtractor(String[] stopLabels) {
        String alternation = FieldAliases.alternation(stopLabels);
        this.stopPattern = Pattern.compile("(?i)\\b(?:" + alternation + ")\\b");
    }

    /**
     * @param text       normalized OCR text
     * @param heading    pattern marking the start of the section (e.g. INGREDIENTS heading)
     * @return the section body, or null if the heading wasn't found
     */
    public String extract(String text, Pattern heading) {

        Matcher headingMatcher = heading.matcher(text);

        if (!headingMatcher.find()) {
            return null;
        }

        int start = headingMatcher.end();

        Matcher stopMatcher = stopPattern.matcher(text);

        int end = text.length();

        if (stopMatcher.find(start)) {
            end = stopMatcher.start();
        }

        if (end <= start) {
            return null;
        }

        String section = text.substring(start, end);
        section = section.replaceAll("\\s+", " ").trim();
        section = section.replaceAll("^[:\\-\\s]+", "");

        return section.isEmpty() ? null : section;
    }
}