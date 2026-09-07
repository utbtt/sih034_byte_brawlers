public class Violation {

    private String ruleId;
    private String title;
    private String severity;
    private String evidence;
    private String recommendation;

    public Violation(String ruleId,
                     String title,
                     String severity,
                     String evidence,
                     String recommendation) {

        this.ruleId = ruleId;
        this.title = title;
        this.severity = severity;
        this.evidence = evidence;
        this.recommendation = recommendation;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getTitle() {
        return title;
    }

    public String getSeverity() {
        return severity;
    }

    public String getEvidence() {
        return evidence;
    }

    public String getRecommendation() {
        return recommendation;
    }

    @Override
    public String toString() {
        return "\nRule: " + ruleId +
               "\nTitle: " + title +
               "\nSeverity: " + severity +
               "\nEvidence: " + evidence +
               "\nRecommendation: " + recommendation +
               "\n";
    }
}
