package com.eqel.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthorizationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("PASS: Unauthenticated access to /api/admin/users is rejected with 401")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PASS: Student role accessing /api/admin/users is rejected with 403 Forbidden")
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testStudentCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PASS: Student role accessing /api/placement/drives is rejected with 403 Forbidden")
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    void testStudentCannotAccessPlacementEndpoints() throws Exception {
        mockMvc.perform(get("/api/placement/drives"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PASS: Placement Officer accessing /api/admin/audit-logs is rejected with 403 Forbidden")
    @WithMockUser(username = "officer1", roles = {"PLACEMENT_OFFICER"})
    void testPlacementOfficerCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PASS: Admin accessing /api/admin/users is authorized (200 OK)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminAuthorizedForAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PASS: Placement Officer accessing /api/placement/drives is authorized (200 OK)")
    @WithMockUser(username = "officer1", roles = {"PLACEMENT_OFFICER"})
    void testPlacementOfficerAuthorizedForPlacementEndpoints() throws Exception {
        mockMvc.perform(get("/api/placement/drives"))
                .andExpect(status().isOk());
    }
}
