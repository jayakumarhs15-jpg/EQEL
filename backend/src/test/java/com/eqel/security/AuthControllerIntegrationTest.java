package com.eqel.security;

import com.eqel.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("PASS: Admin login with valid credentials creates session")
    void testAdminLoginSuccess() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("admin");
        request.setPassword("Admin@eqel2026");
        request.setRole("ADMIN");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("PASS: Role mismatch rejection (Admin user attempting Student portal login)")
    void testRoleMismatchRejection() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("admin");
        request.setPassword("Admin@eqel2026");
        request.setRole("STUDENT"); // Intentional role mismatch

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied: Account role does not match selected login portal."));
    }

    @Test
    @DisplayName("PASS: Bad credentials returns 401 Unauthorized")
    void testBadCredentialsRejection() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("admin");
        request.setPassword("IncorrectPassword999!");
        request.setRole("ADMIN");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("PASS: Session persistence via /api/auth/me and logout invalidation")
    void testSessionMeAndLogout() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("admin");
        request.setPassword("Admin@eqel2026");
        request.setRole("ADMIN");

        // 1. Login to obtain session
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        // 2. Fetch authenticated user with session
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));

        // 3. Logout
        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk());

        // 4. Verify session is invalidated
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
    }
}
