package com.eqel.engine;

import com.eqel.entity.EvidenceRecordEntity;
import com.eqel.entity.PolicyRuleEntity;
import com.eqel.entity.PolicyVersionEntity;
import com.eqel.entity.StudentProfileEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class EligibilityEngine {

    public static final String VERSION = "1.0.0";

    public enum OverallOutcome {
        ELIGIBLE,
        INELIGIBLE,
        UNVERIFIABLE
    }

    public enum RuleOutcome {
        SATISFIED,
        VIOLATED,
        UNVERIFIABLE
    }

    public static class RuleEvaluationResult {
        private final PolicyRuleEntity rule;
        private final RuleOutcome outcome;
        private final String evidenceValue;
        private final String thresholdValue;
        private final String explanation;

        public RuleEvaluationResult(PolicyRuleEntity rule, RuleOutcome outcome,
                                    String evidenceValue, String thresholdValue,
                                    String explanation) {
            this.rule = rule;
            this.outcome = outcome;
            this.evidenceValue = evidenceValue;
            this.thresholdValue = thresholdValue;
            this.explanation = explanation;
        }

        public PolicyRuleEntity getRule() { return rule; }
        public RuleOutcome getOutcome() { return outcome; }
        public String getEvidenceValue() { return evidenceValue; }
        public String getThresholdValue() { return thresholdValue; }
        public String getExplanation() { return explanation; }
    }

    public static class EvaluationVerdict {
        private final OverallOutcome overallOutcome;
        private final String explanation;
        private final List<RuleEvaluationResult> ruleResults;
        private final String engineVersion;

        public EvaluationVerdict(OverallOutcome overallOutcome, String explanation,
                                 List<RuleEvaluationResult> ruleResults) {
            this.overallOutcome = overallOutcome;
            this.explanation = explanation;
            this.ruleResults = ruleResults;
            this.engineVersion = VERSION;
        }

        public OverallOutcome getOverallOutcome() { return overallOutcome; }
        public String getExplanation() { return explanation; }
        public List<RuleEvaluationResult> getRuleResults() { return ruleResults; }
        public String getEngineVersion() { return engineVersion; }
    }

    /**
     * Evaluates a student against a versioned eligibility policy.
     * Evidence can come from StudentProfileEntity (source of truth) and EvidenceRecordEntity items.
     */
    public EvaluationVerdict evaluate(StudentProfileEntity student,
                                      PolicyVersionEntity policyVersion,
                                      List<EvidenceRecordEntity> evidenceRecords) {

        List<PolicyRuleEntity> rules = policyVersion.getRules();
        List<RuleEvaluationResult> ruleResults = new ArrayList<>();

        // Map latest verified evidence by fieldName
        Map<String, EvidenceRecordEntity> evidenceMap = new HashMap<>();
        Map<String, List<EvidenceRecordEntity>> allEvidenceMap = new HashMap<>();

        if (evidenceRecords != null) {
            for (EvidenceRecordEntity record : evidenceRecords) {
                String field = record.getFieldName().toUpperCase();
                allEvidenceMap.computeIfAbsent(field, k -> new ArrayList<>()).add(record);

                // Priority: VERIFIED evidence takes precedence
                if ("VERIFIED".equalsIgnoreCase(record.getStatus())) {
                    evidenceMap.put(field, record);
                } else if (!evidenceMap.containsKey(field)) {
                    evidenceMap.put(field, record);
                }
            }
        }

        int satisfiedCount = 0;
        int mandatoryViolationCount = 0;
        int mandatoryUnverifiableCount = 0;

        for (PolicyRuleEntity rule : rules) {
            RuleEvaluationResult result = evaluateRule(rule, student, evidenceMap, allEvidenceMap);
            ruleResults.add(result);

            if (rule.isMandatory()) {
                if (result.getOutcome() == RuleOutcome.VIOLATED) {
                    mandatoryViolationCount++;
                } else if (result.getOutcome() == RuleOutcome.UNVERIFIABLE) {
                    mandatoryUnverifiableCount++;
                } else if (result.getOutcome() == RuleOutcome.SATISFIED) {
                    satisfiedCount++;
                }
            }
        }

        // --- Mandatory Overall Outcome Semantics ---
        // 1. INELIGIBLE: At least one mandatory rule is definitively violated
        if (mandatoryViolationCount > 0) {
            String explanation = String.format(
                    "INELIGIBLE: Violates %d mandatory rule(s). Deterministic disqualification.",
                    mandatoryViolationCount
            );
            return new EvaluationVerdict(OverallOutcome.INELIGIBLE, explanation, ruleResults);
        }

        // 2. UNVERIFIABLE: No violations, but at least one mandatory rule is unverifiable or missing
        if (mandatoryUnverifiableCount > 0) {
            String explanation = String.format(
                    "UNVERIFIABLE: %d mandatory rule(s) cannot be confirmed due to missing, pending, or conflicting evidence.",
                    mandatoryUnverifiableCount
            );
            return new EvaluationVerdict(OverallOutcome.UNVERIFIABLE, explanation, ruleResults);
        }

        // 3. ELIGIBLE: All mandatory rules are fully verified and satisfied
        String explanation = String.format(
                "ELIGIBLE: All %d mandatory rule(s) fully satisfied by verified evidence.",
                satisfiedCount
        );
        return new EvaluationVerdict(OverallOutcome.ELIGIBLE, explanation, ruleResults);
    }

    private RuleEvaluationResult evaluateRule(PolicyRuleEntity rule,
                                              StudentProfileEntity student,
                                              Map<String, EvidenceRecordEntity> evidenceMap,
                                              Map<String, List<EvidenceRecordEntity>> allEvidenceMap) {

        String field = rule.getFieldName().toUpperCase();
        String operator = rule.getOperator().toUpperCase();
        String threshold = rule.getThresholdValue();

        // 1. Check for conflicting evidence
        List<EvidenceRecordEntity> records = allEvidenceMap.get(field);
        if (records != null && records.size() > 1) {
            Set<String> distinctValues = new HashSet<>();
            for (EvidenceRecordEntity r : records) {
                if (r.getFieldValue() != null) distinctValues.add(r.getFieldValue().trim());
            }
            if (distinctValues.size() > 1) {
                return new RuleEvaluationResult(
                        rule,
                        RuleOutcome.UNVERIFIABLE,
                        "CONFLICT: " + String.join(" vs ", distinctValues),
                        threshold,
                        "Conflicting evidence documents present for " + field + "; human officer verification required"
                );
            }
        }

        // 2. Extract actual value (Profile is base truth, overridden by verified EvidenceRecord)
        String actualValue = null;
        boolean isVerified = false;

        if (evidenceMap.containsKey(field)) {
            EvidenceRecordEntity record = evidenceMap.get(field);
            actualValue = record.getFieldValue();
            isVerified = "VERIFIED".equalsIgnoreCase(record.getStatus());
        }

        // Fallback to student profile fields if evidence not explicitly submitted
        if (actualValue == null && student != null) {
            switch (field) {
                case "CGPA":
                    if (student.getCgpa() != null) {
                        actualValue = student.getCgpa().toString();
                        isVerified = true;
                    }
                    break;
                case "BACKLOGS":
                    if (student.getBacklogs() != null) {
                        actualValue = student.getBacklogs().toString();
                        isVerified = true;
                    }
                    break;
                case "DEPARTMENT":
                case "DEPT":
                    if (student.getDepartment() != null) {
                        actualValue = student.getDepartment();
                        isVerified = true;
                    }
                    break;
                case "PROGRAM":
                    if (student.getProgram() != null) {
                        actualValue = student.getProgram();
                        isVerified = true;
                    }
                    break;
                case "SEMESTER":
                    if (student.getSemester() != null) {
                        actualValue = student.getSemester().toString();
                        isVerified = true;
                    }
                    break;
            }
        }

        // 3. Handle missing evidence
        if (actualValue == null || actualValue.isBlank()) {
            return new RuleEvaluationResult(
                    rule,
                    RuleOutcome.UNVERIFIABLE,
                    "MISSING",
                    threshold,
                    "No evidence provided for field: " + field
            );
        }

        // 4. If evidence is pending verification, rule is UNVERIFIABLE
        if (!isVerified) {
            return new RuleEvaluationResult(
                    rule,
                    RuleOutcome.UNVERIFIABLE,
                    actualValue + " (UNVERIFIED)",
                    threshold,
                    "Evidence submitted for " + field + " is awaiting officer verification"
            );
        }

        // 5. Evaluate comparison logic
        try {
            boolean satisfied = compareValues(actualValue, operator, threshold);
            if (satisfied) {
                return new RuleEvaluationResult(
                        rule,
                        RuleOutcome.SATISFIED,
                        actualValue,
                        threshold,
                        String.format("Satisfies rule: %s %s %s (actual: %s)", field, operator, threshold, actualValue)
                );
            } else {
                return new RuleEvaluationResult(
                        rule,
                        RuleOutcome.VIOLATED,
                        actualValue,
                        threshold,
                        String.format("Violates rule: requires %s %s %s, but actual value is %s", field, operator, threshold, actualValue)
                );
            }
        } catch (Exception e) {
            return new RuleEvaluationResult(
                    rule,
                    RuleOutcome.UNVERIFIABLE,
                    actualValue,
                    threshold,
                    "Evaluation error for rule: " + e.getMessage()
            );
        }
    }

    private boolean compareValues(String actual, String operator, String threshold) {
        // Numeric comparison (CGPA, Backlogs, Semester)
        try {
            BigDecimal actualNum = new BigDecimal(actual.trim());
            BigDecimal thresholdNum = new BigDecimal(threshold.trim());

            int cmp = actualNum.compareTo(thresholdNum);
            switch (operator) {
                case "GTE": return cmp >= 0;
                case "GT":  return cmp > 0;
                case "LTE": return cmp <= 0;
                case "LT":  return cmp < 0;
                case "EQ":  return cmp == 0;
                case "NEQ": return cmp != 0;
            }
        } catch (NumberFormatException ignored) {
            // String comparison
        }

        String actualStr = actual.trim();
        String thresholdStr = threshold.trim();

        switch (operator) {
            case "EQ":
                return actualStr.equalsIgnoreCase(thresholdStr);
            case "NEQ":
                return !actualStr.equalsIgnoreCase(thresholdStr);
            case "IN": {
                // e.g. threshold: "CSE, IT, ECE"
                String[] parts = thresholdStr.split("[,;]");
                for (String part : parts) {
                    if (actualStr.equalsIgnoreCase(part.trim())) {
                        return true;
                    }
                }
                return false;
            }
            case "NOT_IN": {
                String[] parts = thresholdStr.split("[,;]");
                for (String part : parts) {
                    if (actualStr.equalsIgnoreCase(part.trim())) {
                        return false;
                    }
                }
                return true;
            }
            default:
                return actualStr.equalsIgnoreCase(thresholdStr);
        }
    }
}
