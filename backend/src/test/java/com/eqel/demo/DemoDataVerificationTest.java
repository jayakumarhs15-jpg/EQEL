package com.eqel.demo;

import com.eqel.engine.EligibilityEngine;
import com.eqel.entity.*;
import com.eqel.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the demo data seeding logic and eligibility engine consistency.
 * Seeds data programmatically (mirrors V3__demo_records.sql) since the test
 * profile uses H2 with Flyway disabled.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DemoDataVerificationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private StudentProfileRepository studentProfileRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private PlacementDriveRepository placementDriveRepository;
    @Autowired private EligibilityPolicyRepository eligibilityPolicyRepository;
    @Autowired private PolicyVersionRepository policyVersionRepository;
    @Autowired private DrivePolicyAssignmentRepository drivePolicyAssignmentRepository;
    @Autowired private EvidenceRecordRepository evidenceRecordRepository;
    @Autowired private EvaluationRecordRepository evaluationRecordRepository;
    @Autowired private RuleResultRepository ruleResultRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EligibilityEngine eligibilityEngine;

    // Track created entities
    private UserEntity adminUser;
    private RoleEntity studentRole;
    private UserEntity[] studentUsers = new UserEntity[5];
    private StudentProfileEntity[] profiles = new StudentProfileEntity[5];
    private CompanyEntity[] companies = new CompanyEntity[2];
    private PlacementDriveEntity[] drives = new PlacementDriveEntity[2];
    private EligibilityPolicyEntity[] policies = new EligibilityPolicyEntity[2];
    private PolicyVersionEntity[] versions = new PolicyVersionEntity[2];
    private PolicyRuleEntity[] rules = new PolicyRuleEntity[4];

    @BeforeEach
    void seedDemoData() {
        // Grab admin user (created by AdminBootstrapService)
        adminUser = userRepository.findByUsername("admin").orElseThrow();
        studentRole = roleRepository.findByName("STUDENT").orElseThrow();

        // --- 1. Users (5 rows) ---
        String[] usernames = {"demo.student01", "demo.student02", "demo.student03", "demo.student04", "demo.student05"};
        String[] fullNames = {"Aarav Sharma", "Bhavna Patel", "Chirag Verma", "Divya Nair", "Eshan Gupta"};
        String[] emails = {"aarav@demo.eqel.local", "bhavna@demo.eqel.local", "chirag@demo.eqel.local", "divya@demo.eqel.local", "eshan@demo.eqel.local"};
        String hash = passwordEncoder.encode("Student@eqel2026");
        for (int i = 0; i < 5; i++) {
            if (userRepository.findByUsername(usernames[i]).isEmpty()) {
                UserEntity u = new UserEntity();
                u.setUsername(usernames[i]);
                u.setEmail(emails[i]);
                u.setFullName(fullNames[i]);
                u.setPasswordHash(hash);
                u.setRole(studentRole);
                u.setActive(true);
                studentUsers[i] = userRepository.save(u);
            } else {
                studentUsers[i] = userRepository.findByUsername(usernames[i]).get();
            }
        }

        // --- 2. Student Profiles (5 rows) ---
        String[] enrollments = {"EQEL2026CS001", "EQEL2026IT002", "EQEL2026EC003", "EQEL2026CS004", "EQEL2026ME005"};
        String[] departments = {"Computer Science", "Information Technology", "Electronics & Comm", "Computer Science", "Mechanical Engineering"};
        BigDecimal[] cgpas = {new BigDecimal("8.85"), new BigDecimal("7.20"), new BigDecimal("8.10"), new BigDecimal("8.40"), new BigDecimal("7.90")};
        int[] backlogs = {0, 0, 2, 0, 0};
        for (int i = 0; i < 5; i++) {
            StudentProfileEntity sp = new StudentProfileEntity();
            sp.setUser(studentUsers[i]);
            sp.setEnrollmentNumber(enrollments[i]);
            sp.setDepartment(departments[i]);
            sp.setProgram("B.Tech");
            sp.setSemester(7);
            sp.setCgpa(cgpas[i]);
            sp.setBacklogs(backlogs[i]);
            sp.setPhone("+91-98765-0000" + (i + 1));
            sp.setDateOfBirth(LocalDate.of(2004, 1 + i, 10 + i));
            profiles[i] = studentProfileRepository.save(sp);
        }

        // --- 3. Companies (2 rows) ---
        CompanyEntity c1 = new CompanyEntity();
        c1.setName("NexGen Cloud Technologies");
        c1.setIndustry("Cloud Computing & AI");
        c1.setWebsite("https://nexgen.demo.local");
        c1.setDescription("Enterprise cloud infrastructure");
        c1.setCreatedBy(adminUser);
        companies[0] = companyRepository.save(c1);

        CompanyEntity c2 = new CompanyEntity();
        c2.setName("Apex Cybernetics Corp");
        c2.setIndustry("Autonomous Systems & Robotics");
        c2.setWebsite("https://apex-cyber.demo.local");
        c2.setDescription("Real-time perception and autonomous systems");
        c2.setCreatedBy(adminUser);
        companies[1] = companyRepository.save(c2);

        // --- 4. Placement Drives (2 rows) ---
        PlacementDriveEntity d1 = new PlacementDriveEntity();
        d1.setCompany(companies[0]);
        d1.setTitle("NexGen Cloud Software Engineer 2026");
        d1.setDescription("Campus recruitment for cloud engineering");
        d1.setJobRole("Associate Cloud Engineer");
        d1.setPackageLpa(new BigDecimal("14.50"));
        d1.setDriveStatus("OPEN");
        d1.setRegistrationOpen(LocalDateTime.of(2026, 10, 1, 9, 0));
        d1.setRegistrationClose(LocalDateTime.of(2026, 11, 15, 18, 0));
        d1.setDriveDate(LocalDate.of(2026, 11, 20));
        d1.setCreatedBy(adminUser);
        drives[0] = placementDriveRepository.save(d1);

        PlacementDriveEntity d2 = new PlacementDriveEntity();
        d2.setCompany(companies[1]);
        d2.setTitle("Apex Cybernetics Systems Engineer 2026");
        d2.setDescription("Graduate recruitment for real-time systems");
        d2.setJobRole("Systems Software Engineer");
        d2.setPackageLpa(new BigDecimal("18.00"));
        d2.setDriveStatus("OPEN");
        d2.setRegistrationOpen(LocalDateTime.of(2026, 10, 5, 9, 0));
        d2.setRegistrationClose(LocalDateTime.of(2026, 11, 20, 18, 0));
        d2.setDriveDate(LocalDate.of(2026, 11, 25));
        d2.setCreatedBy(adminUser);
        drives[1] = placementDriveRepository.save(d2);

        // --- 5. Eligibility Policies (2 rows) ---
        EligibilityPolicyEntity p1 = new EligibilityPolicyEntity();
        p1.setName("NexGen Engineering Eligibility Policy");
        p1.setDescription("Standard eligibility requirements for NexGen Cloud");
        p1.setCreatedBy(adminUser);
        policies[0] = eligibilityPolicyRepository.save(p1);

        EligibilityPolicyEntity p2 = new EligibilityPolicyEntity();
        p2.setName("Apex Core Systems Eligibility Policy");
        p2.setDescription("Academic baseline for Apex Cybernetics");
        p2.setCreatedBy(adminUser);
        policies[1] = eligibilityPolicyRepository.save(p2);

        // --- 6. Policy Versions (2 rows) ---
        PolicyVersionEntity pv1 = new PolicyVersionEntity();
        pv1.setPolicy(policies[0]);
        pv1.setVersionNumber(1);
        pv1.setNotes("Initial published version for NexGen 2026");
        pv1.setPublished(true);
        pv1.setCreatedBy(adminUser);

        PolicyRuleEntity r1 = new PolicyRuleEntity();
        r1.setPolicyVersion(pv1);
        r1.setRuleCode("NG-MIN-CGPA");
        r1.setRuleDescription("Minimum cumulative GPA of 7.50 required");
        r1.setFieldName("CGPA");
        r1.setOperator("GTE");
        r1.setThresholdValue("7.50");
        r1.setMandatory(true);
        pv1.getRules().add(r1);

        PolicyRuleEntity r2 = new PolicyRuleEntity();
        r2.setPolicyVersion(pv1);
        r2.setRuleCode("NG-MAX-BACKLOG");
        r2.setRuleDescription("Maximum permitted active backlogs is zero");
        r2.setFieldName("BACKLOGS");
        r2.setOperator("LTE");
        r2.setThresholdValue("0");
        r2.setMandatory(true);
        pv1.getRules().add(r2);

        versions[0] = policyVersionRepository.save(pv1);
        rules[0] = versions[0].getRules().get(0);
        rules[1] = versions[0].getRules().get(1);

        PolicyVersionEntity pv2 = new PolicyVersionEntity();
        pv2.setPolicy(policies[1]);
        pv2.setVersionNumber(1);
        pv2.setNotes("Initial published version for Apex Cybernetics 2026");
        pv2.setPublished(true);
        pv2.setCreatedBy(adminUser);

        PolicyRuleEntity r3 = new PolicyRuleEntity();
        r3.setPolicyVersion(pv2);
        r3.setRuleCode("AC-MIN-CGPA");
        r3.setRuleDescription("Minimum cumulative GPA of 8.00 required");
        r3.setFieldName("CGPA");
        r3.setOperator("GTE");
        r3.setThresholdValue("8.00");
        r3.setMandatory(true);
        pv2.getRules().add(r3);

        PolicyRuleEntity r4 = new PolicyRuleEntity();
        r4.setPolicyVersion(pv2);
        r4.setRuleCode("AC-MAX-BACKLOG");
        r4.setRuleDescription("No active backlogs allowed");
        r4.setFieldName("BACKLOGS");
        r4.setOperator("LTE");
        r4.setThresholdValue("0");
        r4.setMandatory(true);
        pv2.getRules().add(r4);

        versions[1] = policyVersionRepository.save(pv2);
        rules[2] = versions[1].getRules().get(0);
        rules[3] = versions[1].getRules().get(1);

        // --- 8. Drive Policy Assignments (2 rows) ---
        DrivePolicyAssignmentEntity dpa1 = new DrivePolicyAssignmentEntity();
        dpa1.setDrive(drives[0]);
        dpa1.setPolicyVersion(versions[0]);
        dpa1.setAssignedBy(adminUser);
        drivePolicyAssignmentRepository.save(dpa1);

        DrivePolicyAssignmentEntity dpa2 = new DrivePolicyAssignmentEntity();
        dpa2.setDrive(drives[1]);
        dpa2.setPolicyVersion(versions[1]);
        dpa2.setAssignedBy(adminUser);
        drivePolicyAssignmentRepository.save(dpa2);

        // --- 9. Evidence Records (8 rows) ---
        // Student 1: Verified CGPA + Verified Backlogs
        saveEvidence(profiles[0], "OFFICIAL_TRANSCRIPT", "CGPA", "8.85", "VERIFIED", "University Portal");
        saveEvidence(profiles[0], "ACADEMIC_RECORD", "BACKLOGS", "0", "VERIFIED", "Institutional ERP");
        // Student 2: Verified CGPA (7.20 — ineligible for 7.50 threshold)
        saveEvidence(profiles[1], "OFFICIAL_TRANSCRIPT", "CGPA", "7.20", "VERIFIED", "Registrar Sealed Copy");
        // Student 3: Verified CGPA (8.10) + Verified Backlogs (2 — ineligible)
        saveEvidence(profiles[2], "OFFICIAL_TRANSCRIPT", "CGPA", "8.10", "VERIFIED", "Digital Transcript");
        saveEvidence(profiles[2], "BACKLOG_SUMMARY", "BACKLOGS", "2", "VERIFIED", "Department Arrear Ledger");
        // Student 4: Pending CGPA (unverifiable)
        saveEvidence(profiles[3], "OFFICIAL_TRANSCRIPT", "CGPA", "8.40", "PENDING", "Student Self-Upload");
        // Student 5: Rejected CGPA (unverifiable) + Verified skills cert
        saveEvidence(profiles[4], "OFFICIAL_TRANSCRIPT", "CGPA", "7.90", "REJECTED", "Uncertified Mobile Scan");
        saveEvidence(profiles[4], "CERTIFICATE", "SKILLS", "CAD, Python, FEA", "VERIFIED", "National Skill Development Academy");

        // --- 10. Evaluation Records (6 rows) + 11. Rule Results (12 rows) ---
        // These are created by the engine in the assertions below, to verify consistency
    }

    private void saveEvidence(StudentProfileEntity student, String type, String field, String value, String status, String source) {
        EvidenceRecordEntity e = new EvidenceRecordEntity();
        e.setStudent(student);
        e.setEvidenceType(type);
        e.setFieldName(field);
        e.setFieldValue(value);
        e.setStatus(status);
        e.setRevision(1);
        e.setSource(source);
        if ("VERIFIED".equals(status) || "REJECTED".equals(status)) {
            e.setVerifiedAt(LocalDateTime.now());
            e.setVerifiedBy(adminUser);
            e.setReviewerNotes("Reviewed");
        }
        evidenceRecordRepository.save(e);
    }

    @Test
    @DisplayName("Verify demo record counts across 9 seeded tables total 32 (pre-evaluation)")
    void testDemoRecordCounts() {
        long userCount = userRepository.findAll().stream()
                .filter(u -> u.getUsername().startsWith("demo.student")).count();
        long profileCount = studentProfileRepository.findAll().stream()
                .filter(p -> p.getEnrollmentNumber().startsWith("EQEL2026")).count();
        long companyCount = companyRepository.findAll().stream()
                .filter(c -> c.getName().contains("NexGen") || c.getName().contains("Apex")).count();
        long driveCount = placementDriveRepository.findAll().stream()
                .filter(d -> d.getTitle().contains("2026")).count();
        long policyCount = eligibilityPolicyRepository.findAll().stream()
                .filter(p -> p.getName().contains("NexGen") || p.getName().contains("Apex")).count();
        long versionCount = policyVersionRepository.findAll().stream()
                .filter(pv -> pv.getPolicy().getName().contains("NexGen") || pv.getPolicy().getName().contains("Apex")).count();
        long ruleCount = policyVersionRepository.findAll().stream()
                .filter(pv -> pv.getPolicy().getName().contains("NexGen") || pv.getPolicy().getName().contains("Apex"))
                .mapToLong(pv -> pv.getRules().size()).sum();
        long assignmentCount = drivePolicyAssignmentRepository.findAll().size();
        long evidenceCount = evidenceRecordRepository.findAll().stream()
                .filter(e -> e.getStudent().getEnrollmentNumber().startsWith("EQEL2026")).count();

        assertThat(userCount).as("demo users").isEqualTo(5);
        assertThat(profileCount).as("student profiles").isEqualTo(5);
        assertThat(companyCount).as("companies").isEqualTo(2);
        assertThat(driveCount).as("placement drives").isEqualTo(2);
        assertThat(policyCount).as("eligibility policies").isEqualTo(2);
        assertThat(versionCount).as("policy versions").isEqualTo(2);
        assertThat(ruleCount).as("policy rules").isEqualTo(4);
        assertThat(assignmentCount).as("drive-policy assignments").isEqualTo(2);
        assertThat(evidenceCount).as("evidence records").isEqualTo(8);

        // 5+5+2+2+2+2+4+2+8 = 32 pre-evaluation rows
        long preEvalTotal = userCount + profileCount + companyCount + driveCount + policyCount + versionCount + ruleCount + assignmentCount + evidenceCount;
        assertThat(preEvalTotal).as("Pre-evaluation total").isEqualTo(32);
    }

    @Test
    @DisplayName("Verify admin user is preserved")
    void testAdminPreserved() {
        UserEntity admin = userRepository.findByUsername("admin").orElse(null);
        assertThat(admin).isNotNull();
        assertThat(admin.getRole().getName()).isEqualTo("ADMIN");
        assertThat(admin.isActive()).isTrue();
    }

    @Test
    @DisplayName("Verify eligibility engine produces correct tri-state outcomes for all 5 students")
    void testEligibilityEngineConsistency() {
        PolicyVersionEntity nexGenPolicy = versions[0];

        // Student 1 (Aarav): CGPA 8.85, backlogs 0, all verified → ELIGIBLE
        List<EvidenceRecordEntity> s1Evidence = evidenceRecordRepository.findByStudentId(profiles[0].getId());
        EligibilityEngine.EvaluationVerdict v1 = eligibilityEngine.evaluate(profiles[0], nexGenPolicy, s1Evidence);
        assertThat(v1.getOverallOutcome()).isEqualTo(EligibilityEngine.OverallOutcome.ELIGIBLE);
        assertThat(v1.getRuleResults()).hasSize(2);
        assertThat(v1.getRuleResults()).allMatch(r -> r.getOutcome() == EligibilityEngine.RuleOutcome.SATISFIED);

        // Student 2 (Bhavna): CGPA 7.20 < 7.50 threshold → INELIGIBLE
        List<EvidenceRecordEntity> s2Evidence = evidenceRecordRepository.findByStudentId(profiles[1].getId());
        EligibilityEngine.EvaluationVerdict v2 = eligibilityEngine.evaluate(profiles[1], nexGenPolicy, s2Evidence);
        assertThat(v2.getOverallOutcome()).isEqualTo(EligibilityEngine.OverallOutcome.INELIGIBLE);

        // Student 3 (Chirag): CGPA 8.10 ≥ 7.50, but backlogs 2 > 0 → INELIGIBLE
        List<EvidenceRecordEntity> s3Evidence = evidenceRecordRepository.findByStudentId(profiles[2].getId());
        EligibilityEngine.EvaluationVerdict v3 = eligibilityEngine.evaluate(profiles[2], nexGenPolicy, s3Evidence);
        assertThat(v3.getOverallOutcome()).isEqualTo(EligibilityEngine.OverallOutcome.INELIGIBLE);

        // Student 4 (Divya): CGPA 8.40 PENDING → UNVERIFIABLE
        List<EvidenceRecordEntity> s4Evidence = evidenceRecordRepository.findByStudentId(profiles[3].getId());
        EligibilityEngine.EvaluationVerdict v4 = eligibilityEngine.evaluate(profiles[3], nexGenPolicy, s4Evidence);
        assertThat(v4.getOverallOutcome()).isEqualTo(EligibilityEngine.OverallOutcome.UNVERIFIABLE);

        // Student 5 (Eshan): CGPA 7.90 REJECTED → UNVERIFIABLE
        List<EvidenceRecordEntity> s5Evidence = evidenceRecordRepository.findByStudentId(profiles[4].getId());
        EligibilityEngine.EvaluationVerdict v5 = eligibilityEngine.evaluate(profiles[4], nexGenPolicy, s5Evidence);
        assertThat(v5.getOverallOutcome()).isEqualTo(EligibilityEngine.OverallOutcome.UNVERIFIABLE);
    }

    @Test
    @DisplayName("Verify evaluation records and rule results reach exactly 50 total when engine runs")
    void testFullFiftyRowsWithEvaluations() {
        PolicyVersionEntity nexGenPolicy = versions[0];
        PolicyVersionEntity apexPolicy = versions[1];

        // Run engine for 6 evaluations (matching V3 migration data)
        // Eval 1: Student 1 vs NexGen
        createEvaluation(profiles[0], drives[0], nexGenPolicy);
        // Eval 2: Student 1 vs Apex
        createEvaluation(profiles[0], drives[1], apexPolicy);
        // Eval 3: Student 2 vs NexGen
        createEvaluation(profiles[1], drives[0], nexGenPolicy);
        // Eval 4: Student 3 vs NexGen
        createEvaluation(profiles[2], drives[0], nexGenPolicy);
        // Eval 5: Student 4 vs NexGen
        createEvaluation(profiles[3], drives[0], nexGenPolicy);
        // Eval 6: Student 5 vs NexGen
        createEvaluation(profiles[4], drives[0], nexGenPolicy);

        long evalCount = evaluationRecordRepository.findAll().stream()
                .filter(e -> e.getStudent().getEnrollmentNumber().startsWith("EQEL2026")).count();
        long ruleResultCount = ruleResultRepository.findAll().stream()
                .filter(rr -> rr.getRuleCode().startsWith("NG-") || rr.getRuleCode().startsWith("AC-")).count();

        assertThat(evalCount).as("evaluation records").isEqualTo(6);
        assertThat(ruleResultCount).as("rule results").isEqualTo(12);

        // Grand total: 32 (pre-eval) + 6 (evals) + 12 (rule results) = 50
        long grandTotal = 5 + 5 + 2 + 2 + 2 + 2 + 4 + 2 + 8 + evalCount + ruleResultCount;
        assertThat(grandTotal).as("Grand total demo rows must be exactly 50").isEqualTo(50);
    }

    private void createEvaluation(StudentProfileEntity student, PlacementDriveEntity drive, PolicyVersionEntity policyVersion) {
        List<EvidenceRecordEntity> evidence = evidenceRecordRepository.findByStudentId(student.getId());
        EligibilityEngine.EvaluationVerdict verdict = eligibilityEngine.evaluate(student, policyVersion, evidence);

        EvaluationRecordEntity record = new EvaluationRecordEntity();
        record.setStudent(student);
        record.setDrive(drive);
        record.setPolicyVersion(policyVersion);
        record.setOverallOutcome(verdict.getOverallOutcome().name());
        record.setExplanation(verdict.getExplanation());
        record.setDecisionEngineVersion(verdict.getEngineVersion());
        record.setEvaluatedBy(adminUser);
        EvaluationRecordEntity saved = evaluationRecordRepository.save(record);

        for (EligibilityEngine.RuleEvaluationResult rr : verdict.getRuleResults()) {
            RuleResultEntity rre = new RuleResultEntity();
            rre.setEvaluation(saved);
            rre.setRule(rr.getRule());
            rre.setRuleCode(rr.getRule().getRuleCode());
            rre.setResult(rr.getOutcome().name());
            rre.setEvidenceValue(rr.getEvidenceValue());
            rre.setThresholdValue(rr.getThresholdValue());
            rre.setExplanation(rr.getExplanation());
            ruleResultRepository.save(rre);
        }
    }
}
