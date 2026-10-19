package com.eqel.engine;

import com.eqel.entity.EvidenceRecordEntity;
import com.eqel.entity.PolicyRuleEntity;
import com.eqel.entity.PolicyVersionEntity;
import com.eqel.entity.StudentProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EligibilityEngineTest {

    private EligibilityEngine engine;
    private PolicyVersionEntity policyVersion;
    private StudentProfileEntity student;

    @BeforeEach
    void setUp() {
        engine = new EligibilityEngine();

        policyVersion = new PolicyVersionEntity();
        policyVersion.setVersionNumber(1);

        // Standard rules:
        // Rule 1: CGPA >= 7.50 (Mandatory)
        PolicyRuleEntity rule1 = new PolicyRuleEntity();
        rule1.setRuleCode("MIN_CGPA");
        rule1.setRuleDescription("Minimum CGPA of 7.50");
        rule1.setFieldName("CGPA");
        rule1.setOperator("GTE");
        rule1.setThresholdValue("7.50");
        rule1.setMandatory(true);

        // Rule 2: BACKLOGS <= 0 (Mandatory)
        PolicyRuleEntity rule2 = new PolicyRuleEntity();
        rule2.setRuleCode("MAX_BACKLOGS");
        rule2.setRuleDescription("No active backlogs");
        rule2.setFieldName("BACKLOGS");
        rule2.setOperator("LTE");
        rule2.setThresholdValue("0");
        rule2.setMandatory(true);

        // Rule 3: DEPT IN [CSE, IT] (Mandatory)
        PolicyRuleEntity rule3 = new PolicyRuleEntity();
        rule3.setRuleCode("ELIGIBLE_DEPTS");
        rule3.setRuleDescription("Department must be CSE or IT");
        rule3.setFieldName("DEPARTMENT");
        rule3.setOperator("IN");
        rule3.setThresholdValue("CSE, IT");
        rule3.setMandatory(true);

        List<PolicyRuleEntity> rules = new ArrayList<>();
        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);
        policyVersion.setRules(rules);

        // Base student
        student = new StudentProfileEntity();
        student.setCgpa(new BigDecimal("8.20"));
        student.setBacklogs(0);
        student.setDepartment("CSE");
    }

    @Test
    @DisplayName("ELIGIBLE: When all mandatory criteria are met with verified data")
    void testEligibleOutcome() {
        EligibilityEngine.EvaluationVerdict verdict = engine.evaluate(student, policyVersion, List.of());

        assertEquals(EligibilityEngine.OverallOutcome.ELIGIBLE, verdict.getOverallOutcome());
        assertTrue(verdict.getExplanation().contains("ELIGIBLE"));
        assertEquals(3, verdict.getRuleResults().size());

        for (EligibilityEngine.RuleEvaluationResult rr : verdict.getRuleResults()) {
            assertEquals(EligibilityEngine.RuleOutcome.SATISFIED, rr.getOutcome());
        }
    }

    @Test
    @DisplayName("INELIGIBLE: When CGPA is below mandatory threshold")
    void testIneligibleOutcome_CgpaViolation() {
        student.setCgpa(new BigDecimal("6.80")); // Below 7.50

        EligibilityEngine.EvaluationVerdict verdict = engine.evaluate(student, policyVersion, List.of());

        assertEquals(EligibilityEngine.OverallOutcome.INELIGIBLE, verdict.getOverallOutcome());
        assertTrue(verdict.getExplanation().contains("INELIGIBLE"));

        EligibilityEngine.RuleEvaluationResult cgpaResult = verdict.getRuleResults().stream()
                .filter(r -> r.getRule().getRuleCode().equals("MIN_CGPA"))
                .findFirst()
                .orElseThrow();

        assertEquals(EligibilityEngine.RuleOutcome.VIOLATED, cgpaResult.getOutcome());
        assertTrue(cgpaResult.getExplanation().contains("Violates rule"));
    }

    @Test
    @DisplayName("INELIGIBLE: When backlogs exceed allowable threshold")
    void testIneligibleOutcome_BacklogsViolation() {
        student.setBacklogs(2);

        EligibilityEngine.EvaluationVerdict verdict = engine.evaluate(student, policyVersion, List.of());

        assertEquals(EligibilityEngine.OverallOutcome.INELIGIBLE, verdict.getOverallOutcome());
    }

    @Test
    @DisplayName("UNVERIFIABLE: Missing mandatory evidence cannot automatically produce Ineligible")
    void testUnverifiableOutcome_MissingEvidence() {
        student.setCgpa(null); // CGPA is missing

        EligibilityEngine.EvaluationVerdict verdict = engine.evaluate(student, policyVersion, List.of());

        assertEquals(EligibilityEngine.OverallOutcome.UNVERIFIABLE, verdict.getOverallOutcome());
        assertTrue(verdict.getExplanation().contains("UNVERIFIABLE"));

        EligibilityEngine.RuleEvaluationResult cgpaResult = verdict.getRuleResults().stream()
                .filter(r -> r.getRule().getRuleCode().equals("MIN_CGPA"))
                .findFirst()
                .orElseThrow();

        assertEquals(EligibilityEngine.RuleOutcome.UNVERIFIABLE, cgpaResult.getOutcome());
        assertEquals("MISSING", cgpaResult.getEvidenceValue());
    }

    @Test
    @DisplayName("UNVERIFIABLE: Conflicting evidence records trigger human verification requirement")
    void testUnverifiableOutcome_ConflictingEvidence() {
        EvidenceRecordEntity rec1 = new EvidenceRecordEntity();
        rec1.setFieldName("CGPA");
        rec1.setFieldValue("8.50");
        rec1.setStatus("VERIFIED");

        EvidenceRecordEntity rec2 = new EvidenceRecordEntity();
        rec2.setFieldName("CGPA");
        rec2.setFieldValue("7.10");
        rec2.setStatus("PENDING");

        EligibilityEngine.EvaluationVerdict verdict = engine.evaluate(student, policyVersion, List.of(rec1, rec2));

        assertEquals(EligibilityEngine.OverallOutcome.UNVERIFIABLE, verdict.getOverallOutcome());
        EligibilityEngine.RuleEvaluationResult cgpaResult = verdict.getRuleResults().stream()
                .filter(r -> r.getRule().getRuleCode().equals("MIN_CGPA"))
                .findFirst()
                .orElseThrow();

        assertEquals(EligibilityEngine.RuleOutcome.UNVERIFIABLE, cgpaResult.getOutcome());
        assertTrue(cgpaResult.getEvidenceValue().contains("CONFLICT"));
    }

    @Test
    @DisplayName("Non-mandatory rule failure does not cause Ineligible")
    void testNonMandatoryRuleFailure() {
        // Add optional rule: Certification EQ AWS_CERT (non-mandatory)
        PolicyRuleEntity optionalRule = new PolicyRuleEntity();
        optionalRule.setRuleCode("AWS_CERT");
        optionalRule.setRuleDescription("AWS Cloud Practitioner Certification");
        optionalRule.setFieldName("CERTIFICATION");
        optionalRule.setOperator("EQ");
        optionalRule.setThresholdValue("AWS_CERT");
        optionalRule.setMandatory(false); // OPTIONAL

        policyVersion.getRules().add(optionalRule);

        EligibilityEngine.EvaluationVerdict verdict = engine.evaluate(student, policyVersion, List.of());

        // Overall outcome should still be ELIGIBLE because mandatory rules passed
        assertEquals(EligibilityEngine.OverallOutcome.ELIGIBLE, verdict.getOverallOutcome());
    }
}
