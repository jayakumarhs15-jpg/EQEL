package com.eqel.placement;

import com.eqel.controller.PlacementController;
import com.eqel.entity.*;
import com.eqel.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PlacementAndEvaluationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PlacementDriveRepository placementDriveRepository;

    @Autowired
    private EligibilityPolicyRepository eligibilityPolicyRepository;

    @Autowired
    private PolicyVersionRepository policyVersionRepository;

    @Autowired
    private DrivePolicyAssignmentRepository drivePolicyAssignmentRepository;

    @Autowired
    private EvaluationRecordRepository evaluationRecordRepository;

    @Test
    @DisplayName("PASS: End-to-end evaluation flow with policy versioning and ledger persistence")
    @WithMockUser(username = "officer", roles = {"PLACEMENT_OFFICER"})
    void testEndToEndEvaluationFlow() throws Exception {
        // 1. Setup Student
        RoleEntity studentRole = roleRepository.findByName("STUDENT").orElseThrow();
        UserEntity studentUser = new UserEntity();
        studentUser.setUsername("teststudent2026");
        studentUser.setEmail("teststudent2026@university.edu");
        studentUser.setPasswordHash("hash");
        studentUser.setFullName("Ananya Sharma");
        studentUser.setRole(studentRole);
        userRepository.save(studentUser);

        StudentProfileEntity profile = new StudentProfileEntity();
        profile.setUser(studentUser);
        profile.setEnrollmentNumber("ENR-2026-TEST");
        profile.setCgpa(new BigDecimal("8.75"));
        profile.setBacklogs(0);
        profile.setDepartment("CSE");
        studentProfileRepository.save(profile);

        // 2. Setup Company & Drive
        CompanyEntity company = new CompanyEntity();
        company.setName("Anthropic Corp");
        company.setIndustry("AI Research");
        companyRepository.save(company);

        PlacementDriveEntity drive = new PlacementDriveEntity();
        drive.setCompany(company);
        drive.setTitle("AI Research Engineer 2026");
        drive.setPackageLpa(new BigDecimal("35.00"));
        drive.setDriveStatus("OPEN");
        placementDriveRepository.save(drive);

        // 3. Setup Versioned Policy
        EligibilityPolicyEntity policy = new EligibilityPolicyEntity();
        policy.setName("High-Tier Research Standard");
        eligibilityPolicyRepository.save(policy);

        PolicyVersionEntity pv = new PolicyVersionEntity();
        pv.setPolicy(policy);
        pv.setVersionNumber(1);
        pv.setNotes("First locked edition");
        pv.setPublished(true);

        PolicyRuleEntity r1 = new PolicyRuleEntity();
        r1.setPolicyVersion(pv);
        r1.setRuleCode("MIN_CGPA");
        r1.setRuleDescription("Minimum 8.00 CGPA");
        r1.setFieldName("CGPA");
        r1.setOperator("GTE");
        r1.setThresholdValue("8.00");
        r1.setMandatory(true);

        PolicyRuleEntity r2 = new PolicyRuleEntity();
        r2.setPolicyVersion(pv);
        r2.setRuleCode("ZERO_BACKLOGS");
        r2.setRuleDescription("Zero backlogs");
        r2.setFieldName("BACKLOGS");
        r2.setOperator("LTE");
        r2.setThresholdValue("0");
        r2.setMandatory(true);

        pv.setRules(List.of(r1, r2));
        policyVersionRepository.save(pv);

        // 4. Assign Policy to Drive
        DrivePolicyAssignmentEntity assignment = new DrivePolicyAssignmentEntity();
        assignment.setDrive(drive);
        assignment.setPolicyVersion(pv);
        drivePolicyAssignmentRepository.save(assignment);

        // 5. Trigger Evaluation API
        PlacementController.EvaluateRequest evalReq = new PlacementController.EvaluateRequest();
        evalReq.studentId = profile.getId();
        evalReq.driveId = drive.getId();

        mockMvc.perform(post("/api/placement/evaluations/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evalReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallOutcome").value("ELIGIBLE"))
                .andExpect(jsonPath("$.decisionEngineVersion").value("1.0.0"))
                .andExpect(jsonPath("$.ruleResults.length()").value(2));

        // 6. Verify Database Persistence in Decision Ledger
        List<EvaluationRecordEntity> records = evaluationRecordRepository.findByStudentId(profile.getId());
        assertEquals(1, records.size(), "Evaluation record must be persisted in ledger");

        EvaluationRecordEntity persisted = records.get(0);
        assertEquals("ELIGIBLE", persisted.getOverallOutcome());
        assertEquals(1, persisted.getPolicyVersion().getVersionNumber(), "Must lock policy version 1");
        assertEquals(2, persisted.getRuleResults().size(), "Must preserve granular rule outcomes");
    }
}
