package com.eqel.demo;

import com.eqel.entity.ApplicationEntity;
import com.eqel.entity.CompanyEntity;
import com.eqel.entity.DrivePolicyAssignmentEntity;
import com.eqel.entity.EligibilityPolicyEntity;
import com.eqel.entity.EvaluationRecordEntity;
import com.eqel.entity.EvidenceRecordEntity;
import com.eqel.entity.PlacementDriveEntity;
import com.eqel.entity.PolicyRuleEntity;
import com.eqel.entity.PolicyVersionEntity;
import com.eqel.entity.RoleEntity;
import com.eqel.entity.RuleResultEntity;
import com.eqel.entity.StudentProfileEntity;
import com.eqel.entity.UserEntity;
import com.eqel.repository.ApplicationRepository;
import com.eqel.repository.CompanyRepository;
import com.eqel.repository.DrivePolicyAssignmentRepository;
import com.eqel.repository.EligibilityPolicyRepository;
import com.eqel.repository.EvaluationRecordRepository;
import com.eqel.repository.EvidenceRecordRepository;
import com.eqel.repository.PlacementDriveRepository;
import com.eqel.repository.PolicyRuleRepository;
import com.eqel.repository.PolicyVersionRepository;
import com.eqel.repository.RoleRepository;
import com.eqel.repository.RuleResultRepository;
import com.eqel.repository.StudentProfileRepository;
import com.eqel.repository.UserRepository;
import com.eqel.service.PlacementDemoDataSeeder;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PlacementDemoDataSeederTest {

    @Autowired private PlacementDemoDataSeeder seeder;
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
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpExistingData() {
        // Ensure standard roles exist
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new RoleEntity("ADMIN", "System administrator")));
        RoleEntity studentRole = roleRepository.findByName("STUDENT")
                .orElseGet(() -> roleRepository.save(new RoleEntity("STUDENT", "Student applicant")));

        // Ensure admin user exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setEmail("admin@eqel.local");
            admin.setFullName("System Administrator");
            admin.setPasswordHash(passwordEncoder.encode("Admin@eqel2026"));
            admin.setRole(adminRole);
            admin.setActive(true);
            userRepository.save(admin);
        }

        // Ensure 5 standard demo students exist
        String[] usernames = {"demo.student01", "demo.student02", "demo.student03", "demo.student04", "demo.student05"};
        String[] fullNames = {"Aarav Sharma", "Bhavna Patel", "Chirag Verma", "Divya Nair", "Eshan Gupta"};
        String[] emails = {"aarav@demo.eqel.local", "bhavna@demo.eqel.local", "chirag@demo.eqel.local", "divya@demo.eqel.local", "eshan@demo.eqel.local"};
        String[] enrollments = {"EQEL2026CS001", "EQEL2026IT002", "EQEL2026EC003", "EQEL2026CS004", "EQEL2026ME005"};
        String[] departments = {"Computer Science", "Information Technology", "Electronics & Comm", "Computer Science", "Mechanical Engineering"};
        BigDecimal[] cgpas = {new BigDecimal("8.85"), new BigDecimal("7.20"), new BigDecimal("8.10"), new BigDecimal("8.40"), new BigDecimal("7.90")};
        int[] backlogs = {0, 0, 2, 0, 0};

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
                UserEntity savedU = userRepository.save(u);

                StudentProfileEntity sp = new StudentProfileEntity();
                sp.setUser(savedU);
                sp.setEnrollmentNumber(enrollments[i]);
                sp.setDepartment(departments[i]);
                sp.setProgram("B.Tech");
                sp.setSemester(7);
                sp.setCgpa(cgpas[i]);
                sp.setBacklogs(backlogs[i]);
                sp.setPhone("+91-98765-0000" + (i + 1));
                sp.setDateOfBirth(LocalDate.of(2004, 1 + i, 10 + i));
                studentProfileRepository.save(sp);
            }
        }
    }

    @Test
    @DisplayName("Verify seeder adds exactly 50 rows across all 10 placement tables")
    void testSeedExactlyFiftyRows() {
        PlacementDemoDataSeeder.SeedResult result = seeder.seedPlacementDemoData();

        assertThat(result.isAlreadySeeded()).isFalse();
        assertThat(result.getTotalRowsAdded()).isEqualTo(50);
        assertThat(result.getCompaniesAdded()).isEqualTo(2);
        assertThat(result.getDrivesAdded()).isEqualTo(3);
        assertThat(result.getPoliciesAdded()).isEqualTo(2);
        assertThat(result.getPolicyVersionsAdded()).isEqualTo(2);
        assertThat(result.getPolicyRulesAdded()).isEqualTo(4);
        assertThat(result.getDriveAssignmentsAdded()).isEqualTo(3);
        assertThat(result.getEvidenceRecordsAdded()).isEqualTo(8);
        assertThat(result.getEvaluationRecordsAdded()).isEqualTo(7);
        assertThat(result.getRuleResultsAdded()).isEqualTo(14);
        assertThat(result.getApplicationsAdded()).isEqualTo(5);

        // Verify entities in database
        assertThat(companyRepository.findByName("QuantumScale AI Systems")).isPresent();
        assertThat(companyRepository.findByName("StrataFin Global Analytics")).isPresent();

        assertThat(placementDriveRepository.findByTitle("QuantumScale Machine Learning Engineer 2026")).isPresent();
        assertThat(placementDriveRepository.findByTitle("StrataFin Quantitative Analyst 2026")).isPresent();
        assertThat(placementDriveRepository.findByTitle("StrataFin Data Platform Engineer 2026")).isPresent();

        assertThat(eligibilityPolicyRepository.findByName("QuantumScale Engineering Criteria Policy")).isPresent();
        assertThat(eligibilityPolicyRepository.findByName("StrataFin Analytical Criteria Policy")).isPresent();

        List<ApplicationEntity> applications = applicationRepository.findAll();
        assertThat(applications).hasSize(5);
        assertThat(applications).anyMatch(a -> "SELECTED".equals(a.getStatus()));
        assertThat(applications).anyMatch(a -> "SHORTLISTED".equals(a.getStatus()));
        assertThat(applications).anyMatch(a -> "APPLIED".equals(a.getStatus()));
        assertThat(applications).anyMatch(a -> "REJECTED".equals(a.getStatus()));
    }

    @Test
    @DisplayName("Verify seeder idempotence: second run adds 0 rows")
    void testSeederIdempotence() {
        PlacementDemoDataSeeder.SeedResult firstResult = seeder.seedPlacementDemoData();
        assertThat(firstResult.getTotalRowsAdded()).isEqualTo(50);

        PlacementDemoDataSeeder.SeedResult secondResult = seeder.seedPlacementDemoData();
        assertThat(secondResult.isAlreadySeeded()).isTrue();
        assertThat(secondResult.getTotalRowsAdded()).isEqualTo(0);
    }

    @Test
    @DisplayName("Verify eligibility outcomes and business rules consistency")
    void testEligibilityEvaluationOutcomes() {
        seeder.seedPlacementDemoData();

        PlacementDriveEntity qsDrive = placementDriveRepository.findByTitle("QuantumScale Machine Learning Engineer 2026").orElseThrow();
        PlacementDriveEntity sfQuantDrive = placementDriveRepository.findByTitle("StrataFin Quantitative Analyst 2026").orElseThrow();

        StudentProfileEntity s1 = studentProfileRepository.findByEnrollmentNumber("EQEL2026CS001").orElseThrow();
        StudentProfileEntity s2 = studentProfileRepository.findByEnrollmentNumber("EQEL2026IT002").orElseThrow();
        StudentProfileEntity s3 = studentProfileRepository.findByEnrollmentNumber("EQEL2026EC003").orElseThrow();
        StudentProfileEntity s4 = studentProfileRepository.findByEnrollmentNumber("EQEL2026CS004").orElseThrow();

        // Student 1 (CGPA 8.85 >= 8.50, backlogs 0) -> ELIGIBLE for QuantumScale
        EvaluationRecordEntity evalS1 = evaluationRecordRepository.searchEvaluations(null, qsDrive.getId(), s1.getId(), null)
                .getContent().get(0);
        assertThat(evalS1.getOverallOutcome()).isEqualTo("ELIGIBLE");

        // Student 2 (CGPA 7.20 < 8.50) -> INELIGIBLE for QuantumScale
        EvaluationRecordEntity evalS2 = evaluationRecordRepository.searchEvaluations(null, qsDrive.getId(), s2.getId(), null)
                .getContent().get(0);
        assertThat(evalS2.getOverallOutcome()).isEqualTo("INELIGIBLE");

        // Student 3 (Backlogs 2 > 0) -> INELIGIBLE for StrataFin
        EvaluationRecordEntity evalS3 = evaluationRecordRepository.searchEvaluations(null, sfQuantDrive.getId(), s3.getId(), null)
                .getContent().get(0);
        assertThat(evalS3.getOverallOutcome()).isEqualTo("INELIGIBLE");

        // Student 4 (CGPA pending) -> UNVERIFIABLE for QuantumScale
        EvaluationRecordEntity evalS4 = evaluationRecordRepository.searchEvaluations(null, qsDrive.getId(), s4.getId(), null)
                .getContent().get(0);
        assertThat(evalS4.getOverallOutcome()).isEqualTo("UNVERIFIABLE");
    }

    @Test
    @DisplayName("Verify admin credentials and existing data remain untouched")
    void testAdminPreserved() {
        UserEntity adminBefore = userRepository.findByUsername("admin").orElseThrow();
        String initialPasswordHash = adminBefore.getPasswordHash();

        seeder.seedPlacementDemoData();

        UserEntity adminAfter = userRepository.findByUsername("admin").orElseThrow();
        assertThat(adminAfter.getPasswordHash()).isEqualTo(initialPasswordHash);
        assertThat(adminAfter.getRole().getName()).isEqualTo("ADMIN");
        assertThat(adminAfter.isActive()).isTrue();
    }
}
