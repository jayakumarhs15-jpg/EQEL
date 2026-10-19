package com.eqel.service;

import com.eqel.engine.EligibilityEngine;
import com.eqel.entity.*;
import com.eqel.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Explicit development-only seeder for Placement Demo Records.
 * 
 * Disabled by default. Enabled only when explicitly requested via:
 * - Property: eqel.placement-demo.enabled=true
 * - Admin REST endpoint: POST /api/admin/seed-placement-demo
 * - Programmatic invocation in tests.
 * 
 * Requirements:
 * - Adds exactly 50 demo rows across 10 placement tables.
 * - Idempotent: repeated runs add 0 duplicate rows.
 * - Evaluates eligibility using the genuine EligibilityEngine.
 * - Preserves existing administrator account and user credentials.
 */
@Service
public class PlacementDemoDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(PlacementDemoDataSeeder.class);

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final EligibilityPolicyRepository eligibilityPolicyRepository;
    private final PolicyVersionRepository policyVersionRepository;
    private final DrivePolicyAssignmentRepository drivePolicyAssignmentRepository;
    private final EvidenceRecordRepository evidenceRecordRepository;
    private final EvaluationRecordRepository evaluationRecordRepository;
    private final RuleResultRepository ruleResultRepository;
    private final ApplicationRepository applicationRepository;
    private final EligibilityEngine eligibilityEngine;

    public PlacementDemoDataSeeder(UserRepository userRepository,
                                   StudentProfileRepository studentProfileRepository,
                                   CompanyRepository companyRepository,
                                   PlacementDriveRepository placementDriveRepository,
                                   EligibilityPolicyRepository eligibilityPolicyRepository,
                                   PolicyVersionRepository policyVersionRepository,
                                   DrivePolicyAssignmentRepository drivePolicyAssignmentRepository,
                                   EvidenceRecordRepository evidenceRecordRepository,
                                   EvaluationRecordRepository evaluationRecordRepository,
                                   RuleResultRepository ruleResultRepository,
                                   ApplicationRepository applicationRepository,
                                   EligibilityEngine eligibilityEngine) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyRepository = companyRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.eligibilityPolicyRepository = eligibilityPolicyRepository;
        this.policyVersionRepository = policyVersionRepository;
        this.drivePolicyAssignmentRepository = drivePolicyAssignmentRepository;
        this.evidenceRecordRepository = evidenceRecordRepository;
        this.evaluationRecordRepository = evaluationRecordRepository;
        this.ruleResultRepository = ruleResultRepository;
        this.applicationRepository = applicationRepository;
        this.eligibilityEngine = eligibilityEngine;
    }

    public static class SeedResult {
        private final boolean alreadySeeded;
        private final int companiesAdded;
        private final int drivesAdded;
        private final int policiesAdded;
        private final int policyVersionsAdded;
        private final int policyRulesAdded;
        private final int driveAssignmentsAdded;
        private final int evidenceRecordsAdded;
        private final int evaluationRecordsAdded;
        private final int ruleResultsAdded;
        private final int applicationsAdded;
        private final int totalRowsAdded;
        private final String message;

        public SeedResult(boolean alreadySeeded,
                          int companiesAdded, int drivesAdded, int policiesAdded,
                          int policyVersionsAdded, int policyRulesAdded,
                          int driveAssignmentsAdded, int evidenceRecordsAdded,
                          int evaluationRecordsAdded, int ruleResultsAdded,
                          int applicationsAdded, String message) {
            this.alreadySeeded = alreadySeeded;
            this.companiesAdded = companiesAdded;
            this.drivesAdded = drivesAdded;
            this.policiesAdded = policiesAdded;
            this.policyVersionsAdded = policyVersionsAdded;
            this.policyRulesAdded = policyRulesAdded;
            this.driveAssignmentsAdded = driveAssignmentsAdded;
            this.evidenceRecordsAdded = evidenceRecordsAdded;
            this.evaluationRecordsAdded = evaluationRecordsAdded;
            this.ruleResultsAdded = ruleResultsAdded;
            this.applicationsAdded = applicationsAdded;
            this.totalRowsAdded = companiesAdded + drivesAdded + policiesAdded +
                    policyVersionsAdded + policyRulesAdded + driveAssignmentsAdded +
                    evidenceRecordsAdded + evaluationRecordsAdded + ruleResultsAdded +
                    applicationsAdded;
            this.message = message;
        }

        public static SeedResult alreadySeeded() {
            return new SeedResult(true, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    "Placement demo dataset is already seeded. 0 rows added (idempotent).");
        }

        public boolean isAlreadySeeded() { return alreadySeeded; }
        public int getCompaniesAdded() { return companiesAdded; }
        public int getDrivesAdded() { return drivesAdded; }
        public int getPoliciesAdded() { return policiesAdded; }
        public int getPolicyVersionsAdded() { return policyVersionsAdded; }
        public int getPolicyRulesAdded() { return policyRulesAdded; }
        public int getDriveAssignmentsAdded() { return driveAssignmentsAdded; }
        public int getEvidenceRecordsAdded() { return evidenceRecordsAdded; }
        public int getEvaluationRecordsAdded() { return evaluationRecordsAdded; }
        public int getRuleResultsAdded() { return ruleResultsAdded; }
        public int getApplicationsAdded() { return applicationsAdded; }
        public int getTotalRowsAdded() { return totalRowsAdded; }
        public String getMessage() { return message; }
    }

    /**
     * Executes the seeding in a single database transaction.
     * Idempotent: checks for stable marker company before proceeding.
     */
    @Transactional
    public SeedResult seedPlacementDemoData() {
        // Idempotency guard: Stable marker company
        if (companyRepository.findByName("QuantumScale AI Systems").isPresent()) {
            log.info("PlacementDemoDataSeeder: Demo records already exist. Skipping (0 rows added).");
            return SeedResult.alreadySeeded();
        }

        UserEntity admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalStateException("Administrator account 'admin' must exist prior to demo seeding"));

        // Retrieve existing students
        StudentProfileEntity s1 = studentProfileRepository.findByEnrollmentNumber("EQEL2026CS001")
                .orElseThrow(() -> new IllegalStateException("Student EQEL2026CS001 not found"));
        StudentProfileEntity s2 = studentProfileRepository.findByEnrollmentNumber("EQEL2026IT002")
                .orElseThrow(() -> new IllegalStateException("Student EQEL2026IT002 not found"));
        StudentProfileEntity s3 = studentProfileRepository.findByEnrollmentNumber("EQEL2026EC003")
                .orElseThrow(() -> new IllegalStateException("Student EQEL2026EC003 not found"));
        StudentProfileEntity s4 = studentProfileRepository.findByEnrollmentNumber("EQEL2026CS004")
                .orElseThrow(() -> new IllegalStateException("Student EQEL2026CS004 not found"));
        StudentProfileEntity s5 = studentProfileRepository.findByEnrollmentNumber("EQEL2026ME005")
                .orElseThrow(() -> new IllegalStateException("Student EQEL2026ME005 not found"));

        // 1. Companies (2 rows)
        CompanyEntity c1 = new CompanyEntity();
        c1.setName("QuantumScale AI Systems");
        c1.setIndustry("Artificial Intelligence & Distributed Systems");
        c1.setWebsite("https://quantumscale.demo.local");
        c1.setDescription("Enterprise generative inference and large-scale AI orchestration engines");
        c1.setCreatedBy(admin);
        CompanyEntity savedC1 = companyRepository.save(c1);

        CompanyEntity c2 = new CompanyEntity();
        c2.setName("StrataFin Global Analytics");
        c2.setIndustry("Quantitative Finance & Fintech");
        c2.setWebsite("https://stratafin.demo.local");
        c2.setDescription("Algorithmic execution models, market microstructure research and high-throughput telemetry");
        c2.setCreatedBy(admin);
        CompanyEntity savedC2 = companyRepository.save(c2);

        // 2. Placement Drives (3 rows)
        PlacementDriveEntity d1 = new PlacementDriveEntity();
        d1.setCompany(savedC1);
        d1.setTitle("QuantumScale Machine Learning Engineer 2026");
        d1.setDescription("Campus recruitment for deep learning systems, distributed training and inference compilers");
        d1.setJobRole("Machine Learning Engineer");
        d1.setPackageLpa(new BigDecimal("22.00"));
        d1.setDriveStatus("OPEN");
        d1.setRegistrationOpen(LocalDateTime.now().minusDays(5));
        d1.setRegistrationClose(LocalDateTime.now().plusDays(25));
        d1.setDriveDate(LocalDate.now().plusDays(30));
        d1.setCreatedBy(admin);
        PlacementDriveEntity savedD1 = placementDriveRepository.save(d1);

        PlacementDriveEntity d2 = new PlacementDriveEntity();
        d2.setCompany(savedC2);
        d2.setTitle("StrataFin Quantitative Analyst 2026");
        d2.setDescription("Graduate intake for quantitative research, stochastic volatility modeling and signal synthesis");
        d2.setJobRole("Quantitative Analyst");
        d2.setPackageLpa(new BigDecimal("19.50"));
        d2.setDriveStatus("OPEN");
        d2.setRegistrationOpen(LocalDateTime.now().minusDays(3));
        d2.setRegistrationClose(LocalDateTime.now().plusDays(20));
        d2.setDriveDate(LocalDate.now().plusDays(35));
        d2.setCreatedBy(admin);
        PlacementDriveEntity savedD2 = placementDriveRepository.save(d2);

        PlacementDriveEntity d3 = new PlacementDriveEntity();
        d3.setCompany(savedC2);
        d3.setTitle("StrataFin Data Platform Engineer 2026");
        d3.setDescription("Engineering position for real-time market event streaming pipelines and low latency storage");
        d3.setJobRole("Data Platform Engineer");
        d3.setPackageLpa(new BigDecimal("16.00"));
        d3.setDriveStatus("OPEN");
        d3.setRegistrationOpen(LocalDateTime.now().minusDays(2));
        d3.setRegistrationClose(LocalDateTime.now().plusDays(22));
        d3.setDriveDate(LocalDate.now().plusDays(40));
        d3.setCreatedBy(admin);
        PlacementDriveEntity savedD3 = placementDriveRepository.save(d3);

        // 3. Eligibility Policies (2 rows)
        EligibilityPolicyEntity p1 = new EligibilityPolicyEntity();
        p1.setName("QuantumScale Engineering Criteria Policy");
        p1.setDescription("Rigorous algorithmic and academic standards for ML engineering roles");
        p1.setCreatedBy(admin);
        EligibilityPolicyEntity savedP1 = eligibilityPolicyRepository.save(p1);

        EligibilityPolicyEntity p2 = new EligibilityPolicyEntity();
        p2.setName("StrataFin Analytical Criteria Policy");
        p2.setDescription("Quantitative proficiency, zero standing arrears, and core problem solving standards");
        p2.setCreatedBy(admin);
        EligibilityPolicyEntity savedP2 = eligibilityPolicyRepository.save(p2);

        // 4. Policy Versions (2 rows)
        PolicyVersionEntity pv1 = new PolicyVersionEntity();
        pv1.setPolicy(savedP1);
        pv1.setVersionNumber(1);
        pv1.setNotes("Version 1.0 criteria for QuantumScale 2026 campus intake");
        pv1.setPublished(true);
        pv1.setCreatedBy(admin);

        // 5. Policy Rules (4 rows)
        PolicyRuleEntity r1 = new PolicyRuleEntity();
        r1.setPolicyVersion(pv1);
        r1.setRuleCode("QS-MIN-CGPA");
        r1.setRuleDescription("Minimum cumulative grade point average of 8.50 required");
        r1.setFieldName("CGPA");
        r1.setOperator("GTE");
        r1.setThresholdValue("8.50");
        r1.setMandatory(true);
        pv1.getRules().add(r1);

        PolicyRuleEntity r2 = new PolicyRuleEntity();
        r2.setPolicyVersion(pv1);
        r2.setRuleCode("QS-MAX-BACKLOG");
        r2.setRuleDescription("Strict zero active arrears requirement");
        r2.setFieldName("BACKLOGS");
        r2.setOperator("LTE");
        r2.setThresholdValue("0");
        r2.setMandatory(true);
        pv1.getRules().add(r2);

        PolicyVersionEntity savedPv1 = policyVersionRepository.save(pv1);

        PolicyVersionEntity pv2 = new PolicyVersionEntity();
        pv2.setPolicy(savedP2);
        pv2.setVersionNumber(1);
        pv2.setNotes("Version 1.0 criteria for StrataFin 2026 quantitative recruitment");
        pv2.setPublished(true);
        pv2.setCreatedBy(admin);

        PolicyRuleEntity r3 = new PolicyRuleEntity();
        r3.setPolicyVersion(pv2);
        r3.setRuleCode("SF-MIN-CGPA");
        r3.setRuleDescription("Minimum cumulative grade point average of 8.00 required");
        r3.setFieldName("CGPA");
        r3.setOperator("GTE");
        r3.setThresholdValue("8.00");
        r3.setMandatory(true);
        pv2.getRules().add(r3);

        PolicyRuleEntity r4 = new PolicyRuleEntity();
        r4.setPolicyVersion(pv2);
        r4.setRuleCode("SF-MAX-BACKLOG");
        r4.setRuleDescription("No active backlogs permitted across all completed semesters");
        r4.setFieldName("BACKLOGS");
        r4.setOperator("LTE");
        r4.setThresholdValue("0");
        r4.setMandatory(true);
        pv2.getRules().add(r4);

        PolicyVersionEntity savedPv2 = policyVersionRepository.save(pv2);

        // 6. Drive Policy Assignments (3 rows)
        DrivePolicyAssignmentEntity dpa1 = new DrivePolicyAssignmentEntity();
        dpa1.setDrive(savedD1);
        dpa1.setPolicyVersion(savedPv1);
        dpa1.setAssignedBy(admin);
        drivePolicyAssignmentRepository.save(dpa1);

        DrivePolicyAssignmentEntity dpa2 = new DrivePolicyAssignmentEntity();
        dpa2.setDrive(savedD2);
        dpa2.setPolicyVersion(savedPv2);
        dpa2.setAssignedBy(admin);
        drivePolicyAssignmentRepository.save(dpa2);

        DrivePolicyAssignmentEntity dpa3 = new DrivePolicyAssignmentEntity();
        dpa3.setDrive(savedD3);
        dpa3.setPolicyVersion(savedPv2);
        dpa3.setAssignedBy(admin);
        drivePolicyAssignmentRepository.save(dpa3);

        // 7. Evidence Records (8 rows)
        saveEvidence(s1, "CERTIFICATE", "SKILLS", "Distributed Systems, Kubernetes", "VERIFIED",
                "Cloud Native Computing Foundation", admin, "Certified Kubernetes Application Developer (CKAD) credential verified");

        saveEvidence(s2, "ACADEMIC_RECORD", "BACKLOGS", "0", "VERIFIED",
                "Department Semester Audit", admin, "Confirmed 0 active arrears");

        saveEvidence(s2, "CERTIFICATE", "SKILLS", "SQL, Tableau, Financial Modeling", "VERIFIED",
                "Institute of Financial Analytics", admin, "Financial data modeling credential authenticated");

        saveEvidence(s3, "CERTIFICATE", "SKILLS", "PyTorch, CUDA, Embedded C", "VERIFIED",
                "Hardware Acceleration Institute", admin, "Accelerated computing certification validated");

        saveEvidence(s4, "OFFICIAL_TRANSCRIPT", "CGPA", "8.90", "PENDING",
                "Provisional Grade Sheet Portal", null, "Awaiting controller of examinations verification");

        saveEvidence(s4, "ACADEMIC_RECORD", "BACKLOGS", "0", "VERIFIED",
                "Office of the Dean Examinations", admin, "Clean academic record verified");

        saveEvidence(s5, "ACADEMIC_RECORD", "BACKLOGS", "0", "VERIFIED",
                "Registrar Records Division", admin, "Verified zero standing backlog count");

        saveEvidence(s5, "CERTIFICATE", "SKILLS", "CAD, Finite Element Analysis", "VERIFIED",
                "Engineering Design Council", admin, "Finite Element Analysis professional certification validated");

        // 8. Evaluation Records (7 rows) & 9. Rule Results (14 rows)
        // Using real EligibilityEngine to evaluate
        createEvaluation(s1, savedD1, savedPv1, admin); // ELIGIBLE (8.85 >= 8.50, backlogs 0 <= 0)
        createEvaluation(s1, savedD2, savedPv2, admin); // ELIGIBLE (8.85 >= 8.00, backlogs 0 <= 0)
        createEvaluation(s1, savedD3, savedPv2, admin); // ELIGIBLE (8.85 >= 8.00, backlogs 0 <= 0)
        createEvaluation(s2, savedD1, savedPv1, admin); // INELIGIBLE (7.20 < 8.50)
        createEvaluation(s2, savedD2, savedPv2, admin); // INELIGIBLE (7.20 < 8.00)
        createEvaluation(s3, savedD2, savedPv2, admin); // INELIGIBLE (backlogs 2 > 0)
        createEvaluation(s4, savedD1, savedPv1, admin); // UNVERIFIABLE (CGPA is pending review)

        // 10. Student Applications (5 rows)
        // S1 (Eligible) applied and progressed across drives
        saveApplication(s1, savedD1, "SELECTED");
        saveApplication(s1, savedD2, "SHORTLISTED");
        saveApplication(s1, savedD3, "APPLIED");
        // S2 and S3 (Ineligible) recorded applications marked REJECTED
        saveApplication(s2, savedD3, "REJECTED");
        saveApplication(s3, savedD3, "REJECTED");

        log.info("PlacementDemoDataSeeder: Successfully seeded exactly 50 placement demo records.");

        return new SeedResult(false,
                2,  // companies
                3,  // drives
                2,  // policies
                2,  // versions
                4,  // rules
                3,  // drive assignments
                8,  // evidence records
                7,  // evaluations
                14, // rule results
                5,  // applications
                "Successfully seeded exactly 50 demo records across 10 placement tables."
        );
    }

    private void saveEvidence(StudentProfileEntity student, String type, String field, String value,
                              String status, String source, UserEntity verifiedBy, String notes) {
        EvidenceRecordEntity e = new EvidenceRecordEntity();
        e.setStudent(student);
        e.setEvidenceType(type);
        e.setFieldName(field);
        e.setFieldValue(value);
        e.setStatus(status);
        e.setRevision(1);
        e.setSource(source);
        if ("VERIFIED".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status)) {
            e.setVerifiedAt(LocalDateTime.now());
            e.setVerifiedBy(verifiedBy);
            e.setReviewerNotes(notes);
        } else {
            e.setReviewerNotes(notes);
        }
        evidenceRecordRepository.save(e);
    }

    private void createEvaluation(StudentProfileEntity student, PlacementDriveEntity drive,
                                  PolicyVersionEntity policyVersion, UserEntity evaluator) {
        List<EvidenceRecordEntity> evidence = evidenceRecordRepository.findByStudentId(student.getId());
        EligibilityEngine.EvaluationVerdict verdict = eligibilityEngine.evaluate(student, policyVersion, evidence);

        EvaluationRecordEntity record = new EvaluationRecordEntity();
        record.setStudent(student);
        record.setDrive(drive);
        record.setPolicyVersion(policyVersion);
        record.setOverallOutcome(verdict.getOverallOutcome().name());
        record.setExplanation(verdict.getExplanation());
        record.setDecisionEngineVersion(verdict.getEngineVersion());
        record.setEvaluatedBy(evaluator);
        EvaluationRecordEntity saved = evaluationRecordRepository.save(record);

        List<RuleResultEntity> ruleEntities = new ArrayList<>();
        for (EligibilityEngine.RuleEvaluationResult rr : verdict.getRuleResults()) {
            RuleResultEntity rre = new RuleResultEntity();
            rre.setEvaluation(saved);
            rre.setRule(rr.getRule());
            rre.setRuleCode(rr.getRule().getRuleCode());
            rre.setResult(rr.getOutcome().name());
            rre.setEvidenceValue(rr.getEvidenceValue());
            rre.setThresholdValue(rr.getThresholdValue());
            rre.setExplanation(rr.getExplanation());
            ruleEntities.add(rre);
        }
        ruleResultRepository.saveAll(ruleEntities);
        saved.setRuleResults(ruleEntities);
    }

    private void saveApplication(StudentProfileEntity student, PlacementDriveEntity drive, String status) {
        ApplicationEntity app = new ApplicationEntity();
        app.setStudent(student);
        app.setDrive(drive);
        app.setStatus(status);
        applicationRepository.save(app);
    }
}
