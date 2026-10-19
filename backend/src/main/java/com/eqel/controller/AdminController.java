package com.eqel.controller;

import com.eqel.dto.ApiResponseDto;
import com.eqel.dto.AuthUserDto;
import com.eqel.entity.AuditEventEntity;
import com.eqel.entity.RoleEntity;
import com.eqel.entity.UserEntity;
import com.eqel.repository.AuditEventRepository;
import com.eqel.repository.RoleRepository;
import com.eqel.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditEventRepository auditEventRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.eqel.service.PlacementDemoDataSeeder placementDemoDataSeeder;

    public AdminController(UserRepository userRepository,
                           RoleRepository roleRepository,
                           AuditEventRepository auditEventRepository,
                           PasswordEncoder passwordEncoder,
                           com.eqel.service.PlacementDemoDataSeeder placementDemoDataSeeder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditEventRepository = auditEventRepository;
        this.passwordEncoder = passwordEncoder;
        this.placementDemoDataSeeder = placementDemoDataSeeder;
    }

    public static class CreateUserRequest {
        @NotBlank public String username;
        @NotBlank @Email public String email;
        @NotBlank public String password;
        @NotBlank public String fullName;
        @NotBlank public String role;
    }

    public static class ResetPasswordRequest {
        @NotBlank public String newPassword;
    }

    // ── 1. Users Listing & Search ──
    @GetMapping("/users")
    public ResponseEntity<Page<AuthUserDto>> listUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserEntity> users = userRepository.searchUsers(query, role, pageable);
        return ResponseEntity.ok(users.map(AuthUserDto::new));
    }

    // ── 2. Create User ──
    @PostMapping("/users")
    @Transactional
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request,
                                        Authentication auth,
                                        HttpServletRequest httpRequest) {

        if (userRepository.existsByUsername(request.username)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Username already in use"));
        }
        if (userRepository.existsByEmail(request.email)) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Email already in use"));
        }

        RoleEntity role = roleRepository.findByName(request.role.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role specified: " + request.role));

        UserEntity user = new UserEntity();
        user.setUsername(request.username);
        user.setEmail(request.email);
        user.setFullName(request.fullName);
        user.setPasswordHash(passwordEncoder.encode(request.password));
        user.setRole(role);
        user.setActive(true);

        UserEntity saved = userRepository.save(user);

        // Record Audit Event
        recordAudit(auth, "USER_CREATED", "USER", saved.getId(),
                "Created user " + saved.getUsername() + " with role " + role.getName(), httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthUserDto(saved));
    }

    // ── 3. Toggle User Active Status ──
    @PutMapping("/users/{id}/status")
    @Transactional
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id,
                                              @RequestBody Map<String, Boolean> body,
                                              Authentication auth,
                                              HttpServletRequest httpRequest) {

        UserEntity target = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        boolean newActive = body.getOrDefault("active", !target.isActive());

        // Protect last active administrator
        if (!newActive && "ADMIN".equalsIgnoreCase(target.getRole().getName())) {
            long activeAdmins = userRepository.countByRoleNameAndIsActiveTrue("ADMIN");
            if (activeAdmins <= 1) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Cannot deactivate the sole active Administrator account."));
            }
        }

        target.setActive(newActive);
        userRepository.save(target);

        recordAudit(auth, newActive ? "USER_ACTIVATED" : "USER_DEACTIVATED", "USER", target.getId(),
                (newActive ? "Activated" : "Deactivated") + " user " + target.getUsername(), httpRequest);

        return ResponseEntity.ok(new AuthUserDto(target));
    }

    // ── 4. Reset User Password ──
    @PostMapping("/users/{id}/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@PathVariable Long id,
                                           @Valid @RequestBody ResetPasswordRequest req,
                                           Authentication auth,
                                           HttpServletRequest httpRequest) {

        UserEntity target = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        target.setPasswordHash(passwordEncoder.encode(req.newPassword));
        userRepository.save(target);

        recordAudit(auth, "PASSWORD_RESET", "USER", target.getId(),
                "Reset password for user " + target.getUsername(), httpRequest);

        return ResponseEntity.ok(ApiResponseDto.ok("Password successfully reset", null));
    }

    // ── 5. Audit Logs ──
    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditEventEntity>> getAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(auditEventRepository.searchAuditEvents(action, entityType, pageable));
    }

    // ── 6. Roles & Permissions List ──
    @GetMapping("/roles")
    public ResponseEntity<List<RoleEntity>> getRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    // ── 7. System Health Check ──
    @GetMapping("/system-health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("engineVersion", "1.0.0");
        health.put("database", "CONNECTED");
        health.put("activeAdmins", userRepository.countByRoleNameAndIsActiveTrue("ADMIN"));
        health.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.ok(health);
    }

    // ── 8. Explicit Placement Demo Data Seeding ──
    @PostMapping("/demo/seed-placement")
    public ResponseEntity<?> seedPlacementDemo(Authentication auth, HttpServletRequest httpRequest) {
        com.eqel.service.PlacementDemoDataSeeder.SeedResult result = placementDemoDataSeeder.seedPlacementDemoData();
        recordAudit(auth, "DEMO_DATA_SEEDED", "SYSTEM", null,
                "Seeded placement demo records: " + result.getMessage(), httpRequest);
        return ResponseEntity.ok(result);
    }

    private void recordAudit(Authentication auth, String action, String entityType, Long entityId, String details, HttpServletRequest req) {
        UserEntity actor = null;
        if (auth != null) {
            actor = userRepository.findByUsername(auth.getName()).orElse(null);
        }
        String ip = req != null ? req.getRemoteAddr() : "127.0.0.1";
        auditEventRepository.save(new AuditEventEntity(actor, action, entityType, entityId, details, ip));
    }
}
