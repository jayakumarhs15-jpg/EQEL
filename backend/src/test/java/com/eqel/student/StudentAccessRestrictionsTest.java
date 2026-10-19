package com.eqel.student;

import com.eqel.entity.*;
import com.eqel.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StudentAccessRestrictionsTest {

    @Autowired
    private MockMvc mockMvc;

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

    private Long student1ProfileId;
    private Long student2ProfileId;
    private Long openDriveId;

    @BeforeEach
    void setUp() {
        RoleEntity studentRole = roleRepository.findByName("STUDENT").orElseThrow();

        // Student 1: Alice
        UserEntity u1 = new UserEntity();
        u1.setUsername("alice");
        u1.setEmail("alice@university.edu");
        u1.setPasswordHash("hash1");
        u1.setFullName("Alice Smith");
        u1.setRole(studentRole);
        userRepository.save(u1);

        StudentProfileEntity p1 = new StudentProfileEntity();
        p1.setUser(u1);
        p1.setEnrollmentNumber("ENR-ALICE");
        p1.setCgpa(new BigDecimal("9.10"));
        p1.setBacklogs(0);
        p1.setDepartment("CSE");
        studentProfileRepository.save(p1);
        student1ProfileId = p1.getId();

        // Student 2: Bob
        UserEntity u2 = new UserEntity();
        u2.setUsername("bob");
        u2.setEmail("bob@university.edu");
        u2.setPasswordHash("hash2");
        u2.setFullName("Bob Jones");
        u2.setRole(studentRole);
        userRepository.save(u2);

        StudentProfileEntity p2 = new StudentProfileEntity();
        p2.setUser(u2);
        p2.setEnrollmentNumber("ENR-BOB");
        p2.setCgpa(new BigDecimal("6.20"));
        p2.setBacklogs(2);
        p2.setDepartment("ECE");
        studentProfileRepository.save(p2);
        student2ProfileId = p2.getId();

        // Open Drive
        CompanyEntity comp = new CompanyEntity();
        comp.setName("Enterprise Corp");
        companyRepository.save(comp);

        PlacementDriveEntity d = new PlacementDriveEntity();
        d.setCompany(comp);
        d.setTitle("Graduate Trainee 2026");
        d.setDriveStatus("OPEN");
        placementDriveRepository.save(d);
        openDriveId = d.getId();
    }

    @Test
    @DisplayName("PASS: Student authenticated as 'alice' retrieves only Alice's profile and enrollment")
    @WithMockUser(username = "alice", roles = {"STUDENT"})
    void testAliceRetrievesOnlyHerProfile() throws Exception {
        mockMvc.perform(get("/api/student/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentNumber").value("ENR-ALICE"))
                .andExpect(jsonPath("$.department").value("CSE"))
                .andExpect(jsonPath("$.cgpa").value(9.10));
    }

    @Test
    @DisplayName("PASS: Student authenticated as 'bob' retrieves only Bob's profile")
    @WithMockUser(username = "bob", roles = {"STUDENT"})
    void testBobRetrievesOnlyHisProfile() throws Exception {
        mockMvc.perform(get("/api/student/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentNumber").value("ENR-BOB"))
                .andExpect(jsonPath("$.department").value("ECE"))
                .andExpect(jsonPath("$.backlogs").value(2));
    }

    @Test
    @DisplayName("PASS: Student cannot self-approve evidence records")
    @WithMockUser(username = "alice", roles = {"STUDENT"})
    void testStudentCannotApproveEvidence() throws Exception {
        // Attempting to call placement verification endpoint
        mockMvc.perform(post("/api/placement/evidence/1/verify"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PASS: Student cannot apply for a placement drive without an ELIGIBLE evaluation verdict")
    @WithMockUser(username = "bob", roles = {"STUDENT"})
    void testCannotApplyWithoutEligibleVerdict() throws Exception {
        // Bob has not been evaluated yet
        mockMvc.perform(post("/api/student/applications/" + openDriveId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You cannot apply without a verified ELIGIBLE verdict. Current status: NOT EVALUATED"));
    }
}
